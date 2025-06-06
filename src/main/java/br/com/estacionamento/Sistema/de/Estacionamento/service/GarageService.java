package br.com.estacionamento.Sistema.de.Estacionamento.service;

import br.com.estacionamento.Sistema.de.Estacionamento.dto.GarageResponse;
import br.com.estacionamento.Sistema.de.Estacionamento.exception.BusinessException;
import br.com.estacionamento.Sistema.de.Estacionamento.model.Sector;
import br.com.estacionamento.Sistema.de.Estacionamento.model.Spot;
import br.com.estacionamento.Sistema.de.Estacionamento.repository.SectorRepository;
import br.com.estacionamento.Sistema.de.Estacionamento.repository.SpotRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class GarageService {

    private static final Logger logger = LoggerFactory.getLogger(GarageService.class);

    private final SectorRepository sectorRepository;
    private final SpotRepository spotRepository;

    public GarageService(SectorRepository sectorRepository, SpotRepository spotRepository) {
        this.sectorRepository = sectorRepository;
        this.spotRepository = spotRepository;
    }

    public void saveGarageData(GarageResponse response) {
        try {
            response.garage().forEach(dto -> {
                try {
                    if (!sectorRepository.existsBySector(dto.sector())) {
                        Sector sector = new Sector();
                        sector.setSector(dto.sector());
                        sector.setBasePrice(dto.basePrice());
                        sector.setMaxCapacity(dto.maxCapacity());
                        sector.setOpenHour(dto.openHour());
                        sector.setCloseHour(dto.closeHour());
                        sector.setDurationLimitMinutes(dto.durationLimitMinutes());
                        sectorRepository.save(sector);
                    }
                } catch (Exception e) {
                    logger.error("Erro ao salvar setor: {}", dto.sector(), e);
                    throw new BusinessException("Erro ao salvar setor: " + dto.sector());
                }
            });

            response.spots().forEach(dto -> {
                try {
                    if (!spotRepository.existsById(dto.id())) {
                        Spot spot = new Spot();
                        spot.setId(dto.id());
                        spot.setSector(dto.sector());
                        spot.setLat(dto.lat());
                        spot.setLng(dto.lng());
                        spot.setOccupied(false);
                        spotRepository.save(spot);
                    }
                } catch (Exception e) {
                    logger.error("Erro ao salvar vaga: {}", dto.id(), e);
                    throw new BusinessException("Erro ao salvar vaga: " + dto.id());
                }
            });

        } catch (Exception e) {
            logger.error("Erro geral ao processar dados da garagem", e);
            throw new BusinessException("Erro ao processar dados da garagem");
        }
    }
}
