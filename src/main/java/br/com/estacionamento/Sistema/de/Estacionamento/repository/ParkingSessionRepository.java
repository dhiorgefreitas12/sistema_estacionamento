package br.com.estacionamento.Sistema.de.Estacionamento.repository;

import br.com.estacionamento.Sistema.de.Estacionamento.model.ParkingSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ParkingSessionRepository extends JpaRepository<ParkingSession, Long> {
    ParkingSession findTopByLicensePlateOrderByEntryTimeDesc(String licensePlate);

    ParkingSession findTopBySpotIdAndExitTimeIsNull(Long spotId);

    List<ParkingSession> findAllBySectorAndExitTimeBetween(String sector, LocalDateTime start, LocalDateTime end);

    boolean existsByLicensePlateAndExitTimeIsNull(String licensePlate);

}