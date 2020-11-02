package ru.nntu.vst.dips.symbolicdiversity.rest.errorhandling;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
class Error {
    String timestamp;
    Integer code;
    String status;
    String error;
}
