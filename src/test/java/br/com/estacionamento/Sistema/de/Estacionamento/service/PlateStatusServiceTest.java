package br.com.estacionamento.Sistema.de.Estacionamento.service;

import br.com.estacionamento.Sistema.de.Estacionamento.dto.PlateStatusResponseDTO;
import br.com.estacionamento.Sistema.de.Estacionamento.exception.ResourceNotFoundException;
import br.com.estacionamento.Sistema.de.Estacionamento.model.ParkingSession;
import br.com.estacionamento.Sistema.de.Estacionamento.model.Sector;
import br.com.estacionamento.Sistema.de.Estacionamento.repository.ParkingSessionRepository;
import br.com.estacionamento.Sistema.de.Estacionamento.repository.SectorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class PlateStatusServiceTest {

    @Mock
    private ParkingSessionRepository parkingSessionRepository;

    @Mock
    private SectorRepository sectorRepository;

    @InjectMocks
    private PlateStatusService plateStatusService;

    private final String PLATE = "ABC1234";
    private final String SECTOR = "A";
    private final double BASE_PRICE = 10.0;
    private final LocalDateTime FIXED_NOW = LocalDateTime.of(2025, 6, 6, 12, 0);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCalculatePriceAfterGracePeriod() {
        ParkingSession session = new ParkingSession();
        session.setLicensePlate(PLATE);
        session.setEntryTime(FIXED_NOW.minusMinutes(45));
        session.setSector(SECTOR);
        session.setPrice(BASE_PRICE);

        Sector sector = new Sector();
        sector.setSector(SECTOR);
        sector.setBasePrice(BASE_PRICE);

        when(parkingSessionRepository.findTopByLicensePlateOrderByEntryTimeDesc(PLATE))
                .thenReturn(session);

        when(sectorRepository.findBySector(SECTOR))
                .thenReturn(sector);

        PlateStatusResponseDTO response = plateStatusService.getStatusByLicensePlate(PLATE);

        assertTrue(response.price_until_now() > 0.0);
        assertEquals(PLATE, response.license_plate());
        assertEquals(session.getEntryTime(), response.entry_time());
        assertNotNull(response.time_parked());
    }

    @Test
    void shouldThrowExceptionWhenSessionNotFound() {
        when(parkingSessionRepository.findTopByLicensePlateOrderByEntryTimeDesc(PLATE))
                .thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> {
            plateStatusService.getStatusByLicensePlate(PLATE);
        });
    }

    @Test
    void shouldThrowExceptionIfSessionHasExitTime() {
        ParkingSession session = new ParkingSession();
        session.setLicensePlate(PLATE);
        session.setEntryTime(FIXED_NOW.minusMinutes(60));
        session.setExitTime(FIXED_NOW.minusMinutes(5));

        when(parkingSessionRepository.findTopByLicensePlateOrderByEntryTimeDesc(PLATE))
                .thenReturn(session);

        assertThrows(ResourceNotFoundException.class, () -> {
            plateStatusService.getStatusByLicensePlate(PLATE);
        });
    }

    @Test
    void shouldThrowExceptionIfSectorNotFound() {
        ParkingSession session = new ParkingSession();
        session.setLicensePlate(PLATE);
        session.setEntryTime(FIXED_NOW.minusMinutes(40));
        session.setSector(SECTOR);
        session.setPrice(BASE_PRICE);

        when(parkingSessionRepository.findTopByLicensePlateOrderByEntryTimeDesc(PLATE))
                .thenReturn(session);

        when(sectorRepository.findBySector(SECTOR))
                .thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> {
            plateStatusService.getStatusByLicensePlate(PLATE);
        });
    }
}
