package br.com.estacionamento.Sistema.de.Estacionamento.controller;

import br.com.estacionamento.Sistema.de.Estacionamento.dto.SpotStatusRequestDTO;
import br.com.estacionamento.Sistema.de.Estacionamento.dto.SpotStatusResponseDTO;
import br.com.estacionamento.Sistema.de.Estacionamento.service.SpotStatusService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class SpotStatusControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SpotStatusService spotStatusService;

    @InjectMocks
    private SpotStatusController spotStatusController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final double LAT = -23.561684;
    private final double LNG = -46.655981;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(spotStatusController).build();
    }

    @Test
    void shouldReturnSpotOccupiedTrue() throws Exception {
        SpotStatusRequestDTO request = new SpotStatusRequestDTO(LAT, LNG);
        SpotStatusResponseDTO response = new SpotStatusResponseDTO(true,
                LocalDateTime.of(2025, 6, 7, 8, 0),
                LocalDateTime.of(2025, 6, 7, 12, 0));

        when(spotStatusService.getStatusByCoordinates(Mockito.any())).thenReturn(response);

        mockMvc.perform(post("/spot-status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.occupy").value(true))
                .andExpect(jsonPath("$.entry_time").exists())
                .andExpect(jsonPath("$.time_parked").exists());
    }

    @Test
    void shouldReturnSpotOccupiedFalse() throws Exception {
        SpotStatusRequestDTO request = new SpotStatusRequestDTO(LAT, LNG);
        SpotStatusResponseDTO response = new SpotStatusResponseDTO(false, null, null);

        when(spotStatusService.getStatusByCoordinates(Mockito.any())).thenReturn(response);

        mockMvc.perform(post("/spot-status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.occupy").value(false))
                .andExpect(jsonPath("$.entry_time").doesNotExist())
                .andExpect(jsonPath("$.time_parked").doesNotExist());
    }
}
