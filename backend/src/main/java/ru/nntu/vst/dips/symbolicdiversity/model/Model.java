package ru.nntu.vst.dips.symbolicdiversity.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Model {
    @JsonProperty("m")
    Integer windowSize;
    @JsonProperty("Cm")
    Double entropyEstimate;
    @JsonProperty("dCm")
    Double reverseDifference;
}
