package ru.nntu.vst.dips.symbolicdiversity.service;

import ru.nntu.vst.dips.symbolicdiversity.model.Language;
import ru.nntu.vst.dips.symbolicdiversity.model.Model;

import java.util.List;

public interface SymbolService {
    List<Model> calculateEntropyWords(String input, Language lang, Integer maxWindowSize);

    List<Model> calculateEntropySymbols(String input, Integer maxWindowSize);

    Double getMaxDiffEntropyValue(List<Model> list);

    Double getSymbolicDiversityValue(Double entropy);

    Integer getLength();
}
