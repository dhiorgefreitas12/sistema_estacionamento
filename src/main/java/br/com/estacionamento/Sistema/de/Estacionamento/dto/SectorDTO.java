package br.com.estacionamento.Sistema.de.Estacionamento.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SectorDTO(
        String sector,

        @JsonProperty("base_price")
        double basePrice,

        @JsonProperty("max_capacity")
        int maxCapacity,

        @JsonProperty("open_hour")
        String openHour,

        @JsonProperty("close_hour")
        String closeHour,

        @JsonProperty("duration_limit_minutes")
        int durationLimitMinutes
) {}
