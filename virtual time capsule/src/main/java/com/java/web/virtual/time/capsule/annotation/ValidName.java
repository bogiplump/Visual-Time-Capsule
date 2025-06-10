package com.java.web.virtual.time.capsule.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = {})
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@NotNull(message = "{NotNull.message}")
@NotBlank(message = "{NotBlank.message}")
@Pattern(
    regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ '-]{1,50}$",
    message = "{InvalidName.message}"
)
@Size(max = 20)
public @interface ValidName {
    String message() default "Invalid name format";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}