package br.com.estacionamento.Sistema.de.Estacionamento.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "sectors")
@Getter
@Setter
@NoArgsConstructor
public class Sector {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 10, nullable = false)
    private String sector;

    @Column(nullable = false)
    @JsonProperty("base_price")
    private double basePrice;

    @Column(nullable = false)
    private int maxCapacity;

    @Column(length = 5, nullable = false)
    private String openHour;

    @Column(length = 5, nullable = false)
    private String closeHour;

    @Column(nullable = false)
    private int durationLimitMinutes;
}
