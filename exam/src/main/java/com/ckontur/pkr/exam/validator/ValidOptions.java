package com.ckontur.pkr.exam.validator;

import com.ckontur.pkr.exam.model.question.Option;
import com.ckontur.pkr.exam.model.question.Question;
import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import io.vavr.collection.Set;
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
                .flatMap(rm -> Try.of(() -> ((List<?>)rm.invoke(value)).map(o -> (Option) o)))
                .getOrElse(List.empty());
        }

        private boolean isValidChoiceOptions(List<Option> options) {
            return List.of(
                options.size() > 1,
                options.forAll(o -> HashSet.of(Option.Type.ANY, Option.Type.LIST).contains(o.getType()))
            ).fold(true, (a, b) -> a && b);
        }

        private boolean isValidMatchOptions(List<Option> options) {
            return List.of(
                options.size() >= 4,
                options.forAll(o -> HashSet.of(Option.Type.LEFT, Option.Type.RIGHT).contains(o.getType())),
                options.partition(o -> o.getType() == Option.Type.LEFT).apply((a, b) -> a.size() == b.size())
            ).fold(true, (a, b) -> a && b);
        }
    }
}
