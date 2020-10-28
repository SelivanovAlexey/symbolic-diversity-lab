package ru.nntu.vst.dips.symbolicdiversity.service;

import ru.nntu.vst.dips.symbolicdiversity.model.Model;

import java.util.List;

public interface SymbolService {
    List<Model> calculateEntropy(String input, String lang, Integer maxWindowSize);

    Double getMaxEntropyValue(List<Model> list);

    Double getSymbolicDiversityValue(Double entropy);
}
