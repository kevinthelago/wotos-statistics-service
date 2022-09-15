package com.wotos.wotosstatisticsservice.validation;

import com.wotos.wotosstatisticsservice.util.feign.wot.WotAccountsFeignClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class LanguageValidationTests {

    @Mock
    private WotAccountsFeignClient wotAccountsFeignClient;
    @InjectMocks
    private LanguageValidator languageValidator;

    @Test
    public void languageValidatorTests() {

    }

}
