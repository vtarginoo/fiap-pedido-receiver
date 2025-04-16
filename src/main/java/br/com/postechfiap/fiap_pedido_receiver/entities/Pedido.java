package br.com.postechfiap.fiap_pedido_receiver.entities;


import br.com.postechfiap.fiap_pedido_receiver.dto.DadosPagamento;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode(callSuper = false)
@ToString
@Builder(toBuilder = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Pedido {

    private UUID id;
    private Long idCliente;
    private List<ItemPedido> produtos;
    private DadosPagamento pagamento;
    private LocalDateTime dataCriacao;
}