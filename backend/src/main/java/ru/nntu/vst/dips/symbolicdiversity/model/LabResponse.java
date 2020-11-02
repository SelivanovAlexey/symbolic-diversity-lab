package ru.nntu.vst.dips.symbolicdiversity.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LabResponse {
    @JsonProperty("values")
    List<Model> values;
    @JsonProperty("n")
    Integer textLength;
    @JsonProperty("maxdCm")
    Double maxEntropyEstimate;
    @JsonProperty("mu")
    Double symbolicDiversityValue;
}
