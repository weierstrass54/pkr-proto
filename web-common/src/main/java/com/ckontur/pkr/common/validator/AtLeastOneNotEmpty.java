package com.ckontur.pkr.common.validator;

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
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
            try {
                for (PropertyDescriptor pd: getPropertyDescriptors(value)) {
                    Object obj = pd.getReadMethod().invoke(value);
                    if (obj != null) {
                        if (obj instanceof CharSequence) {
                            return !((CharSequence) obj).isEmpty();
                        }
                        if (obj instanceof Collection<?>) {
                            return !((Collection<?>) obj).isEmpty();
                        }
                        if (obj instanceof Map<?, ?>) {
                            return !((Map<?, ?>) obj).isEmpty();
                        }
                        if (obj instanceof Object[]) {
                            return ((Object[]) obj).length > 0;
                        }
                        return true;
                    }
                }
                return false;
            }
            catch (Throwable t) {
                return false;
            }
        }

        private List<PropertyDescriptor> getPropertyDescriptors(Object value) {
            if (value == null) {
                return Collections.emptyList();
            }
            PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(value.getClass());
            if (fields.isEmpty()) {
                return Arrays.asList(pds);
            }
            return Stream.of(pds).filter(pd -> fields.contains(pd.getName())).collect(Collectors.toList());
        }
    }
}
