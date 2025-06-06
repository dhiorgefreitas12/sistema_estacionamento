package br.com.estacionamento.Sistema.de.Estacionamento.service;

import br.com.estacionamento.Sistema.de.Estacionamento.dto.GarageResponse;
import br.com.estacionamento.Sistema.de.Estacionamento.dto.SectorDTO;
import br.com.estacionamento.Sistema.de.Estacionamento.dto.SpotDTO;
import br.com.estacionamento.Sistema.de.Estacionamento.exception.BusinessException;
import br.com.estacionamento.Sistema.de.Estacionamento.model.Sector;
import br.com.estacionamento.Sistema.de.Estacionamento.model.Spot;
import br.com.estacionamento.Sistema.de.Estacionamento.repository.SectorRepository;
import br.com.estacionamento.Sistema.de.Estacionamento.repository.SpotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class GarageServiceTest {

    private SectorRepository sectorRepository;
    private SpotRepository spotRepository;
    private GarageService garageService;

    @BeforeEach
    void setUp() {
        sectorRepository = mock(SectorRepository.class);
        spotRepository = mock(SpotRepository.class);
        garageService = new GarageService(sectorRepository, spotRepository);
    }

    @Test
    void testSaveGarageData_SavesNewSectorAndSpot() {
        SectorDTO sectorDTO = new SectorDTO("A", 10.0, 50, "08:00", "18:00", 120);
        SpotDTO spotDTO = new SpotDTO(1L, "A", 100.0, 200.0);
        GarageResponse response = new GarageResponse(List.of(sectorDTO), List.of(spotDTO));

        when(sectorRepository.existsBySector("A")).thenReturn(false);
        when(spotRepository.existsById(1L)).thenReturn(false);

        garageService.saveGarageData(response);

        verify(sectorRepository).save(any(Sector.class));
        verify(spotRepository).save(any(Spot.class));
    }

    @Test
    void testSaveGarageData_DoesNotSaveIfExists() {
        SectorDTO sectorDTO = new SectorDTO("A", 10.0, 50, "08:00", "18:00", 120);
        SpotDTO spotDTO = new SpotDTO(1L, "A", 100.0, 200.0);
        GarageResponse response = new GarageResponse(List.of(sectorDTO), List.of(spotDTO));

        when(sectorRepository.existsBySector("A")).thenReturn(true);
        when(spotRepository.existsById(1L)).thenReturn(true);

        garageService.saveGarageData(response);

        verify(sectorRepository, never()).save(any(Sector.class));
        verify(spotRepository, never()).save(any(Spot.class));
    }

    @Test
    void testSaveGarageData_ThrowsBusinessExceptionOnSectorSaveFailure() {
        SectorDTO sectorDTO = new SectorDTO("A", 10.0, 50, "08:00", "18:00", 120);
        GarageResponse response = new GarageResponse(List.of(sectorDTO), List.of());

        when(sectorRepository.existsBySector("A")).thenReturn(false);
        doThrow(new RuntimeException("Erro no banco")).when(sectorRepository).save(any(Sector.class));

        assertThrows(BusinessException.class, () -> garageService.saveGarageData(response));
    }

    @Test
    void testSaveGarageData_ThrowsBusinessExceptionOnSpotSaveFailure() {
        SpotDTO spotDTO = new SpotDTO(1L, "A", 100.0, 200.0);
        GarageResponse response = new GarageResponse(List.of(), List.of(spotDTO));

        when(spotRepository.existsById(1L)).thenReturn(false);
        doThrow(new RuntimeException("Erro no banco")).when(spotRepository).save(any(Spot.class));

        assertThrows(BusinessException.class, () -> garageService.saveGarageData(response));
    }
}
