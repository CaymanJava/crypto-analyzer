package pro.crypto.configuration.web;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pro.crypto.response.Problem;

@RestControllerAdvice
@Order(1)
public class ApiExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<Problem> handleException(Exception exception) {
        return new ResponseEntity<>(new Problem(exception.getMessage()), HttpStatus.BAD_REQUEST);
    }

}
