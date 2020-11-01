package ru.nntu.vst.dips.symbolicdiversity;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.nntu.vst.dips.symbolicdiversity.config.TestConfig;
import ru.nntu.vst.dips.symbolicdiversity.model.Language;
import ru.nntu.vst.dips.symbolicdiversity.model.ProcessingType;
import ru.nntu.vst.dips.symbolicdiversity.rest.Controller;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = Controller.class)
@ContextConfiguration(classes = TestConfig.class)
public class ControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Controller controller;

    @Autowired
    @Qualifier("helloAllWorld")
    private MockMultipartFile mockMultipartFile;

    @Test
    public void contextLoads() {
        Assertions.assertThat(controller).isNotNull();
    }

    @Test
    public void controllerTest() throws Exception {
        mockMvc.perform(multipart("/api/estimates")
                .file(mockMultipartFile)
                .param("lang", Language.EN.name())
                .param("type", ProcessingType.symbols.name()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void controllerMissingParametersTest() throws Exception {
        mockMvc.perform(multipart("/api/estimates")
                .file(mockMultipartFile))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void controllerNoFileTest() throws Exception {
        mockMvc.perform(multipart("/api/estimates"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void controllerWrongParametersTest() throws Exception {
        mockMvc.perform(multipart("/api/estimates")
                .file(mockMultipartFile)
                .param("lang", "CZ")
                .param("type", ProcessingType.symbols.name()))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void controllerWrongContentTypeTest() throws Exception {
        mockMvc.perform(multipart("/api/estimates")
                .file(new MockMultipartFile("file", "test.txt", MediaType.APPLICATION_PDF_VALUE, "hello".getBytes()))
                .param("lang", Language.EN.name())
                .param("type", ProcessingType.words.name()))
                .andDo(print())
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    public void controllerSmallFileTest() throws Exception {
        mockMvc.perform(multipart("/api/estimates")
                .file(new MockMultipartFile("file", "test.txt", MediaType.TEXT_PLAIN_VALUE, "hello".getBytes()))
                .param("lang", Language.EN.name())
                .param("type", ProcessingType.words.name()))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}
