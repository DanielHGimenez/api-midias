package dev.dhg.apimidias.infrastructure.exception;

public class MediaNaoSuportadaException extends RuntimeException {

    public MediaNaoSuportadaException(String msg) {
        super(msg);
    }
}
