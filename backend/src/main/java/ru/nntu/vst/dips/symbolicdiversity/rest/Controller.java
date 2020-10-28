package ru.nntu.vst.dips.symbolicdiversity.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.web.multipart.MultipartException;
import ru.nntu.vst.dips.symbolicdiversity.model.LabResponse;
import ru.nntu.vst.dips.symbolicdiversity.model.Model;
import ru.nntu.vst.dips.symbolicdiversity.service.SymbolService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@Slf4j
public class Controller {

    @Autowired
    private SymbolService service;

    @PostMapping(value = "/estimates", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public LabResponse getEstimates(@RequestPart(value = "file") MultipartFile file,
                                    @RequestParam String lang,
                                    @RequestParam(value = "maxWindowSize", required = false, defaultValue = "30")
                                            Integer maxWindowSize) throws IOException {
        if (!isPlaintextType(file))
            throw new MultipartException("Wrong file content type. Only text/plain is applicable");

        log.debug("started parsing file with name {}", file.getName());
        String input = IOUtils.toString(file.getInputStream(), StandardCharsets.UTF_8);

        List<Model> result = service.calculateEntropy(input, lang, maxWindowSize);
        Double maxEntropy = service.getMaxEntropyValue(result);
        Double diversityValue = service.getSymbolicDiversityValue(maxEntropy);

        return LabResponse.builder()
                .values(result)
                .maxEntropyEstimate(maxEntropy)
                .symbolicDiversityValue(diversityValue)
                .build();

    }

    private boolean isPlaintextType(@NonNull MultipartFile file) {
        return Optional.ofNullable(file.getContentType())
                .orElseThrow(() -> new MultipartException("No input file"))
                .equals(MediaType.TEXT_PLAIN_VALUE);
    }
}
