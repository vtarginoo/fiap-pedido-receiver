package br.com.postechfiap.fiap_pedido_receiver.controller;

import br.com.postechfiap.fiap_pedido_receiver.dto.PedidoRequest;
import br.com.postechfiap.fiap_pedido_receiver.dto.PedidoResponse;
import br.com.postechfiap.fiap_pedido_receiver.interfaces.usecase.CriarPedidoUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;



@RestController
@RequestMapping(value = "/pedido")
@RequiredArgsConstructor
@Validated
@Tag(name = "Pedido", description = "API para criar Pedido")
public class PedidoController {

    private final CriarPedidoUseCase criarPedidoUsecase;

    @PostMapping
    @Operation(summary = "Cria Pedido", description = "Cria pedido")
    public ResponseEntity<PedidoResponse> criarPedido(@RequestBody @Valid PedidoRequest dto) {

        PedidoResponse pedido = criarPedidoUsecase.execute(dto);

        return ResponseEntity.ok(pedido);

    }
}