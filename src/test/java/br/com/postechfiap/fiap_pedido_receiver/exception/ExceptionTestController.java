package br.com.postechfiap.fiap_pedido_receiver.exception;

import org.apache.kafka.common.KafkaException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeoutException;

@RestController
public class ExceptionTestController {

    @GetMapping("/test/kafka-timeout")
    @ResponseBody
    public String triggerKafkaTimeout() {
        throw new KafkaException("Simulando erro de Kafka", new TimeoutException("Timeout ao conectar com o Kafka"));
    }

    @GetMapping("/test/pedido-timeout")
    public String triggerPedidoTimeout() {
        throw new PedidoKafkaTimeoutException("Simulando timeout de pedido");
    }


    @GetMapping("/test/processamento")
    public String triggerProcessamento() {
        throw new ErroDeProcessamentoDePedidoException("Simulando erro de processamento");
    }

    @GetMapping("/test/generico")
    public String triggerGenerico() {
        throw new RuntimeException("Simulando erro genérico");
    }
}
