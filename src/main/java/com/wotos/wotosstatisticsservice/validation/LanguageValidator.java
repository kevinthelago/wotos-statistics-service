package com.wotos.wotosstatisticsservice.validation;

import com.wotos.wotosstatisticsservice.constants.Languages;
import com.wotos.wotosstatisticsservice.validation.constraints.Language;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.EnumSet;

public class LanguageValidator implements ConstraintValidator<Language, String> {
    @Override
    public boolean isValid(String code, ConstraintValidatorContext constraintValidatorContext) {
        Languages language = EnumSet.allOf(Languages.class).stream().filter(value -> value.getCode().equalsIgnoreCase(code)).findFirst().orElse(null);

        if (language != null) {
            return true;
        }

        constraintValidatorContext.buildConstraintViolationWithTemplate("Language Code: " + code + " is invalid").addConstraintViolation();
        return false;
    }
}
