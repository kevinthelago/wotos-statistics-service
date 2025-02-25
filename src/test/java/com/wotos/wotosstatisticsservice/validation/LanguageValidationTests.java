package com.wotos.wotosstatisticsservice.validation;

import com.wotos.wotosstatisticsservice.client.wot.WotAccountsFeignClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
