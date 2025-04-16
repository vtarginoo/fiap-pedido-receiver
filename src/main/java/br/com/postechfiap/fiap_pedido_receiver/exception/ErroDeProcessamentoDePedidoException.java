package br.com.postechfiap.fiap_pedido_receiver.exception;

public class ErroDeProcessamentoDePedidoException extends RuntimeException {

    public ErroDeProcessamentoDePedidoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }

    public ErroDeProcessamentoDePedidoException(String mensagem) {
        super(mensagem);
    }
}