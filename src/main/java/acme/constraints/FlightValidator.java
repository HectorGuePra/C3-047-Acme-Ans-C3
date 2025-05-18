
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

		boolean result = true;

		if (flight == null)
			return true;

		List<Leg> legs = this.repository.legsDuringFlight(flight.getId());

		boolean hasLegs = legs != null && !legs.isEmpty();
		if (flight.getDraftMode() != null) {
			if (!flight.getDraftMode())
				super.state(context, hasLegs, "flightTag", "acme.validation.flight.zero-legs.message");

			if (!flight.getDraftMode())
				for (Leg leg : legs) {
					boolean isPublished = !leg.isDraftMode();
					super.state(context, isPublished, "flightTag", "acme.validation.flight.cant-be-publish.message");
				}
		}

<<<<<<< HEAD
=======
		if (flight.getCost() != null)
			super.state(context, flight.getCost().getCurrency().equals("EUR"), "cost", "acme.validation.flight.cost-euro.message");

>>>>>>> 59f37b926541d364dc67bb8bb8396922fa92ded8
		result = !super.hasErrors(context);
		return result;
	}
}
