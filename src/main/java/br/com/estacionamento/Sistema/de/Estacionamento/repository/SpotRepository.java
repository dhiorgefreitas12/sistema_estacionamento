package br.com.estacionamento.Sistema.de.Estacionamento.repository;

import br.com.estacionamento.Sistema.de.Estacionamento.model.Spot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpotRepository extends JpaRepository<Spot, Long> {
    Optional<Spot> findByLatAndLng(Double lat, Double lng);

    long countBySectorAndOccupyTrue(String sector);

}
