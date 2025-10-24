package barbearia.com.br.barbearia.config;

import barbearia.com.br.barbearia.dto.ErrorResponse;
import barbearia.com.br.barbearia.exceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex, WebRequest request) {
        log.warn("Business exception: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(ex.getStatus().value());
        errorResponse.setError(ex.getStatus().getReasonPhrase());
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setPath(getRequestPath(request));
        
        return new ResponseEntity<>(errorResponse, ex.getStatus());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex, WebRequest request) {
        log.warn("Resource not found: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
        errorResponse.setError("Recurso não encontrado");
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setPath(getRequestPath(request));
        
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(ConflictException ex, WebRequest request) {
        log.warn("Conflict: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(HttpStatus.CONFLICT.value());
        errorResponse.setError("Conflito");
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setPath(getRequestPath(request));
        
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, WebRequest request) {
        log.warn("Access denied: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(HttpStatus.FORBIDDEN.value());
        errorResponse.setError("Acesso negado");
        errorResponse.setMessage("Você não tem permissão para acessar este recurso");
        errorResponse.setPath(getRequestPath(request));
        
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        log.warn("Validation error: {}", ex.getMessage());
        
        List<ErrorResponse.FieldError> fieldErrors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> {
                    String fieldName = ((FieldError) error).getField();
                    String errorMessage = error.getDefaultMessage();
                    Object rejectedValue = ((FieldError) error).getRejectedValue();
                    return new ErrorResponse.FieldError(fieldName, errorMessage, rejectedValue);
                })
                .collect(Collectors.toList());
        
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponse.setError("Erro de validação");
        errorResponse.setMessage("Dados de entrada inválidos");
        errorResponse.setPath(getRequestPath(request));
        errorResponse.setFieldErrors(fieldErrors);
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        log.warn("Constraint violation: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponse.setError("Violação de constraints");
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setPath(getRequestPath(request));
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, WebRequest request) {
        log.error("Erro interno do servidor: ", ex);
        
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorResponse.setError("Erro interno do servidor");
        errorResponse.setMessage("Ocorreu um erro inesperado. Tente novamente mais tarde.");
        errorResponse.setPath(getRequestPath(request));
        
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String getRequestPath(WebRequest request) {
        if (request instanceof ServletWebRequest) {
            HttpServletRequest servletRequest = ((ServletWebRequest) request).getRequest();
            return servletRequest.getRequestURI();
        }
        return "";
    }
}