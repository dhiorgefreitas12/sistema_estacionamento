package br.com.estacionamento.Sistema.de.Estacionamento.repository;

import br.com.estacionamento.Sistema.de.Estacionamento.model.Sector;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SectorRepository extends JpaRepository<Sector, Long> {
    boolean existsBySector(String sector);
}
