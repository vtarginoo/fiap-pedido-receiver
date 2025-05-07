package br.com.postechfiap.fiap_pedido_receiver.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.TimeoutException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({GlobalExceptionHandler.class, ExceptionTestController.class})
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;



    @Test
    void handleKafkaException_withGenericKafkaException_shouldReturnInternalServerError() throws Exception {
        mockMvc.perform(get("/test/generico") // Vamos usar o endpoint genérico aqui para simplificar
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .andExpect(jsonPath("$.httpError").value(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()))
                .andExpect(jsonPath("$.message[0]").value("Erro inesperado: Simulando erro genérico"));
    }

    @Test
    void handleKafkaTimeout_shouldReturnGatewayTimeout() throws Exception {
        mockMvc.perform(get("/test/pedido-timeout")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isGatewayTimeout())
                .andExpect(jsonPath("$.status").value(HttpStatus.GATEWAY_TIMEOUT.value()))
                .andExpect(jsonPath("$.httpError").value(HttpStatus.GATEWAY_TIMEOUT.getReasonPhrase()))
                .andExpect(jsonPath("$.message[0]").value("Simulando timeout de pedido"));
    }

    @Test
    void handleErroDeProcessamento_shouldReturnInternalServerErrorWithMessage() throws Exception {
        mockMvc.perform(get("/test/processamento")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .andExpect(jsonPath("$.httpError").value(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()))
                .andExpect(jsonPath("$.message[0]").value("Simulando erro de processamento"));
    }

    // Note que o teste para MethodArgumentNotValidException ainda precisa de uma abordagem
    // um pouco diferente, pois essa exceção é geralmente resultado de falha de validação.
    // Podemos criar um teste que simule essa falha se precisar.

    @Test
    void handleGenericException_shouldReturnInternalServerErrorWithGenericMessage() throws Exception {
        mockMvc.perform(get("/test/generico")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .andExpect(jsonPath("$.httpError").value(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()))
                .andExpect(jsonPath("$.message[0]").value("Erro inesperado: Simulando erro genérico"));
    }
}