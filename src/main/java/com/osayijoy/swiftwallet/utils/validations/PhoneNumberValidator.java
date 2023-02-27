package com.zurum.lanefinance.utils.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.regex.Pattern;

@Component
public class PhoneNumberValidator implements ConstraintValidator<PhoneNumber, String> {

    @Override
    public boolean isValid(String phoneNumber, ConstraintValidatorContext constraintValidatorContext) {
        String regex = "[0-9]{10,16}";
        Pattern pat = Pattern.compile(regex);

        if (Objects.isNull(phoneNumber)) {
            return false;
        }
        return pat.matcher(phoneNumber.replace("+", "")).matches();
    }
}
