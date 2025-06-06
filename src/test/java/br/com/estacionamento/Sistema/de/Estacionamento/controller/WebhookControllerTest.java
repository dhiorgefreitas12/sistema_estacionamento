package br.com.estacionamento.Sistema.de.Estacionamento.controller;

import br.com.estacionamento.Sistema.de.Estacionamento.dto.WebhookEventDTO;
import br.com.estacionamento.Sistema.de.Estacionamento.enums.EventType;
import br.com.estacionamento.Sistema.de.Estacionamento.service.EventService;
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

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class WebhookControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EventService eventService;

    @InjectMocks
    private WebhookController webhookController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(webhookController).build();
    }

    @Test
    void testHandleEvent_ReturnsOk() throws Exception {
        WebhookEventDTO dto = new WebhookEventDTO(
                "ABC1234",
                "2025-06-06T17:00:00",
                "",
                10.123,
                -20.456,
                EventType.ENTRY
        );

        mockMvc.perform(post("/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(eventService).processEvent(Mockito.any(WebhookEventDTO.class));
    }
}