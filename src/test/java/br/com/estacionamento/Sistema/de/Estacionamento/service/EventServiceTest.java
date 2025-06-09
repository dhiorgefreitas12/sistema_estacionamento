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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class EventServiceTest {

    @InjectMocks
    private EventService eventService;

    @Mock
    private ParkingSessionRepository parkingSessionRepository;

    @Mock
    private SpotRepository spotRepository;

    @Mock
    private SectorRepository sectorRepository;

    private static final String PLATE = "ABC1234";
    private static final double LAT = -23.0;
    private static final double LNG = -46.0;
    private static final String TIME = LocalDateTime.now().toString();

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    private WebhookEventDTO createEvent(EventType type, String entry, String exit) {
        return new WebhookEventDTO(PLATE, entry, exit, LAT, LNG, type);
    }

    @Test
    void shouldProcessEntrySuccessfully() {
        WebhookEventDTO event = createEvent(EventType.ENTRY, TIME, TIME);

        when(parkingSessionRepository.existsByLicensePlateAndExitTimeIsNull(PLATE)).thenReturn(false);

        eventService.processEvent(event);

        verify(parkingSessionRepository).save(any(ParkingSession.class));
    }

    @Test
    void shouldThrowWhenSectorDoesNotExist() {
        WebhookEventDTO event = createEvent(EventType.PARKED, TIME, TIME);

        Spot spot = new Spot();
        spot.setId(1L);
        spot.setSector("Z");

        when(spotRepository.findByLatAndLng(LAT, LNG)).thenReturn(Optional.of(spot));
        when(sectorRepository.findBySector("Z")).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> eventService.processEvent(event));
    }

    @Test
    void shouldProcessParkedSuccessfully() {
        WebhookEventDTO event = createEvent(EventType.PARKED, TIME, TIME);

        Spot spot = new Spot();
        spot.setId(1L);
        spot.setSector("A");

        Sector sector = new Sector();
        sector.setSector("A");
        sector.setMaxCapacity(10);
        sector.setBasePrice(10.0);

        ParkingSession session = new ParkingSession();
        session.setLicensePlate(PLATE);

        when(spotRepository.findByLatAndLng(LAT, LNG)).thenReturn(Optional.of(spot));
        when(sectorRepository.findBySector("A")).thenReturn(sector);
        when(spotRepository.countBySectorAndOccupyTrue("A")).thenReturn(4L);
        when(parkingSessionRepository.findTopByLicensePlateOrderByEntryTimeDesc(PLATE)).thenReturn(session);

        eventService.processEvent(event);

        verify(spotRepository).save(spot);
        verify(parkingSessionRepository).save(session);
    }

    @Test
    void shouldProcessExitSuccessfully() {
        LocalDateTime entry = LocalDateTime.now().minusMinutes(40);
        LocalDateTime exit = LocalDateTime.now();

        WebhookEventDTO event = createEvent(EventType.EXIT, entry.toString(), exit.toString());

        ParkingSession session = new ParkingSession();
        session.setLicensePlate(PLATE);
        session.setEntryTime(entry);
        session.setSector("C");
        session.setPrice(10.0);
        session.setSpotId(1L);

        Spot spot = new Spot();
        spot.setId(1L);

        Sector sector = new Sector();
        sector.setSector("C");

        when(parkingSessionRepository.findTopByLicensePlateOrderByEntryTimeDesc(PLATE)).thenReturn(session);
        when(sectorRepository.findBySector("C")).thenReturn(sector);
        when(spotRepository.findById(1L)).thenReturn(Optional.of(spot));

        eventService.processEvent(event);

        verify(parkingSessionRepository).save(session);
        verify(spotRepository).save(spot);
        assertNotNull(session.getExitTime());
    }

    @Test
    void shouldThrowWhenEventIsInvalid() {
        WebhookEventDTO event = new WebhookEventDTO(null, null, null, null, null, null);
        assertThrows(BusinessException.class, () -> eventService.processEvent(event));
    }

    @Test
    void shouldThrowWhenEventTypeIsUnknown() {
        WebhookEventDTO event = new WebhookEventDTO(PLATE, TIME, TIME, LAT, LNG, null);
        assertThrows(BusinessException.class, () -> eventService.processEvent(event));
    }
}
