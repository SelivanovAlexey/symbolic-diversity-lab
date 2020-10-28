package ru.nntu.vst.dips.symbolicdiversity.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LabResponse {
    @JsonProperty("values")
    List<Model> values;
    @JsonProperty("n")
    Integer textLength;
    @JsonProperty("maxCm")
    Double maxEntropyEstimate;
    @JsonProperty("mu")
    Double symbolicDiversityValue;
}
