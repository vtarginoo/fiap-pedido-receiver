package br.com.postechfiap.fiap_pedido_receiver.controller;

import br.com.postechfiap.fiap_pedido_receiver.dto.ItemPedidoRequest;
import br.com.postechfiap.fiap_pedido_receiver.dto.PedidoRequest;
import br.com.postechfiap.fiap_pedido_receiver.dto.PedidoResponse;
import br.com.postechfiap.fiap_pedido_receiver.entities.Pedido;
import br.com.postechfiap.fiap_pedido_receiver.interfaces.usecase.CriarPedidoUseCase;
import br.com.postechfiap.fiap_pedido_receiver.producer.PedidoProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CriarPedidoUseCase criarPedidoUsecase;

    @MockitoSpyBean
    private PedidoProducer pedidoProducer;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule()) // Suporte a LocalDate e outros tipos Java 8
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Para usar "yyyy-MM-dd" ao invés de timestamp

    @DisplayName("1.1 Criar Pedido - Sucesso")
    @Test
    void deveCriarPedidoComSucesso() throws Exception {
        // Arrange
        var request = new PedidoRequest();
        request.setIdCliente(1L);
        request.setNumeroCartao("1234123412341234");
        request.setCodigoSegurancaCartao("123");
        request.setNomeTitularCartao("João da Silva");
        request.setDataValidade(LocalDate.of(2025, 12, 31));
        request.setProdutos(List.of(new ItemPedidoRequest("SKU123", 2)));

        UUID idPedido = UUID.randomUUID();

        var response = PedidoResponse.builder()
                .idPedido(idPedido)
                .mensagem("Pedido enviado com sucesso para a fila")
                .build();

        doNothing().when(pedidoProducer).enviarPedido(any(Pedido.class));
        when(criarPedidoUsecase.execute(any(PedidoRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/pedido")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPedido").value(idPedido.toString()))
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
            "codigo_seguranca_cartao": null,
            "nome_titular_cartao": null,
            "data_validade": null,
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