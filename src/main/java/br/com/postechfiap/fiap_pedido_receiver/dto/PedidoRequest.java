package br.com.postechfiap.fiap_pedido_receiver.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "Representa o pedido recebido")
public class PedidoRequest {

    @NotNull
    @Schema(description = "ID do cliente", example = "123")
    private Long idCliente;

    @NotEmpty
    @Valid
    @Schema(description = "Lista de produtos do pedido")
    private List<ItemPedidoRequest> produtos;

    @NotNull
    @Schema(description = "Numero Cartão")
    private String numeroCartao;
}