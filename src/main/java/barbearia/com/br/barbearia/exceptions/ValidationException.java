package barbearia.com.br.barbearia.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ValidationException extends BusinessException {
    public ValidationException(String message) {
        super(message, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}