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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class EventServiceTest {

    private ParkingSessionRepository parkingSessionRepository;
    private SpotRepository spotRepository;
    private SectorRepository sectorRepository;
    private EventService eventService;

    @BeforeEach
    void setup() {
        parkingSessionRepository = mock(ParkingSessionRepository.class);
        spotRepository = mock(SpotRepository.class);
        sectorRepository = mock(SectorRepository.class);
        eventService = new EventService();
        eventService.sectorRepository = sectorRepository;
        eventService.parkingSessionRepository = parkingSessionRepository;
        eventService.spotRepository = spotRepository;
    }

    @Test
    void testHandleEntry() {
        WebhookEventDTO dto = new WebhookEventDTO("ABC1234", "2025-06-06T17:00:00", null, 11.0, 22.0, EventType.ENTRY);
        when(parkingSessionRepository.findTopByLicensePlateOrderByEntryTimeDesc("ABC1234")).thenReturn(null);
        Spot spot = new Spot();
        spot.setId(1L);
        spot.setSector("A");
        when(spotRepository.findByLatAndLng(11.0, 22.0)).thenReturn(Optional.of(spot));
        Sector sector = new Sector();
        sector.setSector("A");
        sector.setMaxCapacity(10);
        sector.setBasePrice(10.0);
        when(sectorRepository.findBySector("A")).thenReturn(sector);
        when(spotRepository.countBySectorAndOccupiedTrue("A")).thenReturn(2L);

        eventService.processEvent(dto);

        ArgumentCaptor<ParkingSession> captor = ArgumentCaptor.forClass(ParkingSession.class);
        verify(parkingSessionRepository).save(captor.capture());
        ParkingSession saved = captor.getValue();
        assertEquals("ABC1234", saved.getLicensePlate());
        assertEquals("A", saved.getSector());
        assertEquals(1L, saved.getSpotId());
    }

    @Test
    void testHandleParked() {
        WebhookEventDTO dto = new WebhookEventDTO("ABC1234", null, null, 11.0, 22.0, EventType.PARKED);
        Spot spot = new Spot();
        spot.setId(2L);
        spot.setSector("B");
        when(spotRepository.findByLatAndLng(11.0, 22.0)).thenReturn(Optional.of(spot));
        ParkingSession session = new ParkingSession();
        session.setLicensePlate("ABC1234");
        session.setEntryTime(LocalDateTime.now());
        when(parkingSessionRepository.findTopByLicensePlateOrderByEntryTimeDesc("ABC1234")).thenReturn(session);

        eventService.processEvent(dto);

        verify(parkingSessionRepository).save(any(ParkingSession.class));
        verify(spotRepository).save(spot);
    }

    @Test
    void testHandleExitWithCharge() {
        WebhookEventDTO dto = new WebhookEventDTO("ABC1234", null, "2025-06-06T18:00:00", 0.0, 0.0, EventType.EXIT);
        ParkingSession session = new ParkingSession();
        session.setLicensePlate("ABC1234");
        session.setEntryTime(LocalDateTime.parse("2025-06-06T16:30:00"));
        session.setSector("C");
        session.setSpotId(3L);
        when(parkingSessionRepository.findTopByLicensePlateOrderByEntryTimeDesc("ABC1234")).thenReturn(session);
        Sector sector = new Sector();
        sector.setSector("C");
        sector.setBasePrice(12.0);
        when(sectorRepository.findBySector("C")).thenReturn(sector);
        Spot spot = new Spot();
        spot.setId(3L);
        when(spotRepository.findById(3L)).thenReturn(Optional.of(spot));

        eventService.processEvent(dto);

        verify(parkingSessionRepository).save(session);
        verify(spotRepository).save(spot);
        assertEquals(LocalDateTime.parse("2025-06-06T18:00:00"), session.getExitTime());
    }

    @Test
    void testHandleExitWithoutCharge() {
        WebhookEventDTO dto = new WebhookEventDTO("ABC1234", null, "2025-06-06T18:00:00", 0.0, 0.0, EventType.EXIT);
        ParkingSession session = new ParkingSession();
        session.setLicensePlate("ABC1234");
        session.setEntryTime(LocalDateTime.parse("2025-06-06T18:00:00"));
        session.setSector("D");
        when(parkingSessionRepository.findTopByLicensePlateOrderByEntryTimeDesc("ABC1234")).thenReturn(session);
        Sector sector = new Sector();
        sector.setSector("D");
        sector.setBasePrice(10.0);
        when(sectorRepository.findBySector("D")).thenReturn(sector);

        eventService.processEvent(dto);

        assertEquals(0.0, session.getPrice());
        verify(parkingSessionRepository).save(session);
    }

    @Test
    void testProcessEvent_NullEvent_ThrowsException() {
        assertThrows(BusinessException.class, () -> eventService.processEvent(null));
    }

    @Test
    void testHandleEntry_SectorNotFound_ThrowsException() {
        WebhookEventDTO dto = new WebhookEventDTO("ABC1234", "2025-06-06T17:00:00", null, 11.0, 22.0, EventType.ENTRY);
        when(parkingSessionRepository.findTopByLicensePlateOrderByEntryTimeDesc("ABC1234")).thenReturn(null);
        Spot spot = new Spot();
        spot.setId(1L);
        spot.setSector("A");
        when(spotRepository.findByLatAndLng(11.0, 22.0)).thenReturn(Optional.of(spot));
        when(sectorRepository.findBySector("A")).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> eventService.processEvent(dto));
    }

    @Test
    void testHandleEntry_SectorLotado_ThrowsException() {
        WebhookEventDTO dto = new WebhookEventDTO("ABC1234", "2025-06-06T17:00:00", null, 11.0, 22.0, EventType.ENTRY);
        when(parkingSessionRepository.findTopByLicensePlateOrderByEntryTimeDesc("ABC1234")).thenReturn(null);
        Spot spot = new Spot();
        spot.setId(1L);
        spot.setSector("A");
        when(spotRepository.findByLatAndLng(11.0, 22.0)).thenReturn(Optional.of(spot));
        Sector sector = new Sector();
        sector.setSector("A");
        sector.setMaxCapacity(10);
        sector.setBasePrice(20.0);
        when(sectorRepository.findBySector("A")).thenReturn(sector);
        when(spotRepository.countBySectorAndOccupiedTrue("A")).thenReturn(10L);

        assertThrows(BusinessException.class, () -> eventService.processEvent(dto));
    }

    @Test
    void testHandleEntry_SpotNotFound_ThrowsException() {
        WebhookEventDTO dto = new WebhookEventDTO("ABC1234", "2025-06-06T17:00:00", null, 99.9, 99.9, EventType.ENTRY);
        when(parkingSessionRepository.findTopByLicensePlateOrderByEntryTimeDesc("ABC1234")).thenReturn(null);
        when(spotRepository.findByLatAndLng(99.9, 99.9)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> eventService.processEvent(dto));
    }

    @Test
    void shouldApply90PercentPrice_WhenOccupancyIsBelow25Percent() {
        WebhookEventDTO dto = new WebhookEventDTO("ABC1234", "2025-06-06T17:00:00", null, 1.0, 1.0, EventType.ENTRY);

        when(parkingSessionRepository.findTopByLicensePlateOrderByEntryTimeDesc("ABC1234")).thenReturn(null);

        Spot spot = new Spot();
        spot.setId(1L);
        spot.setSector("A");
        when(spotRepository.findByLatAndLng(1.0, 1.0)).thenReturn(Optional.of(spot));

        Sector sector = new Sector();
        sector.setSector("A");
        sector.setMaxCapacity(10);
        sector.setBasePrice(20.0);
        when(sectorRepository.findBySector("A")).thenReturn(sector);
        when(spotRepository.countBySectorAndOccupiedTrue("A")).thenReturn(2L); // 20%

        eventService.processEvent(dto);

        ArgumentCaptor<ParkingSession> captor = ArgumentCaptor.forClass(ParkingSession.class);
        verify(parkingSessionRepository).save(captor.capture());
        assertEquals(18.0, captor.getValue().getPrice());
    }

    @Test
    void shouldApplyBasePrice_WhenOccupancyIsBetween25And50Percent() {
        WebhookEventDTO dto = new WebhookEventDTO("ABC1234", "2025-06-06T17:00:00", null, 1.0, 1.0, EventType.ENTRY);

        when(parkingSessionRepository.findTopByLicensePlateOrderByEntryTimeDesc("ABC1234")).thenReturn(null);

        Spot spot = new Spot();
        spot.setId(1L);
        spot.setSector("B");
        when(spotRepository.findByLatAndLng(1.0, 1.0)).thenReturn(Optional.of(spot));

        Sector sector = new Sector();
        sector.setSector("B");
        sector.setMaxCapacity(10);
        sector.setBasePrice(20.0);
        when(sectorRepository.findBySector("B")).thenReturn(sector);
        when(spotRepository.countBySectorAndOccupiedTrue("B")).thenReturn(4L);

        eventService.processEvent(dto);

        ArgumentCaptor<ParkingSession> captor = ArgumentCaptor.forClass(ParkingSession.class);
        verify(parkingSessionRepository).save(captor.capture());
        assertEquals(20.0, captor.getValue().getPrice());
    }

    @Test
    void shouldApply110PercentPrice_WhenOccupancyIsBetween50And75Percent() {
        WebhookEventDTO dto = new WebhookEventDTO("ABC1234", "2025-06-06T17:00:00", null, 1.0, 1.0, EventType.ENTRY);

        when(parkingSessionRepository.findTopByLicensePlateOrderByEntryTimeDesc("ABC1234")).thenReturn(null);

        Spot spot = new Spot();
        spot.setId(1L);
        spot.setSector("C");
        when(spotRepository.findByLatAndLng(1.0, 1.0)).thenReturn(Optional.of(spot));

        Sector sector = new Sector();
        sector.setSector("C");
        sector.setMaxCapacity(10);
        sector.setBasePrice(20.0);
        when(sectorRepository.findBySector("C")).thenReturn(sector);
        when(spotRepository.countBySectorAndOccupiedTrue("C")).thenReturn(6L);

        eventService.processEvent(dto);

        ArgumentCaptor<ParkingSession> captor = ArgumentCaptor.forClass(ParkingSession.class);
        verify(parkingSessionRepository).save(captor.capture());
        assertEquals(22.0, captor.getValue().getPrice());
    }

    @Test
    void shouldApply125PercentPrice_WhenOccupancyIs75PercentOrMore() {
        WebhookEventDTO dto = new WebhookEventDTO("ABC1234", "2025-06-06T17:00:00", null, 1.0, 1.0, EventType.ENTRY);

        when(parkingSessionRepository.findTopByLicensePlateOrderByEntryTimeDesc("ABC1234")).thenReturn(null);

        Spot spot = new Spot();
        spot.setId(1L);
        spot.setSector("D");
        when(spotRepository.findByLatAndLng(1.0, 1.0)).thenReturn(Optional.of(spot));

        Sector sector = new Sector();
        sector.setSector("D");
        sector.setMaxCapacity(10);
        sector.setBasePrice(20.0);
        when(sectorRepository.findBySector("D")).thenReturn(sector);
        when(spotRepository.countBySectorAndOccupiedTrue("D")).thenReturn(8L);

        eventService.processEvent(dto);

        ArgumentCaptor<ParkingSession> captor = ArgumentCaptor.forClass(ParkingSession.class);
        verify(parkingSessionRepository).save(captor.capture());
        assertEquals(25.0, captor.getValue().getPrice());
    }

}
