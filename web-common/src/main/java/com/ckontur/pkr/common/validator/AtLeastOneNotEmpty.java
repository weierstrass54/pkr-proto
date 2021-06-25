package com.ckontur.pkr.common.validator;

import io.vavr.collection.Stream;
import io.vavr.control.Option;
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
import java.util.List;
import java.util.Map;
import java.util.*;
import java.util.stream.Collectors;

import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;

@Constraint(validatedBy = AtLeastOneNotEmpty.AtLeastOneNotEmptyValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AtLeastOneNotEmpty {
    String message() default "По крайней мере одно поле должно быть непустым.";
    String[] fields() default {};
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    class AtLeastOneNotEmptyValidator implements ConstraintValidator<AtLeastOneNotEmpty, Object> {
        private List<String> fields;

        @Override
        public void initialize(AtLeastOneNotEmpty constraintAnnotation) {
            this.fields = Arrays.asList(constraintAnnotation.fields());
        }

        @Override
        public boolean isValid(Object value, ConstraintValidatorContext context) {
            boolean isInvalid = Try.sequence(
                getPropertyDescriptors(value).stream().map(pd ->
                    Try.of(() -> pd.getReadMethod().invoke(pd))
                        .map(Option::of)
                        .filter(Option::isDefined)
                        .map(Option::get)
                        .map(obj -> Match(obj).of(
                            Case($(instanceOf(CharSequence.class)), CharSequence::isEmpty),
                            Case($(instanceOf(Collection.class)), Collection::isEmpty),
                            Case($(instanceOf(Map.class)), Map::isEmpty),
                            Case($(instanceOf(Object[].class)), objs -> objs.length == 0),
                            Case($(), __ -> false)
                        ))
                    )
                .collect(Collectors.toList())
            ).getOrElse(Seq(true)).fold(true, (a, b) -> a && b);
            return !isInvalid;
        }

        private List<PropertyDescriptor> getPropertyDescriptors(Object value) {
            return Option.of(value).map(v -> {
                PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(v.getClass());
                return !fields.isEmpty() ? Stream.of(pds).filter(pd -> fields.contains(pd.getName())).collect(Collectors.toList()) :
                    Arrays.asList(pds);
            }).getOrElse(Collections.emptyList());
        }
    }
}
