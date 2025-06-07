package br.com.estacionamento.Sistema.de.Estacionamento.repository;

import br.com.estacionamento.Sistema.de.Estacionamento.model.ParkingSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingSessionRepository extends JpaRepository<ParkingSession, Long> {
    ParkingSession findTopByLicensePlateOrderByEntryTimeDesc(String licensePlate);

    ParkingSession findTopBySpotIdAndExitTimeIsNull(Long spotId);
}