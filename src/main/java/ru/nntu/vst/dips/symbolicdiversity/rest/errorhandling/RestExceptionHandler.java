package ru.nntu.vst.dips.symbolicdiversity.rest.errorhandling;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartException;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<Object> handleMultipartException(HttpServletRequest req, Exception exception) {
        return new ResponseEntity<>(buildErrorMessage(exception, HttpStatus.BAD_REQUEST),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Object> handleException(HttpServletRequest req, Exception exception) {
        return new ResponseEntity<>(buildErrorMessage(exception, HttpStatus.INTERNAL_SERVER_ERROR),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private Error buildErrorMessage(Exception exception, HttpStatus status) {
        return Error.builder()
                .timestamp(LocalDateTime.now().toString())
                .code(status.value())
                .status(status.getReasonPhrase())
                .error(exception.getMessage())
                .build();
    }
}
