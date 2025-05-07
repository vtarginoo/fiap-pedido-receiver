package br.com.postechfiap.fiap_pedido_receiver.usecase;

import br.com.postechfiap.fiap_pedido_receiver.dto.ItemPedidoRequest;
import br.com.postechfiap.fiap_pedido_receiver.dto.PedidoRequest;
import br.com.postechfiap.fiap_pedido_receiver.dto.PedidoResponse;
import br.com.postechfiap.fiap_pedido_receiver.entities.Pedido;
import br.com.postechfiap.fiap_pedido_receiver.exception.ErroDeProcessamentoDePedidoException;
import br.com.postechfiap.fiap_pedido_receiver.interfaces.usecase.CriarPedidoUseCase;
import br.com.postechfiap.fiap_pedido_receiver.producer.PedidoProducer;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;


import org.junit.jupiter.api.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CriarPedidoUseCaseTest {

    @Mock
    private PedidoProducer pedidoProducer;

    @InjectMocks
    private CriarPedidoUseCaseImpl criarPedidoUseCase;

    @Test
    void deveCriarPedidoEEnviarParaFilaComSucesso() {
        // Arrange
        var request = new PedidoRequest();
        request.setIdCliente(10L);
        request.setNumeroCartao("9876543210");
        request.setProdutos(List.of(new ItemPedidoRequest("PROD456", 1)));

        // Act
        PedidoResponse response = criarPedidoUseCase.execute(request);

        // Assert
        assertNotNull(response.getIdPedido());
        assertEquals("Pedido enviado com sucesso para a fila", response.getMensagem());

        verify(pedidoProducer, times(1)).enviarPedido(any(Pedido.class));

        ArgumentCaptor<Pedido> pedidoCaptor = ArgumentCaptor.forClass(Pedido.class);
        verify(pedidoProducer).enviarPedido(pedidoCaptor.capture());
        Pedido pedidoEnviado = pedidoCaptor.getValue();

        assertEquals(request.getIdCliente(), pedidoEnviado.getIdCliente());
        assertEquals(request.getNumeroCartao(), pedidoEnviado.getNumeroCartao());
        assertEquals(1, pedidoEnviado.getProdutos().size());
        assertEquals("PROD456", pedidoEnviado.getProdutos().get(0).getSku());
        assertEquals(1, pedidoEnviado.getProdutos().get(0).getQuantidade());
        assertEquals(response.getIdPedido(), pedidoEnviado.getId());
    }

    @Test
    void deveCriarPedidoComMultiplosItens() {
        // Arrange
        var request = new PedidoRequest();
        request.setIdCliente(20L);
        request.setNumeroCartao("1111222233334444");
        request.setProdutos(List.of(
                new ItemPedidoRequest("ITEM001", 3),
                new ItemPedidoRequest("ITEM002", 2)
        ));

        // Act
        PedidoResponse response = criarPedidoUseCase.execute(request);

        // Assert
        assertNotNull(response.getIdPedido());
        assertEquals("Pedido enviado com sucesso para a fila", response.getMensagem());

        ArgumentCaptor<Pedido> pedidoCaptor = ArgumentCaptor.forClass(Pedido.class);
        verify(pedidoProducer).enviarPedido(pedidoCaptor.capture());
        Pedido pedidoEnviado = pedidoCaptor.getValue();

        assertEquals(request.getIdCliente(), pedidoEnviado.getIdCliente());
        assertEquals(request.getNumeroCartao(), pedidoEnviado.getNumeroCartao());
        assertEquals(2, pedidoEnviado.getProdutos().size());
        assertEquals("ITEM001", pedidoEnviado.getProdutos().get(0).getSku());
        assertEquals(3, pedidoEnviado.getProdutos().get(0).getQuantidade());
        assertEquals("ITEM002", pedidoEnviado.getProdutos().get(1).getSku());
        assertEquals(2, pedidoEnviado.getProdutos().get(1).getQuantidade());
        assertEquals(response.getIdPedido(), pedidoEnviado.getId());
    }

    @Test
    void deveCriarPedidoComDadosBasicosNaoNulos() {
        // Arrange
        var request = new PedidoRequest();
        request.setIdCliente(30L);
        request.setProdutos(List.of(new ItemPedidoRequest("SKU999", 1)));
        request.setNumeroCartao(null);

        // Act
        PedidoResponse response = criarPedidoUseCase.execute(request);

        // Assert
        assertNotNull(response.getIdPedido());
        assertEquals("Pedido enviado com sucesso para a fila", response.getMensagem());

        ArgumentCaptor<Pedido> pedidoCaptor = ArgumentCaptor.forClass(Pedido.class);
        verify(pedidoProducer).enviarPedido(pedidoCaptor.capture());
        Pedido pedidoEnviado = pedidoCaptor.getValue();

        assertEquals(request.getIdCliente(), pedidoEnviado.getIdCliente());
        assertEquals(request.getNumeroCartao(), pedidoEnviado.getNumeroCartao());
        assertEquals(1, pedidoEnviado.getProdutos().size());
        assertEquals("SKU999", pedidoEnviado.getProdutos().get(0).getSku());
        assertEquals(1, pedidoEnviado.getProdutos().get(0).getQuantidade());
        assertEquals(response.getIdPedido(), pedidoEnviado.getId());
    }

    @Test
    void deveLancarErroSeFalhaAoEnviarPedido() {
        // Arrange
        var request = new PedidoRequest();
        request.setIdCliente(40L);
        request.setProdutos(List.of(new ItemPedidoRequest("ERROR01", 1)));

        // Simular uma falha ao enviar o pedido
        doThrow(new ErroDeProcessamentoDePedidoException("Erro simulado")).when(pedidoProducer).enviarPedido(any(Pedido.class));

        // Act & Assert
        assertThrows(ErroDeProcessamentoDePedidoException.class, () -> criarPedidoUseCase.execute(request));

        // Verificar se o pedidoProducer.enviarPedido() foi chamado mesmo na falha
        verify(pedidoProducer, times(1)).enviarPedido(any(Pedido.class));
    }
}