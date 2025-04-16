
package acme.constraints;

import java.util.List;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.entities.airport.Airport;
import acme.entities.airport.AirportRepository;

@Validator
public class AirportValidator extends AbstractValidator<ValidAirport, Airport> {

	@Autowired
	private AirportRepository repository;


	@Override
	protected void initialise(final ValidAirport annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Airport airport, final ConstraintValidatorContext context) {

		assert context != null;

		boolean result;

		if (airport == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else {
			String iataCode = airport.getIataCode();

			if (iataCode == null)
				super.state(context, false, "*", "javax.validation.constraints.NotNull.message");

			List<Airport> airports = this.repository.findAllAirports();
			boolean isUnique = airports.stream().noneMatch(a -> a.getIataCode().equals(iataCode) && !a.equals(airport));

			if (!isUnique)
				super.state(context, false, "*", "acme.validation.airport.iata-code.message");
		}

		result = !super.hasErrors(context);

		return result;

	}

}
