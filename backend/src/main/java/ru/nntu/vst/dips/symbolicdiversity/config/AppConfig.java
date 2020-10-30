package ru.nntu.vst.dips.symbolicdiversity.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.tartarus.snowball.ext.PorterStemmer;
import org.tartarus.snowball.ext.RussianStemmer;
import ru.nntu.vst.dips.symbolicdiversity.utils.CumulativeMap;

@Configuration
public class AppConfig {

    @Bean
    public CumulativeMap cumulativeMap() {
        return new CumulativeMap();
    }

    @Bean
    public RussianStemmer russianLightStemmer(){
        return new RussianStemmer();
    }

    @Bean
    public PorterStemmer porterStemmer(){
        return new PorterStemmer();
    }
}
