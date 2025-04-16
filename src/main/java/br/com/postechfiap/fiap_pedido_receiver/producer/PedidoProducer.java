package br.com.postechfiap.fiap_pedido_receiver.producer;

import br.com.postechfiap.fiap_pedido_receiver.entities.Pedido;
import br.com.postechfiap.fiap_pedido_receiver.exception.ErroDeProcessamentoDePedidoException;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PedidoProducer {

    private final KafkaTemplate<String, Pedido> kafkaTemplate;

    @Value("${kafka.topic.pedido}")
    private String topicoPedido;

    public void enviarPedido(Pedido pedido) {
        kafkaTemplate.send(topicoPedido, pedido.getId().toString(), pedido)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        System.out.println("Falha ao enviar pedido para o Kafka: " + ex.getMessage());
                        throw new ErroDeProcessamentoDePedidoException("Erro ao enviar pedido para o Kafka", ex);
                    }

                    System.out.println("✅ Pedido enviado com sucesso para o Kafka. Offset: "
                            + result.getRecordMetadata().offset());
                });
    }
}