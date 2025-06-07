package br.com.estacionamento.Sistema.de.Estacionamento.service;

import br.com.estacionamento.Sistema.de.Estacionamento.dto.WebhookEventDTO;
import br.com.estacionamento.Sistema.de.Estacionamento.enums.EventType;
import br.com.estacionamento.Sistema.de.Estacionamento.exception.BusinessException;
import br.com.estacionamento.Sistema.de.Estacionamento.exception.ResourceNotFoundException;
import br.com.estacionamento.Sistema.de.Estacionamento.model.ParkingSession;
import br.com.estacionamento.Sistema.de.Estacionamento.model.Sector;
import br.com.estacionamento.Sistema.de.Estacionamento.model.Spot;
import br.com.estacionamento.Sistema.de.Estacionamento.repository.ParkingSessionRepository;
import br.com.estacionamento.Sistema.de.Estacionamento.repository.SectorRepository;
import br.com.estacionamento.Sistema.de.Estacionamento.repository.SpotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class EventService {

    @Autowired
    ParkingSessionRepository parkingSessionRepository;

    @Autowired
    SpotRepository spotRepository;

    @Autowired
    SectorRepository sectorRepository;

    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    public void processEvent(WebhookEventDTO event) {
        if (event == null || event.event_type() == null || event.license_plate() == null) {
            throw new BusinessException("evento, tipo de evento ou placa nao podem ser nulos.");
        }

        EventType type = event.event_type();
        String plate = event.license_plate();

        switch (type) {
            case ENTRY -> handleEntry(event, plate);
            case PARKED -> handleParked(event, plate);
            case EXIT -> handleExit(event, plate);
            default -> throw new BusinessException("tipo de evento desconhecido: " + type);
        }
    }

    private void handleEntry(WebhookEventDTO event, String plate) {
        ParkingSession lastSession = parkingSessionRepository.findTopByLicensePlateOrderByEntryTimeDesc(plate);
        if (lastSession != null && lastSession.getExitTime() == null) return;

        Spot spot = spotRepository.findByLatAndLng(event.lat(), event.lng())
                .orElseThrow(() -> new ResourceNotFoundException("vaga nao encontrada para as coordenadas informadas."));

        Sector sector = sectorRepository.findBySector(spot.getSector());
        if (sector == null) {
            throw new ResourceNotFoundException("setor '" + spot.getSector() + "' nao encontrado.");
        }

        long totalVagas = sector.getMaxCapacity();
        long ocupadas = spotRepository.countBySectorAndOccupyTrue(sector.getSector());
        double ocupacao = (double) ocupadas / totalVagas;

        if (ocupadas >= totalVagas) {
            throw new BusinessException("setor " + sector.getSector() + " esta com 100% de lotacao. Entrada não permitida.");
        }

        double basePrice = sector.getBasePrice();
        if (ocupacao < 0.25) {
            basePrice *= 0.9;
        } else if (ocupacao < 0.5) {
            basePrice *= 1.0;
        } else if (ocupacao < 0.75) {
            basePrice *= 1.1;
        } else {
            basePrice *= 1.25;
        }

        spot.setOccupy(true);
        spotRepository.save(spot);

        ParkingSession session = new ParkingSession();
        session.setLicensePlate(plate);
        session.setEntryTime(LocalDateTime.parse(event.entry_time(), formatter));
        session.setSector(spot.getSector());
        session.setSpotId(spot.getId());
        session.setPrice(basePrice);

        parkingSessionRepository.save(session);
    }

    private void handleParked(WebhookEventDTO event, String plate) {
        Spot spot = spotRepository.findByLatAndLng(event.lat(), event.lng())
                .orElseThrow(() -> new ResourceNotFoundException("vaga nao encontrada para as coordenadas informadas."));

        spot.setOccupy(true);
        spotRepository.save(spot);

        ParkingSession session = parkingSessionRepository.findTopByLicensePlateOrderByEntryTimeDesc(plate);
        if (session != null && session.getExitTime() == null) {
            session.setSector(spot.getSector());
            session.setSpotId(spot.getId());
            parkingSessionRepository.save(session);
        }
    }

    private void handleExit(WebhookEventDTO event, String plate) {
        ParkingSession session = parkingSessionRepository.findTopByLicensePlateOrderByEntryTimeDesc(plate);
        if (session == null || session.getExitTime() != null) return;

        LocalDateTime entryTime = session.getEntryTime();
        LocalDateTime exitTime = LocalDateTime.parse(event.exit_time(), formatter);
        session.setExitTime(exitTime);

        long minutes = Duration.between(entryTime, exitTime).toMinutes();
        double price = 0.0;

        if (minutes > 15) {
            Sector sector = sectorRepository.findBySector(session.getSector());
            if (sector == null) {
                throw new ResourceNotFoundException("setor '" + session.getSector() + "' nao encontrado para calculo de preco.");
            }
            price = calculatePrice(minutes, session.getPrice());
        }

        session.setPrice(price);

        if (session.getSpotId() != null) {
            spotRepository.findById(session.getSpotId()).ifPresent(spot -> {
                spot.setOccupy(false);
                spotRepository.save(spot);
            });
        }

        parkingSessionRepository.save(session);
    }

    private double calculatePrice(long totalMinutes, double basePrice) {
        long chargeable = totalMinutes - 15;
        if (chargeable <= 0) return 0.0;

        double hourPrice = basePrice;
        double prorated = (chargeable / 15.0) * (hourPrice / 4.0);
        return Math.round(prorated * 100.0) / 100.0;
    }
}
