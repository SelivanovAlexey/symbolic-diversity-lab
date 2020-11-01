package ru.nntu.vst.dips.symbolicdiversity.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

@TestConfiguration
@ComponentScan("ru.nntu.vst.dips.symbolicdiversity")
public class TestConfig {

    @Bean
    @Qualifier("helloAllWorld")
    public MockMultipartFile mockFileHello(){
        return new MockMultipartFile("file", "test.txt",
                MediaType.TEXT_PLAIN_VALUE, "Hello all world".getBytes());
    }

    @Bean
    @Qualifier("single")
    public MockMultipartFile mockFileSingleLetter(){
        return new MockMultipartFile("file", "test.txt",
                MediaType.TEXT_PLAIN_VALUE, "qqqqqqqqqqqqqqqq".getBytes());
    }

    @Bean
    @Qualifier("alphabet")
    public MockMultipartFile mockFileAlphabet(){
        return new MockMultipartFile("file", "test.txt",
                MediaType.TEXT_PLAIN_VALUE, "abcdefghijklmnopqrstuvwxyz".getBytes());
    }
}
