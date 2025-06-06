package br.com.estacionamento.Sistema.de.Estacionamento.repository;

import br.com.estacionamento.Sistema.de.Estacionamento.model.Spot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpotRepository extends JpaRepository<Spot, Long> {}
