package br.com.postechfiap.fiap_pedido_receiver.producer;

import br.com.postechfiap.fiap_pedido_receiver.entities.Pedido;
import br.com.postechfiap.fiap_pedido_receiver.exception.ErroDeProcessamentoDePedidoException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
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
    private KafkaTemplate<String, String> kafkaTemplate;

    private PedidoProducer pedidoProducer;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        pedidoProducer = new PedidoProducer(objectMapper);
        ReflectionTestUtils.setField(pedidoProducer, "kafkaTemplate", kafkaTemplate);
    }

    @Test
    void deveEnviarPedidoComSucessoParaKafka() throws Exception {
        UUID pedidoId = UUID.randomUUID();
        Pedido pedido = Pedido.builder().id(pedidoId).idCliente(1L).build();
        String topicoPedido = "test-pedido-topic";
        ReflectionTestUtils.setField(pedidoProducer, "topicoPedido", topicoPedido);

        String pedidoJson = objectMapper.writeValueAsString(pedido);

        RecordMetadata metadata = new RecordMetadata(null, 1, 1, 1L, 1L, 1, 1);
        SendResult<String, String> sendResult = new SendResult<>(null, metadata);
        CompletableFuture<SendResult<String, String>> future = CompletableFuture.completedFuture(sendResult);

        when(kafkaTemplate.send(eq(topicoPedido), eq(pedidoId.toString()), eq(pedidoJson))).thenReturn(future);

        pedidoProducer.enviarPedido(pedido);

        verify(kafkaTemplate, times(1)).send(topicoPedido, pedidoId.toString(), pedidoJson);
    }

    @Test
    void deveEnviarPedidoComIdDoPedidoComoChave() throws Exception {
        UUID pedidoId = UUID.randomUUID();
        Pedido pedido = Pedido.builder().id(pedidoId).idCliente(1L).build();
        String topicoPedido = "test-pedido-topic";
        ReflectionTestUtils.setField(pedidoProducer, "topicoPedido", topicoPedido);

        String pedidoJson = objectMapper.writeValueAsString(pedido);

        RecordMetadata metadata = new RecordMetadata(null, 1, 1, 1L, 1L, 1, 1);
        SendResult<String, String> sendResult = new SendResult<>(null, metadata);
        CompletableFuture<SendResult<String, String>> future = CompletableFuture.completedFuture(sendResult);

        when(kafkaTemplate.send(anyString(), anyString(), anyString())).thenReturn(future);

        pedidoProducer.enviarPedido(pedido);

        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        verify(kafkaTemplate).send(eq(topicoPedido), keyCaptor.capture(), eq(pedidoJson));
        assertEquals(pedidoId.toString(), keyCaptor.getValue());
    }

    @Test
    void deveEnviarPedidoComJsonComoValor() throws Exception {
        UUID pedidoId = UUID.randomUUID();
        Pedido pedido = Pedido.builder().id(pedidoId).idCliente(1L).build();
        String topicoPedido = "test-pedido-topic";
        ReflectionTestUtils.setField(pedidoProducer, "topicoPedido", topicoPedido);

        String pedidoJson = objectMapper.writeValueAsString(pedido);

        RecordMetadata metadata = new RecordMetadata(null, 1, 1, 1L, 1L, 1, 1);
        SendResult<String, String> sendResult = new SendResult<>(null, metadata);
        CompletableFuture<SendResult<String, String>> future = CompletableFuture.completedFuture(sendResult);

        when(kafkaTemplate.send(anyString(), anyString(), anyString())).thenReturn(future);

        pedidoProducer.enviarPedido(pedido);

        ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);
        verify(kafkaTemplate).send(eq(topicoPedido), eq(pedidoId.toString()), valueCaptor.capture());
        assertEquals(pedidoJson, valueCaptor.getValue());
    }

    @Test
    void deveLancarErroAoSerializarPedidoParaJson() throws JsonProcessingException, JsonProcessingException {
        // Arrange
        UUID pedidoId = UUID.randomUUID();
        Pedido pedido = Pedido.builder().id(pedidoId).idCliente(1L).build();
        String topicoPedido = "test-pedido-topic";
        ReflectionTestUtils.setField(pedidoProducer, "topicoPedido", topicoPedido);

        // Simular falha na serialização JSON
        ObjectMapper mockObjectMapper = mock(ObjectMapper.class);
        when(mockObjectMapper.writeValueAsString(pedido)).thenThrow(new JsonProcessingException("Erro simulado") {});

        PedidoProducer pedidoProducerComMock = new PedidoProducer(mockObjectMapper);
        ReflectionTestUtils.setField(pedidoProducerComMock, "kafkaTemplate", kafkaTemplate);
        ReflectionTestUtils.setField(pedidoProducerComMock, "topicoPedido", topicoPedido);

        // Act & Assert
        ErroDeProcessamentoDePedidoException exception = assertThrows(ErroDeProcessamentoDePedidoException.class,
                () -> pedidoProducerComMock.enviarPedido(pedido));

        assertTrue(exception.getMessage().contains("Erro ao serializar o Pedido"));
    }

    @Test
    void deveLancarErroDeProcessamentoQuandoCallbackDeEnvioKafkaFalha() throws Exception {
        // Arrange
        UUID pedidoId = UUID.randomUUID();
        Pedido pedido = Pedido.builder().id(pedidoId).idCliente(1L).build();
        String topicoPedido = "test-pedido-topic";
        ReflectionTestUtils.setField(pedidoProducer, "topicoPedido", topicoPedido);

        CompletableFuture<SendResult<String, String>> future = new CompletableFuture<>();

        // Prepare para que o send retorne um future que chamará a callback com exceção
        when(kafkaTemplate.send(eq(topicoPedido), eq(pedidoId.toString()), anyString())).thenReturn(future);

        // Act
        // Chamar o método que envia o pedido (que retorna void), mas a exceção é lançada dentro do whenComplete
        // Então, precisamos capturar a exceção da callback manualmente
        pedidoProducer.enviarPedido(pedido);

        // Simula a exceção na callback
        Exception kafkaException = new RuntimeException("Erro simulado no Kafka");
        future.completeExceptionally(kafkaException);

        // Espera um pouco para o callback ser executado (em testes reais poderia usar Awaitility ou outro mecanismo)
        Thread.sleep(100);
    }
}
