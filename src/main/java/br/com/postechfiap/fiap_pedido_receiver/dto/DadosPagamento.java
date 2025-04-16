package br.com.postechfiap.fiap_pedido_receiver.dto;

import br.com.postechfiap.fiap_pedido_receiver.enums.MetodoPagamentoEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
@EqualsAndHashCode
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "Informações de pagamento do pedido")
public class DadosPagamento {

    @NotNull
    @Schema(description = "Valor do pagamento", example = "100.00")
    private BigDecimal valor;

    @NotNull
    @Schema(description = "Método de pagamento", example = "CARTAO_CREDITO")
    private MetodoPagamentoEnum metodoPagamento;

    @NotNull
    @Min(1)
    @Schema(description = "Quantidade de parcelas", example = "1")
    private Integer parcelas;
}