
package acme.constraints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FlightNumberValidator.class)
@ReportAsSingleViolation

@NotBlank
@Pattern(regexp = "^[A-Z]{2,3}\\d{4}$")

public @interface ValidFlightNumber {

	String message() default "El número de vuelo debe ser un código de 2 o 3 letras seguidas de 4 dígitos.";

	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};

}
