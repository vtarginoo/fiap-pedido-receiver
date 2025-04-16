package br.com.postechfiap.fiap_pedido_receiver.interfaces.usecase;

import br.com.postechfiap.fiap_pedido_receiver.dto.PedidoRequest;
import br.com.postechfiap.fiap_pedido_receiver.dto.PedidoResponse;
import br.com.postechfiap.fiap_pedido_receiver.interfaces.UseCase;

public interface CriarPedidoUseCase extends UseCase<PedidoRequest, PedidoResponse> {
}
