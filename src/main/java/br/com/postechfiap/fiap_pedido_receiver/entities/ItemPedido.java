package br.com.postechfiap.fiap_pedido_receiver.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
@EqualsAndHashCode
@ToString
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ItemPedido {

    private String sku;
    private Integer quantidade;
}