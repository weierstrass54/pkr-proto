package com.ckontur.pkr.exam.validator;

import com.ckontur.pkr.exam.model.Option;
import com.ckontur.pkr.exam.model.Question;
import io.vavr.control.Try;
import org.springframework.beans.BeanUtils;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.beans.PropertyDescriptor;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Constraint(validatedBy = ValidOptions.ValidOptionsValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidOptions {
    String message() default "Список ответов имеет несовместимый тип с типом вопроса.";
    String typeField();
    String optionsField();
    boolean required() default true;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    class ValidOptionsValidator implements ConstraintValidator<ValidOptions, Object> {
        private String typeField;
        private String optionsField;
        private boolean required;

        @Override
        public void initialize(ValidOptions constraintAnnotation) {
            this.typeField = constraintAnnotation.typeField();
            this.optionsField = constraintAnnotation.optionsField();
            this.required = constraintAnnotation.required();
        }

        @Override
        public boolean isValid(Object value, ConstraintValidatorContext context) {
            return getType(value)
                .map(t -> {
                    List<Option> options = getOptions(value);
                    return switch (t) {
                        case SINGLE, MULTIPLE, SEQUENCE -> isValidChoiceOptions(options);
                        case MATCHING -> isValidMatchOptions(options);
                    };
                })
                .getOrElse(!required);
        }

        private io.vavr.control.Option<Question.Type> getType(Object value) {
            return Try.of(() -> BeanUtils.getPropertyDescriptor(value.getClass(), typeField))
                .map(PropertyDescriptor::getReadMethod)
                .flatMap(rm -> Try.of(() -> (Question.Type) rm.invoke(value)))
                .toOption();
        }

        private List<Option> getOptions(Object value) {
            return Try.of(() -> BeanUtils.getPropertyDescriptor(value.getClass(), optionsField))
                .map(PropertyDescriptor::getReadMethod)
                .flatMap(rm -> Try.of(() -> ((List<?>)rm.invoke(value)).stream().map(o -> (Option) o).collect(Collectors.toList())))
                .getOrElse(Collections.emptyList());
        }

        private boolean isValidChoiceOptions(List<Option> options) {
            return options.size() > 1 && options.stream().allMatch(
                o -> Set.of(Option.Type.ANY, Option.Type.LIST).contains(o.getType())
            );
        }

        private boolean isValidMatchOptions(List<Option> options) {
            boolean validSizeAndTypes = options.size() >= 4 && options.stream().allMatch(
                o -> Set.of(Option.Type.LEFT, Option.Type.RIGHT).contains(o.getType())
            );
            boolean validCounts = options.stream().filter(o -> o.getType() == Option.Type.LEFT).count() ==
                options.stream().filter(o -> o.getType() == Option.Type.RIGHT).count();
            return validSizeAndTypes && validCounts;
        }
    }
}
