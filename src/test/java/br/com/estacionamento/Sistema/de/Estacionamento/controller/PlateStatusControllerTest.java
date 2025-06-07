package br.com.estacionamento.Sistema.de.Estacionamento.controller;

import br.com.estacionamento.Sistema.de.Estacionamento.dto.PlateStatusRequestDTO;
import br.com.estacionamento.Sistema.de.Estacionamento.dto.PlateStatusResponseDTO;
import br.com.estacionamento.Sistema.de.Estacionamento.service.PlateStatusService;
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

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PlateStatusControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PlateStatusService plateStatusService;

    @InjectMocks
    private PlateStatusController plateStatusController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(plateStatusController).build();
    }

    @Test
    void testGetPlateStatus_ReturnsOk() throws Exception {
        String plate = "ZUL0001";
        LocalDateTime now = LocalDateTime.now();
        PlateStatusResponseDTO mockResponse = new PlateStatusResponseDTO(
                plate,
                15.75,
                now.minusMinutes(30),
                now
        );

        Mockito.when(plateStatusService.getStatusByLicensePlate(plate))
                .thenReturn(mockResponse);

        PlateStatusRequestDTO request = new PlateStatusRequestDTO(plate);

        mockMvc.perform(post("/plate-status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.license_plate").value(plate))
                .andExpect(jsonPath("$.price_until_now").value(15.75));

        verify(plateStatusService).getStatusByLicensePlate(plate);
    }
}
