package br.com.estacionamento.Sistema.de.Estacionamento.service;

import br.com.estacionamento.Sistema.de.Estacionamento.dto.SpotStatusRequestDTO;
import br.com.estacionamento.Sistema.de.Estacionamento.dto.SpotStatusResponseDTO;
import br.com.estacionamento.Sistema.de.Estacionamento.model.ParkingSession;
import br.com.estacionamento.Sistema.de.Estacionamento.model.Spot;
import br.com.estacionamento.Sistema.de.Estacionamento.repository.ParkingSessionRepository;
import br.com.estacionamento.Sistema.de.Estacionamento.repository.SpotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class SpotStatusService {

    @Autowired
    private SpotRepository spotRepository;

    @Autowired
    private ParkingSessionRepository parkingSessionRepository;

    public SpotStatusResponseDTO getStatusByCoordinates(SpotStatusRequestDTO dto) {
        Optional<Spot> optionalSpot = spotRepository.findByLatAndLng(dto.lat(), dto.lng());

        if (optionalSpot.isEmpty()) {
            return new SpotStatusResponseDTO(false, null, null);
        }

        Spot spot = optionalSpot.get();


        ParkingSession session = parkingSessionRepository.findTopBySpotIdAndExitTimeIsNull(spot.getId());

        if (session == null) {
            return new SpotStatusResponseDTO(false, null, null);
        }

        LocalDateTime now = LocalDateTime.now();

        return new SpotStatusResponseDTO(
                true,
                session.getEntryTime(),
                now
        );
    }
}
