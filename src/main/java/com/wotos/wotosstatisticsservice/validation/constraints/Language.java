package com.wotos.wotosstatisticsservice.validation.constraints;

import com.wotos.wotosstatisticsservice.validation.LanguageValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Constraint(validatedBy = LanguageValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Language {
    String message() default "Invalid Language Code";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
