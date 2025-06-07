package br.com.estacionamento.Sistema.de.Estacionamento.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "spots")
@Getter
@Setter
@NoArgsConstructor
public class Spot {

    @Id
    private Long id;

    @Column(length = 10, nullable = false)
    private String sector;

    @Column(nullable = false)
    private double lat;

    @Column(nullable = false)
    private double lng;

    @Column(nullable = false)
    private boolean occupy = false;
}
