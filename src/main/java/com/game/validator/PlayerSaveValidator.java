package com.game.validator;

import com.game.entity.Player;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Calendar;

@Service
public class PlayerSaveValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Player.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Player player = (Player) target;

        if (player.getName() == null || player.getName() == "" || player.getName().length() > Player.NAME_STRING_MAX_LENGTH) {
            errors.rejectValue("name", "length.tooLong");
        }

        if (player.getTitle() == null || player.getTitle() == "" || player.getTitle().length() > Player.TITLE_STRING_MAX_LENGTH) {
            errors.rejectValue("title", "length.tooLong");
        }

        if (player.getExperience() == null || player.getExperience() <= Player.EXPERIENCE_BEFORE_MIN_VALUE ||
                player.getExperience() >= Player.EXPERIENCE_AFTER_MAX_VALUE) {
            errors.rejectValue("experience", "value.outOfRange");
        }

        if (player.getBirthday() == null || getYearOfBirthday(player) < Player.BIRTHDAY_MIN_YEAR ||
                getYearOfBirthday(player) > Player.BIRTHDAY_MAX_YEAR) {
            errors.rejectValue("birthday", "value.outOfRange");
        }
    }

    private Integer getYearOfBirthday(Player player) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(player.getBirthday());
        return calendar.get(Calendar.YEAR);
    }
}
