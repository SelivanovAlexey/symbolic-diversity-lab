package ru.nntu.vst.dips.symbolicdiversity.rest.errorhandling;

import org.springframework.http.HttpStatus;

public class UnsupportedMediaTypeException extends ApiException{

    public UnsupportedMediaTypeException(String message) {
        super(message, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }
}

