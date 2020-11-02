package ru.nntu.vst.dips.symbolicdiversity.rest.errorhandling;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
public class ApiException extends RuntimeException {

    private String timestamp;
    private Integer code;
    private HttpStatus status;

    ApiException(String message, HttpStatus status) {
        super(message);
        this.status = status;
        this.code = status.value();
        this.timestamp = LocalDateTime.now().toString();
    }
}
