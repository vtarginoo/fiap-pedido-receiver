package br.com.postechfiap.fiap_pedido_receiver.controller;

import br.com.postechfiap.fiap_pedido_receiver.dto.ItemPedidoRequest;
import br.com.postechfiap.fiap_pedido_receiver.dto.PedidoRequest;
import br.com.postechfiap.fiap_pedido_receiver.dto.PedidoResponse;
import br.com.postechfiap.fiap_pedido_receiver.entities.Pedido;
import br.com.postechfiap.fiap_pedido_receiver.interfaces.usecase.CriarPedidoUseCase;
import br.com.postechfiap.fiap_pedido_receiver.producer.PedidoProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean // Use @MockBean para o CriarPedidoUseCase
    private CriarPedidoUseCase criarPedidoUsecase;

    @MockitoSpyBean // Use @SpyBean para o PedidoProducer
    private PedidoProducer pedidoProducer;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

    @DisplayName("1.1 Criar Pedido - Sucesso")
    @Test
    void deveCriarPedidoComSucesso() throws Exception {
        // Arrange
        var request = new PedidoRequest();
        request.setIdCliente(1L);
        request.setNumeroCartao("1234123412341234");
        request.setProdutos(List.of(new ItemPedidoRequest("SKU123", 2)));

        UUID idPedido = UUID.randomUUID();


        var response = PedidoResponse.builder()
                .idPedido(idPedido).mensagem("Pedido enviado com sucesso para a fila")
                .build();

        doNothing().when(pedidoProducer).enviarPedido(any(Pedido.class)); // Agora você pode stubar o método do bean real

        when(criarPedidoUsecase.execute(any(PedidoRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/pedido")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPedido").value(idPedido.toString())) // Use o ID gerado
                .andExpect(jsonPath("$.mensagem").value("Pedido enviado com sucesso para a fila"));
    }
    @DisplayName("1.2 Criar Pedido - Erro de validação")
    @Test
    void deveRetornarErroQuandoPedidoInvalido() throws Exception {
        // Arrange: pedido com lista de produtos vazia
        var requestInvalido = """
        {
            "id_cliente": null,
            "numero_cartao": null,
            "produtos": []
        }
        """;

        // Act & Assert
        mockMvc.perform(post("/pedido")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestInvalido))
                .andExpect(status().isBadRequest());
    }
}