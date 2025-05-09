package br.com.postechfiap.fiap_pedido_receiver.producer;

import br.com.postechfiap.fiap_pedido_receiver.entities.Pedido;
import br.com.postechfiap.fiap_pedido_receiver.exception.ErroDeProcessamentoDePedidoException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PedidoProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper;

    @Value("${kafka.topic.pedido}")
    private String topicoPedido;

    public PedidoProducer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void enviarPedido(Pedido pedido) {
        try {
            String pedidoJson = objectMapper.writeValueAsString(pedido);
            System.out.println("➡️ Enviando JSON para o Kafka: " + pedidoJson);

            kafkaTemplate.send(topicoPedido, pedido.getId().toString(), pedidoJson)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            System.out.println("Falha ao enviar pedido para o Kafka: " + ex.getMessage());
                            throw new ErroDeProcessamentoDePedidoException("Erro ao enviar pedido para o Kafka", ex);
                        }

                        System.out.println("✅ Pedido enviado com sucesso para o Kafka. Offset: "
                                + result.getRecordMetadata().offset());
                    });
        } catch (Exception e) {
            System.err.println("Erro ao serializar o Pedido para JSON: " + e.getMessage());
            throw new ErroDeProcessamentoDePedidoException("Erro ao serializar o Pedido", e);
        }
    }
}