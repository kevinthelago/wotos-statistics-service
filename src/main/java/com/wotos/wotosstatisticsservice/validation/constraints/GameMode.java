package com.wotos.wotosstatisticsservice.validation.constraints;

import com.wotos.wotosstatisticsservice.validation.GameModeValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Constraint(validatedBy = GameModeValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GameMode {
    String message() default "Invalid GameModes";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
