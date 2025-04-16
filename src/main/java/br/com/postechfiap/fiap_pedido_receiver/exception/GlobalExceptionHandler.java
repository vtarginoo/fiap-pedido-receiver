package br.com.postechfiap.fiap_pedido_receiver.exception;

import br.com.postechfiap.fiap_pedido_receiver.dto.ResponseError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.KafkaException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(KafkaException.class)
    public ResponseEntity<ResponseError> handleKafkaException(KafkaException ex) {
        if (ex.getCause() instanceof org.apache.kafka.common.errors.TimeoutException) {

            System.out.println("Kafka Fora do Ar!");

            // Verificar se o ex.getCause() é nulo ou contém dados inválidos
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                    .body(new ResponseError(
                            HttpStatus.GATEWAY_TIMEOUT.value(),
                            HttpStatus.GATEWAY_TIMEOUT.getReasonPhrase(),
                            List.of(ex.getMessage())
                    ));
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseError(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                        List.of("Erro ao enviar mensagem para o Kafka: " + ex.getMessage())
                ));
    }



    @ExceptionHandler(PedidoKafkaTimeoutException.class)
    public ResponseEntity<ResponseError> handleKafkaTimeout(PedidoKafkaTimeoutException ex) {
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                .body(new ResponseError(
                        HttpStatus.GATEWAY_TIMEOUT.value(),
                        HttpStatus.GATEWAY_TIMEOUT.getReasonPhrase(),
                        List.of(ex.getMessage())
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseError> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<String> messages = new ArrayList<>();
        BindingResult bindingResult = ex.getBindingResult();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            messages.add(fieldError.getDefaultMessage());
        }
        ResponseError errorResponse = new ResponseError(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                messages
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(ErroDeProcessamentoDePedidoException.class)
    public ResponseEntity<ResponseError> handleErroDeProcessamento(ErroDeProcessamentoDePedidoException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ResponseError(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                        ex.getMessage()
                )
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseError> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ResponseError(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                        "Erro inesperado: " + ex.getMessage()
                )
        );
    }
}