package br.com.estacionamento.Sistema.de.Estacionamento.service;

import br.com.estacionamento.Sistema.de.Estacionamento.dto.RevenueRequestDTO;
import br.com.estacionamento.Sistema.de.Estacionamento.dto.RevenueResponseDTO;
import br.com.estacionamento.Sistema.de.Estacionamento.exception.BusinessException;
import br.com.estacionamento.Sistema.de.Estacionamento.exception.ResourceNotFoundException;
import br.com.estacionamento.Sistema.de.Estacionamento.model.ParkingSession;
import br.com.estacionamento.Sistema.de.Estacionamento.repository.ParkingSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
public class RevenueService {

    @Autowired
    private ParkingSessionRepository repository;

    public RevenueResponseDTO getRevenue(RevenueRequestDTO dto) {
        if (dto == null || dto.date() == null || dto.date().isBlank()) {
            throw new BusinessException("data nao pode ser nula ou vazia.");
        }

        if (dto.sector() == null || dto.sector().isBlank()) {
            throw new BusinessException("setor nao pode ser nulo ou vazio.");
        }

        LocalDate date;
        try {
            date = LocalDate.parse(dto.date());
        } catch (DateTimeParseException e) {
            throw new BusinessException("data em formato invalido. Use o formato yyyy-MM-dd.");
        }

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);

        List<ParkingSession> sessions = repository
                .findAllBySectorAndExitTimeBetween(dto.sector(), startOfDay, endOfDay);

        if (sessions.isEmpty()) {
            throw new ResourceNotFoundException("nenhuma sessao encontrada para o setor informado na data.");
        }

        double total = sessions.stream()
                .mapToDouble(ParkingSession::getPrice)
                .sum();

        return new RevenueResponseDTO(
                Math.round(total * 100.0) / 100.0,
                "BRL",
                LocalDateTime.now()
        );
    }
}
