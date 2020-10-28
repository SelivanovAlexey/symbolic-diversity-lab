package ru.nntu.vst.dips.symbolicdiversity.rest.errorhandling;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Error {
    String timestamp;
    Integer code;
    String status;
    String error;
}
