package br.com.postechfiap.fiap_pedido_receiver.usecase;

import br.com.postechfiap.fiap_pedido_receiver.dto.PedidoRequest;
import br.com.postechfiap.fiap_pedido_receiver.dto.PedidoResponse;
import br.com.postechfiap.fiap_pedido_receiver.entities.ItemPedido;
import br.com.postechfiap.fiap_pedido_receiver.entities.Pedido;
import br.com.postechfiap.fiap_pedido_receiver.interfaces.usecase.CriarPedidoUseCase;
import br.com.postechfiap.fiap_pedido_receiver.producer.PedidoProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CriarPedidoUseCaseImpl implements CriarPedidoUseCase {

    private final PedidoProducer pedidoProducer;

    @Override
    public PedidoResponse execute(PedidoRequest request) {
        UUID idPedido = UUID.randomUUID();

        Pedido pedido = Pedido.builder()
                .id(idPedido)
                .idCliente(request.getIdCliente())
                .numeroCartao(request.getNumeroCartao())
                .codigoSegurancaCartao(request.getCodigoSegurancaCartao())
                .nomeTitularCartao(request.getNomeTitularCartao())
                .dataValidade(request.getDataValidade())
                .produtos(
                        request.getProdutos().stream()
                                .map(p -> new ItemPedido(p.getSku(), p.getQuantidade()))
                                .toList()
                )
                .dataCriacao(LocalDateTime.now())
                .build();

        pedidoProducer.enviarPedido(pedido); // envia pra fila



        return new PedidoResponse(pedido.getId(), "Pedido enviado com sucesso para a fila");
    }
}
