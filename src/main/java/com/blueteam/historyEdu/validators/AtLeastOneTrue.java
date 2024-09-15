package com.blueteam.historyEdu.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AtLeastOneTrueValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AtLeastOneTrue {

    String message() default "At least one convenience must be selected";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
