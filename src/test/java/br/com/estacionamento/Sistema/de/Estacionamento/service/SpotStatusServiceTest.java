package br.com.estacionamento.Sistema.de.Estacionamento.service;

import br.com.estacionamento.Sistema.de.Estacionamento.dto.SpotStatusRequestDTO;
import br.com.estacionamento.Sistema.de.Estacionamento.dto.SpotStatusResponseDTO;
import br.com.estacionamento.Sistema.de.Estacionamento.model.ParkingSession;
import br.com.estacionamento.Sistema.de.Estacionamento.model.Spot;
import br.com.estacionamento.Sistema.de.Estacionamento.repository.ParkingSessionRepository;
import br.com.estacionamento.Sistema.de.Estacionamento.repository.SpotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class SpotStatusServiceTest {

    @Mock
    private SpotRepository spotRepository;

    @Mock
    private ParkingSessionRepository parkingSessionRepository;

    @InjectMocks
    private SpotStatusService spotStatusService;

    private final double LAT = -23.561684;
    private final double LNG = -46.655981;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnOccupiedTrueWhenSessionExists() {
        Spot spot = new Spot();
        spot.setId(1L);

        ParkingSession session = new ParkingSession();
        session.setSpotId(1L);
        session.setEntryTime(LocalDateTime.of(2025, 6, 7, 8, 0));

        when(spotRepository.findByLatAndLng(LAT, LNG)).thenReturn(Optional.of(spot));
        when(parkingSessionRepository.findTopBySpotIdAndExitTimeIsNull(1L)).thenReturn(session);

        SpotStatusRequestDTO request = new SpotStatusRequestDTO(LAT, LNG);
        SpotStatusResponseDTO response = spotStatusService.getStatusByCoordinates(request);

        assertTrue(response.occupy());
        assertEquals(session.getEntryTime(), response.entry_time());
        assertNotNull(response.time_parked());
    }

    @Test
    void shouldReturnOccupiedFalseWhenNoSession() {
        Spot spot = new Spot();
        spot.setId(2L);

        when(spotRepository.findByLatAndLng(LAT, LNG)).thenReturn(Optional.of(spot));
        when(parkingSessionRepository.findTopBySpotIdAndExitTimeIsNull(2L)).thenReturn(null);

        SpotStatusRequestDTO request = new SpotStatusRequestDTO(LAT, LNG);
        SpotStatusResponseDTO response = spotStatusService.getStatusByCoordinates(request);

        assertFalse(response.occupy());
        assertNull(response.entry_time());
        assertNull(response.time_parked());
    }

    @Test
    void shouldReturnOccupiedFalseWhenSpotNotFound() {
        when(spotRepository.findByLatAndLng(LAT, LNG)).thenReturn(Optional.empty());

        SpotStatusRequestDTO request = new SpotStatusRequestDTO(LAT, LNG);
        SpotStatusResponseDTO response = spotStatusService.getStatusByCoordinates(request);

        assertFalse(response.occupy());
        assertNull(response.entry_time());
        assertNull(response.time_parked());
    }

}
