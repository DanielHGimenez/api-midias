package dev.dhg.apimidias.controller;

import dev.dhg.apimidias.DTO.ErroResponse;
import dev.dhg.apimidias.infrastructure.exception.CampoInvalidoException;
import dev.dhg.apimidias.infrastructure.exception.ErroProcessamentoMediaException;
import dev.dhg.apimidias.infrastructure.exception.MediaNaoEncontradaException;
import dev.dhg.apimidias.infrastructure.exception.MediaNaoSuportadaException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(MediaNaoSuportadaException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<ErroResponse> handleMediaNaoSuportadaException(MediaNaoSuportadaException ex) {
        return ResponseEntity.of (
            Optional.of (
                ErroResponse.builder()
                    .mensagem("O formato de media enviada n\u00E3o \u00E9 suportada")
                .build()
            )
        );
    }

    @ExceptionHandler(ErroProcessamentoMediaException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ResponseEntity<ErroResponse> handleErroProcessamentoMediaException(ErroProcessamentoMediaException ex) {
        return ResponseEntity.of (
            Optional.of (
                ErroResponse.builder()
                    .mensagem("Um erro inesperado aconteceu")
                .build()
            )
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<ErroResponse> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException ex
    ) {
        return ResponseEntity.of (
            Optional.of (
                ErroResponse.builder()
                    .mensagem(String.format("O parametro \"%s\" n\u00E3o pode ser nulo", ex.getParameterName()))
                .build()
            )
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<ErroResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex
    ) {
        List<String> erros = new LinkedList<String>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String mensagem = error.getDefaultMessage();
            erros.add(mensagem);
        });

        return ResponseEntity.of (
            Optional.of (
                ErroResponse.builder()
                    .mensagem(erros)
                .build()
            )
        );
    }

    @ExceptionHandler(CampoInvalidoException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<ErroResponse> handleCampoInvalidoException(CampoInvalidoException ex) {
        return ResponseEntity.of (
            Optional.of (
                ErroResponse.builder()
                    .mensagem(ex.getMessage())
                .build()
            )
        );
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<ErroResponse> handleMissingServletRequestPartException(
            MissingServletRequestPartException ex
    ) {
        return ResponseEntity.of (
                Optional.of (
                    ErroResponse.builder()
                        .mensagem (
                            String.format (
                                "O parametro \"%s\" n\u00E3o pode ser nulo",
                                ex.getRequestPartName()
                            )
                        )
                    .build()
                )
        );
    }

    @ExceptionHandler(MediaNaoEncontradaException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ResponseEntity<ErroResponse> handleMediaNaoEncontradaException(MediaNaoEncontradaException ex) {
        return ResponseEntity.of (
            Optional.of (
                ErroResponse.builder()
                    .mensagem ("Media n\u00E3o encontrada")
                .build()
            )
        );
    }

}
