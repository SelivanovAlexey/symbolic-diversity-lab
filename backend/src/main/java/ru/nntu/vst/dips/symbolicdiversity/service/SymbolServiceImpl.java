package ru.nntu.vst.dips.symbolicdiversity.service;

import ru.nntu.vst.dips.symbolicdiversity.model.Language;
import ru.nntu.vst.dips.symbolicdiversity.model.Model;
import ru.nntu.vst.dips.symbolicdiversity.utils.CumulativeMap;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SymbolServiceImpl implements SymbolService {

    private static final CharSequence ALPHABET_EN = "abcdefghijklmnopqrstuvwxyz";
    private static final CharSequence ALPHABET_RU = "абвгдеёжзийклмнопрстуфхцчшщъыьэюя";

    private final CumulativeMap map;

    public SymbolServiceImpl(CumulativeMap map) {
        this.map = map;
    }

    private CharSequence getInputLanguage(Language lang) {
        switch (lang) {
            case RU:
                return ALPHABET_RU;
            case EN:
            default:
                return ALPHABET_EN;
        }
    }

    public List<Model> calculateEntropy(String rawInput, String lang, Integer windowSize) {
        CharSequence alphabet = getInputLanguage(Language.valueOf(lang.toUpperCase()));

        String input = prepareInput(rawInput);
        List<Model> entropyEstimateArray = new LinkedList<>();
        double previousCommonSum = 0;
        for (int m = 1; m <= windowSize; m++) {
            double commonSum = 0;
            for (int i = 0; i < input.length() - m + 1; i++) {
                map.put(input.substring(i, i + m));
            }
            for (Map.Entry<String, Double> entry : map.entrySet()) {
                double factor = (entry.getValue() / (input.length() - m + 1));
                commonSum += factor * (Math.log(factor) / Math.log(Math.pow(alphabet.length(), m)));
            }
            commonSum *= -1;

            map.clear();

            if (m == 1) {
                previousCommonSum = commonSum;
                continue;
            }
            entropyEstimateArray.add(Model.builder()
                    .windowSize(m - 1)
                    .entropyEstimate(previousCommonSum)
                    .reverseDifference(previousCommonSum - commonSum)
                    .build());

            previousCommonSum = commonSum;
        }

        entropyEstimateArray.add(Model.builder()
                .windowSize(windowSize)
                .entropyEstimate(previousCommonSum)
                .build());

        return entropyEstimateArray;
    }

    public Double getMaxEntropyValue(List<Model> list) {
        return list.stream()
                .max(Comparator.comparing(Model::getEntropyEstimate))
                .orElseThrow(NoSuchElementException::new)
                .getEntropyEstimate();
    }

    public Double getSymbolicDiversityValue(Double entropy) {
        return 1.0 / entropy;
    }

    private String prepareInput(String rawInput) {
        return rawInput.replaceAll("\\s", "").toLowerCase();
    }
}
