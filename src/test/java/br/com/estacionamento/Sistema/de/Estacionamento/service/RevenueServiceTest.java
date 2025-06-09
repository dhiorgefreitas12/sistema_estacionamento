package br.com.estacionamento.Sistema.de.Estacionamento.service;

import br.com.estacionamento.Sistema.de.Estacionamento.dto.RevenueRequestDTO;
import br.com.estacionamento.Sistema.de.Estacionamento.dto.RevenueResponseDTO;
import br.com.estacionamento.Sistema.de.Estacionamento.exception.BusinessException;
import br.com.estacionamento.Sistema.de.Estacionamento.exception.ResourceNotFoundException;
import br.com.estacionamento.Sistema.de.Estacionamento.model.ParkingSession;
import br.com.estacionamento.Sistema.de.Estacionamento.repository.ParkingSessionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RevenueServiceTest {

    @Mock
    private ParkingSessionRepository parkingSessionRepository;

    @InjectMocks
    private RevenueService revenueService;

    @Test
    void shouldCalculateRevenueForSectorAndDate() {
        LocalDate date = LocalDate.of(2025, 6, 7);
        String sector = "A";

        ParkingSession session1 = new ParkingSession();
        session1.setPrice(36.45);
        session1.setExitTime(LocalDateTime.of(2025, 6, 7, 13, 0));

        ParkingSession session2 = new ParkingSession();
        session2.setPrice(36.45);
        session2.setExitTime(LocalDateTime.of(2025, 6, 7, 13, 0));

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);

        when(parkingSessionRepository.findAllBySectorAndExitTimeBetween(
                eq(sector), eq(startOfDay), eq(endOfDay)
        )).thenReturn(List.of(session1, session2));

        RevenueRequestDTO dto = new RevenueRequestDTO(date.toString(), sector);
        RevenueResponseDTO result = revenueService.getRevenue(dto);

        assertEquals("BRL", result.currency());
        assertEquals(72.90, result.amount(), 0.01);
        assertNotNull(result.timestamp());
    }

    @Test
    void shouldThrowExceptionForEmptySessionList() {
        LocalDate date = LocalDate.of(2025, 6, 7);
        String sector = "A";

        when(parkingSessionRepository.findAllBySectorAndExitTimeBetween(
                eq(sector), eq(date.atStartOfDay()), eq(date.plusDays(1).atStartOfDay().minusNanos(1))
        )).thenReturn(Collections.emptyList());

        RevenueRequestDTO dto = new RevenueRequestDTO(date.toString(), sector);

        assertThrows(ResourceNotFoundException.class, () -> revenueService.getRevenue(dto));
    }

    @Test
    void shouldThrowExceptionForNullDate() {
        RevenueRequestDTO dto = new RevenueRequestDTO(null, "A");
        assertThrows(BusinessException.class, () -> revenueService.getRevenue(dto));
    }

    @Test
    void shouldThrowExceptionForInvalidDateFormat() {
        RevenueRequestDTO dto = new RevenueRequestDTO("07-06-2025", "A");
        assertThrows(BusinessException.class, () -> revenueService.getRevenue(dto));
    }

    @Test
    void shouldThrowExceptionForEmptySector() {
        RevenueRequestDTO dto = new RevenueRequestDTO("2025-06-07", " ");
        assertThrows(BusinessException.class, () -> revenueService.getRevenue(dto));
    }

    @Test
    void shouldThrowExceptionForNullDTO() {
        assertThrows(BusinessException.class, () -> revenueService.getRevenue(null));
    }
}
