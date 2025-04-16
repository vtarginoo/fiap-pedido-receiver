package br.com.postechfiap.fiap_pedido_receiver.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "Resposta do pedido criado")
public class PedidoResponse {

    @Schema(description = "ID do pedido gerado", example = "f4f5b9e2-6d1c-43b1-85c0-342f4d08b5dd")
    private UUID idPedido;

    @Schema(description = "Mensagem de status", example = "Pedido enviado com sucesso para a fila")
    private String mensagem;
}
