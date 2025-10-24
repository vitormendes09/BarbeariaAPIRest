package barbearia.com.br.barbearia.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AccessDeniedException extends BusinessException {
    public AccessDeniedException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}