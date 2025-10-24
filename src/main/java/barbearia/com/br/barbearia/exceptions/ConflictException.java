package barbearia.com.br.barbearia.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ConflictException extends BusinessException {
    public ConflictException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}