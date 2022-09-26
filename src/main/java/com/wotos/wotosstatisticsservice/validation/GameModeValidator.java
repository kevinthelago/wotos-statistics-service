package com.wotos.wotosstatisticsservice.validation;

import com.wotos.wotosstatisticsservice.constants.GameModes;
import com.wotos.wotosstatisticsservice.validation.constraints.GameMode;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.EnumSet;

public class GameModeValidator implements ConstraintValidator<GameMode, String[]> {
    @Override
    public boolean isValid(String[] strings, ConstraintValidatorContext constraintValidatorContext) {
        if (strings != null) {
            for (String string : strings) {
                GameModes gameMode = EnumSet.allOf(GameModes.class).stream().filter(value -> value.getName().equals(string)).findFirst().orElse(null);

                if (gameMode == null) {
                    constraintValidatorContext.buildConstraintViolationWithTemplate("Game mode: " + string + " is invalid").addConstraintViolation();
                    return false;
                }
            }
        }

        return true;
    }
}
