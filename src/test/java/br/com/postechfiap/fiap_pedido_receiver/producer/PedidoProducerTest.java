package br.com.postechfiap.fiap_pedido_receiver.producer;

import br.com.postechfiap.fiap_pedido_receiver.entities.Pedido;
import br.com.postechfiap.fiap_pedido_receiver.exception.ErroDeProcessamentoDePedidoException;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoProducerTest {

    @Mock
    private KafkaTemplate<String, Pedido> kafkaTemplate;

    @InjectMocks
    private PedidoProducer pedidoProducer;

    @Test
    void deveEnviarPedidoComSucessoParaKafka() throws ExecutionException, InterruptedException {
        // Arrange
        UUID pedidoId = UUID.randomUUID();
        Pedido pedido = Pedido.builder().id(pedidoId).idCliente(1L).build();
        String topicoPedido = "test-pedido-topic";
        ReflectionTestUtils.setField(pedidoProducer, "topicoPedido", topicoPedido);

        RecordMetadata metadata = new RecordMetadata(null, 1, 1, 1L, 1L, 1, 1);
        SendResult<String, Pedido> sendResult = new SendResult<>(null, metadata);
        CompletableFuture<SendResult<String, Pedido>> future = CompletableFuture.completedFuture(sendResult);

        when(kafkaTemplate.send(eq(topicoPedido), eq(pedidoId.toString()), eq(pedido))).thenReturn(future);

        // Act
        pedidoProducer.enviarPedido(pedido);

        // Assert
        verify(kafkaTemplate, times(1)).send(topicoPedido, pedidoId.toString(), pedido);
    }

    @Test
    void deveLancarErroDeProcessamentoSeFalhaAoEnviarParaKafka() {
        // Arrange
        UUID pedidoId = UUID.randomUUID();
        Pedido pedido = Pedido.builder().id(pedidoId).idCliente(1L).build();
        String topicoPedido = "test-pedido-topic";
        ReflectionTestUtils.setField(pedidoProducer, "topicoPedido", topicoPedido);

        CompletableFuture<SendResult<String, Pedido>> futureComFalha = new CompletableFuture<>();
        futureComFalha.completeExceptionally(new ErroDeProcessamentoDePedidoException("Simulated Kafka send exception"));

        when(kafkaTemplate.send(eq(topicoPedido), eq(pedidoId.toString()), eq(pedido))).thenReturn(futureComFalha);

        // Act & Assert
        assertThrows(ErroDeProcessamentoDePedidoException.class, () -> {
            try {
                pedidoProducer.enviarPedido(pedido);
                futureComFalha.get(); // Força a obtenção do resultado (e a exceção)
            } catch (ExecutionException e) {
                throw (Exception) e.getCause();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void deveEnviarPedidoComIdDoPedidoComoChave() {
        // Arrange
        UUID pedidoId = UUID.randomUUID();
        Pedido pedido = Pedido.builder().id(pedidoId).idCliente(1L).build();
        String topicoPedido = "test-pedido-topic";
        ReflectionTestUtils.setField(pedidoProducer, "topicoPedido", topicoPedido);

        RecordMetadata metadata = new RecordMetadata(null, 1, 1, 1L, 1L, 1, 1);
        SendResult<String, Pedido> sendResult = new SendResult<>(null, metadata);
        CompletableFuture<SendResult<String, Pedido>> future = CompletableFuture.completedFuture(sendResult);

        when(kafkaTemplate.send(anyString(), anyString(), any())).thenReturn(future);

        // Act
        pedidoProducer.enviarPedido(pedido);

        // Assert
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        verify(kafkaTemplate).send(eq(topicoPedido), keyCaptor.capture(), eq(pedido));
        assertEquals(pedidoId.toString(), keyCaptor.getValue());
    }

    @Test
    void deveEnviarPedidoComEntidadePedidoComoValor() {
        // Arrange
        UUID pedidoId = UUID.randomUUID();
        Pedido pedido = Pedido.builder().id(pedidoId).idCliente(1L).build();
        String topicoPedido = "test-pedido-topic";
        ReflectionTestUtils.setField(pedidoProducer, "topicoPedido", topicoPedido);

        RecordMetadata metadata = new RecordMetadata(null, 1, 1, 1L, 1L, 1, 1);
        SendResult<String, Pedido> sendResult = new SendResult<>(null, metadata);
        CompletableFuture<SendResult<String, Pedido>> future = CompletableFuture.completedFuture(sendResult);

        when(kafkaTemplate.send(anyString(), anyString(), any())).thenReturn(future);

        // Act
        pedidoProducer.enviarPedido(pedido);

        // Assert
        ArgumentCaptor<Pedido> valueCaptor = ArgumentCaptor.forClass(Pedido.class);
        verify(kafkaTemplate).send(eq(topicoPedido), eq(pedidoId.toString()), valueCaptor.capture());
        assertEquals(pedido, valueCaptor.getValue());
    }
}
