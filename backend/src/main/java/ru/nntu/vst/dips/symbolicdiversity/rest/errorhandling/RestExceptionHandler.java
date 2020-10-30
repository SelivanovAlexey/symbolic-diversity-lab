package ru.nntu.vst.dips.symbolicdiversity.rest.errorhandling;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import javax.servlet.ServletException;
import java.time.LocalDateTime;
import java.util.Arrays;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleBadRequestException(
            ApiException exception) {
        return ResponseEntity.badRequest().body(buildErrorMessage(exception));
    }

    @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception) {
        return ResponseEntity.badRequest().body(buildParameterErrorMessage(exception));

    }

    private Error buildParameterErrorMessage(MethodArgumentTypeMismatchException ex) {
        String errorString = "Wrong parameter value: " + ex.getParameter().getParameterName()
                + ". Applicable values: " + (String.join(", ",
                Arrays.stream(ex.getParameter().getParameterType().getEnumConstants())
                        .map(e -> (Enum) e)
                        .map(Enum::name)
                        .toArray(String[]::new)));
        return Error.builder()
                .timestamp(LocalDateTime.now().toString())
                .code(HttpStatus.BAD_REQUEST.value())
                .status(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .error(errorString)
                .build();
    }

    @ExceptionHandler({MissingServletRequestParameterException.class, MissingServletRequestPartException.class})
    public ResponseEntity<Object> handleMissingServletRequestParameterException(ServletException exception) {
        return ResponseEntity.badRequest().body(buildErrorMessage(exception.getMessage(), HttpStatus.BAD_REQUEST));
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleInternalServerErrorException(
            Exception exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private Error buildErrorMessage(ApiException exception) {
        return Error.builder()
                .timestamp(LocalDateTime.now().toString())
                .code(exception.getCode())
                .status(exception.getStatus().getReasonPhrase())
                .error(exception.getMessage())
                .build();
    }

    private Error buildErrorMessage(String message, HttpStatus status) {
        return Error.builder()
                .timestamp(LocalDateTime.now().toString())
                .code(status.value())
                .status(status.getReasonPhrase())
                .error(message)
                .build();
    }
}
