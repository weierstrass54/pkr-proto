package com.ckontur.pkr.common.validator;

import io.vavr.control.Option;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = Login.LoginValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Login {
    String message() default "Логин не должен содержать пробелов и быть длиной от 3 до 50 символов.";
    boolean nullable() default false;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    class LoginValidator implements ConstraintValidator<Login, String> {
        private boolean nullable;

        @Override
        public void initialize(Login constraintAnnotation) {
            this.nullable = constraintAnnotation.nullable();
        }

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            return Option.of(value)
                .map(v -> v.length() >= 3 && v.length() <= 50 && v.indexOf(' ') == -1)
                .getOrElse(nullable);
        }
    }
}
