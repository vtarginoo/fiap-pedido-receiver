package br.com.postechfiap.fiap_pedido_receiver.exception;

import java.util.concurrent.TimeoutException;

public class PedidoKafkaTimeoutException extends RuntimeException {

    public PedidoKafkaTimeoutException(String message) {
        super(message);
    }

    public PedidoKafkaTimeoutException(String message, Throwable cause) {
        super();
    }
}