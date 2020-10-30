package ru.nntu.vst.dips.symbolicdiversity.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.tartarus.snowball.SnowballProgram;
import org.tartarus.snowball.ext.PorterStemmer;
import org.tartarus.snowball.ext.RussianStemmer;
import ru.nntu.vst.dips.symbolicdiversity.model.Language;
import ru.nntu.vst.dips.symbolicdiversity.model.Model;
import ru.nntu.vst.dips.symbolicdiversity.utils.CumulativeMap;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SymbolServiceImpl implements SymbolService {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private final CumulativeMap map;

    private SnowballProgram stemmer;
    private Integer length;

    public SymbolServiceImpl(CumulativeMap map) {
        this.map = map;
    }

    private SnowballProgram getCorrespondingStemmer(Language lang) {
        switch (lang) {
            case RU:
                return context.getBean(RussianStemmer.class);
            case EN:
            default:
                return context.getBean(PorterStemmer.class);
        }
    }

    @Override
    public List<Model> calculateEntropyWords(String rawInput, Language lang, Integer windowSize) {
        String input = prepareInput(rawInput);
        stemmer = getCorrespondingStemmer(lang);
        List<String> inputArray = getWordsAlphabet(input);
        length = inputArray.size();
        return proceed(" ", inputArray, windowSize);
    }

    @Override
    public List<Model> calculateEntropySymbols(String rawInput, Integer windowSize) {
        String input = prepareInput(rawInput);
        List<String> inputArray = Arrays.asList(input.split(""));
        length = inputArray.size();
        return proceed("", inputArray, windowSize);
    }

    public Double getMaxDiffEntropyValue(List<Model> list) {
        return list.stream()
                .map(Model::getReverseDifference)
                .filter(Objects::nonNull)
                .max(Double::compareTo)
                .orElseThrow(NoSuchElementException::new);
    }

    public Double getSymbolicDiversityValue(Double entropy) {
        return 1.0 / entropy;
    }

    @Override
    public Integer getLength() {
        return length;
    }

    private String prepareInput(String rawInput) {
        return rawInput.replaceAll("[^\\p{L}\\p{Z}]", "").toLowerCase();
    }

    private List<String> getWordsAlphabet(String input) {
        return Arrays.stream(input.split(" "))
                .map(this::stem)
                .collect(Collectors.toList());
    }

    private String stem(String input) {
        stemmer.setCurrent(input);
        stemmer.stem();
        return stemmer.getCurrent();
    }

    private List<Model> proceed(String delimiter, List<String> words, Integer windowSize) {
        List<Model> entropyEstimateArray = new LinkedList<>();
        double previousCommonSum = 0;
        int alphabetPower = 0;
        for (int m = 1; m <= windowSize && m < words.size(); m++) {
            double commonSum = 0;
            for (int i = 0; i < words.size() - m + 1; i++) {
                map.put(String.join(delimiter, words.subList(i, i + m)));
            }
            if (m == 1) alphabetPower = map.size();
            for (Map.Entry<String, Double> entry : map.entrySet()) {
                double factor = (entry.getValue() / (words.size() - m + 1));
                if (factor != 1)
                    commonSum += factor * (Math.log(factor) / Math.log(Math.pow(alphabetPower, m)));
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
                .windowSize(windowSize < words.size() ? windowSize : words.size() - 1)
                .entropyEstimate(previousCommonSum)
                .build());
        return entropyEstimateArray;
    }
}
