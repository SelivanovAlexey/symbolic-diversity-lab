package ru.nntu.vst.dips.symbolicdiversity;

import org.apache.commons.io.IOUtils;
import org.apache.lucene.search.MultiCollectorManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import ru.nntu.vst.dips.symbolicdiversity.config.TestConfig;
import ru.nntu.vst.dips.symbolicdiversity.model.Language;
import ru.nntu.vst.dips.symbolicdiversity.model.Model;
import ru.nntu.vst.dips.symbolicdiversity.service.SymbolService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ContextConfiguration(classes = TestConfig.class)
public class SymbolServiceTests {

    @Autowired
    public SymbolService service;

    @Qualifier("helloAllWorld")
    @Autowired
    public MockMultipartFile mockHelloAllWorldFile;

    @Qualifier("single")
    @Autowired
    public MockMultipartFile mockMultipartFileSingle;

    @Qualifier("alphabet")
    @Autowired
    public MockMultipartFile mockMultipartFileAlphabet;

    @Test
    void testHelloAllWorldWords() throws IOException {
        List<Model> result = service.calculateEntropyWords(
                IOUtils.toString(mockHelloAllWorldFile.getInputStream(), StandardCharsets.UTF_8),
                Language.EN,
                30);
        assertThat(service.getMaxDiffEntropyValue(result), is(equalTo(0.6845351232142713)));
    }

    @Test
    void testSingleSymbols() throws IOException {
        List<Model> result = service.calculateEntropySymbols(
                IOUtils.toString(mockMultipartFileSingle.getInputStream(), StandardCharsets.UTF_8),
                30);

        assertThat(result.stream()
                        .map(Model::getEntropyEstimate)
                        .collect(Collectors.toList()),
                everyItem(anyOf(is(equalTo(0.0)), is(equalTo(-0.0)))));

        assertThat(service.getMaxDiffEntropyValue(result), is(equalTo(0.0)));
    }

    @Test
    void testAlphabetSymbols() throws IOException {
        List<Model> result = service.calculateEntropySymbols(
                IOUtils.toString(mockMultipartFileAlphabet.getInputStream(), StandardCharsets.UTF_8),
                30);
        assertThat(result.stream().findFirst().map(Model::getEntropyEstimate)
                .orElse(0.0), is(equalTo(0.9999999999999996)));

        assertThat(service.getMaxDiffEntropyValue(result), is(equalTo(0.5060189611780342)));
    }

}
