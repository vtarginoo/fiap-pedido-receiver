package br.com.postechfiap.fiap_pedido_receiver.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "Item de produto no pedido")
public class ItemPedidoRequest {

    @NotBlank
    @Schema(description = "SKU do produto", example = "SKU123456")
    private String sku;

    @NotNull
    @Min(1)
    @Schema(description = "Quantidade do produto", example = "2")
    private Integer quantidade;
}