
package acme.constraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class FlightNumberValidator implements ConstraintValidator<ValidFlightNumber, String> {

	private static final String FLIGHT_NUMBER_PATTERN = "^[A-Z]{2,3}\\d{4}$";


	@Override
	public boolean isValid(final String flightNumber, final ConstraintValidatorContext context) {
		return flightNumber != null && flightNumber.matches(FlightNumberValidator.FLIGHT_NUMBER_PATTERN);
	}
}
