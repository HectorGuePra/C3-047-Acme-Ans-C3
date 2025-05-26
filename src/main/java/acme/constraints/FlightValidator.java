
package acme.constraints;

import java.util.List;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.entities.flight.Flight;
import acme.entities.flight.FlightRepository;
import acme.entities.legs.Leg;

@Validator
public class FlightValidator extends AbstractValidator<ValidFlight, Flight> {

	@Autowired
	private FlightRepository repository;


	@Override
	protected void initialise(final ValidFlight annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Flight flight, final ConstraintValidatorContext context) {
		assert context != null;

		if (flight == null)
			return true;

		List<Leg> legs = this.repository.legsDuringFlight(flight.getId());
		boolean hasLegs = legs != null && !legs.isEmpty();
		if (!flight.isDraftMode()) {
			super.state(context, hasLegs, "tag", "acme.validation.flight.zero-legs.message");
			for (Leg leg : legs) {
				boolean isPublished = !leg.isDraftMode();
				super.state(context, isPublished, "tag", "acme.validation.flight.cant-be-publish.message");
			}
		}

		return !super.hasErrors(context);
	}
}
