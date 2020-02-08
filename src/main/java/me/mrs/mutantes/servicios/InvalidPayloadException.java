package me.mrs.mutantes.servicios;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidPayloadException extends IllegalArgumentException {
    public InvalidPayloadException(String s) {
        super(s);
    }
}
