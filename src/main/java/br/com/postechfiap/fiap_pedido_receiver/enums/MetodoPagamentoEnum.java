package br.com.postechfiap.fiap_pedido_receiver.enums;

import br.com.postechfiap.fiap_pedido_receiver.interfaces.EnumSerializable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

@AllArgsConstructor
@Getter
public enum MetodoPagamentoEnum implements EnumSerializable {

    CARTAO_CREDITO("CARTAO_CREDITO"),
    DEBITO("CARTAO_DEBITO"),
    BOLETO("BOLETO"),
    PIX("PIX"),
    TRANSFERENCIA("TRANSFERENCIA");

    private final String metodoPagamento;

    public static MetodoPagamentoEnum findBy(final String metodoPagamento) {
        return Stream.of(values())
                .filter(v -> v.getMetodoPagamento().equalsIgnoreCase(metodoPagamento))
                .findFirst()
                .orElse(null);
    }

    @Override
    public String getValue() {
        return this.metodoPagamento.trim();
    }
}
