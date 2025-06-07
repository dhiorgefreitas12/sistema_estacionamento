package br.com.estacionamento.Sistema.de.Estacionamento.service;

import br.com.estacionamento.Sistema.de.Estacionamento.dto.PlateStatusResponseDTO;
import br.com.estacionamento.Sistema.de.Estacionamento.exception.ResourceNotFoundException;
import br.com.estacionamento.Sistema.de.Estacionamento.model.ParkingSession;
import br.com.estacionamento.Sistema.de.Estacionamento.model.Sector;
import br.com.estacionamento.Sistema.de.Estacionamento.repository.ParkingSessionRepository;
import br.com.estacionamento.Sistema.de.Estacionamento.repository.SectorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class PlateStatusService {

    @Autowired
    private ParkingSessionRepository parkingSessionRepository;

    @Autowired
    private SectorRepository sectorRepository;

    public PlateStatusResponseDTO getStatusByLicensePlate(String plate) {
        ParkingSession session = parkingSessionRepository
                .findTopByLicensePlateOrderByEntryTimeDesc(plate);

        if (session == null || session.getExitTime() != null) {
            throw new ResourceNotFoundException("sessao ativa nao encontrada para a placa: " + plate);
        }

        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(session.getEntryTime(), now);
        long totalMinutes = duration.toMinutes();

        double price = 0.0;
        if (totalMinutes > 15) {
            Sector sector = sectorRepository.findBySector(session.getSector());
            if (sector == null) {
                throw new ResourceNotFoundException("setor '" + session.getSector() + "' nao encontrado.");
            }

            double hourlyRate = sector.getBasePrice();

            long chargeableMinutes = totalMinutes - 15;

            if (chargeableMinutes <= 60) {
                price = hourlyRate;
            } else {
                long extraMinutes = chargeableMinutes - 60;
                double quarterRate = hourlyRate / 4.0;
                long additionalBlocks = (long) Math.ceil(extraMinutes / 15.0);
                price = hourlyRate + (additionalBlocks * quarterRate);
            }

            price = Math.round(price * 100.0) / 100.0;
        }

        return new PlateStatusResponseDTO(
                plate,
                price,
                session.getEntryTime(),
                now
        );
    }

}
