package br.com.postechfiap.fiap_pedido_receiver.interfaces;

public interface UseCase<Input, Output> {

    Output execute(Input entry);
}

