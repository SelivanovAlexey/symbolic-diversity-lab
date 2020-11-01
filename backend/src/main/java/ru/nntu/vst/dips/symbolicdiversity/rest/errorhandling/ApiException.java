package ru.nntu.vst.dips.symbolicdiversity.rest.errorhandling;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class ApiException extends RuntimeException {

    private String timestamp;
    private Integer code;
    private HttpStatus status;

    public ApiException(String message, HttpStatus status) {
        super(message);
        this.status = status;
        this.code = status.value();
        this.timestamp = LocalDateTime.now().toString();
    }
}
