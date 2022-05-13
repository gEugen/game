package com.game.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class IdValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return Long.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Long id = (Long) target;

        if (id == null) {
            errors.rejectValue("id", "value.null");
        }

        if (id <= 0) {
            errors.rejectValue("id", "id.notPositive");
        }
    }
}
