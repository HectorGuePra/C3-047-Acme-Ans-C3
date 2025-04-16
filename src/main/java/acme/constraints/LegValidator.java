
package acme.constraints;

import java.util.List;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.MomentHelper;
import acme.client.helpers.StringHelper;
import acme.entities.legs.Leg;
import acme.entities.legs.LegRepository;

@Validator
public class LegValidator extends AbstractValidator<ValidLeg, Leg> {

	@Autowired
	private LegRepository repository;


	@Override
	protected void initialise(final ValidLeg annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Leg leg, final ConstraintValidatorContext context) {

		assert context != null;

		boolean result;
		if (leg == null) {
			super.state(context, false, "leg", "javax.validation.constraints.NotNull.message");
			return false;
		}

		if (leg == null || leg.getAircraft() == null || leg.getScheduledArrival() == null || leg.getScheduledDeparture() == null) {
			super.state(context, false, "Aircraft", "javax.validation.constraints.NotNull.message");
			return false;
		}

		if (leg.getScheduledDeparture() == null) {
			super.state(context, false, "scheduledDeparture", "javax.validation.constraints.NotNull.message");
			return false;
		}

		if (leg.getScheduledArrival() == null || leg.getScheduledDeparture() == null) {
			super.state(context, false, "scheduledArrival", "javax.validation.constraints.NotNull.message");
			return false;
		}

		else {
			{

				Leg existingLeg = this.repository.getLegFromFlightNumber(leg.getFlightNumber());

				boolean uniqueLeg = existingLeg == null || existingLeg.equals(leg);

				super.state(context, uniqueLeg, "flightNumber", "acme.validation.legs.flight-number.message");
			}
			{
				if (MomentHelper.isAfterOrEqual(leg.getScheduledDeparture(), leg.getScheduledArrival()))
					super.state(context, false, "scheduledDeparture", "acme.validation.legs.departure-arrival-date.message");

			}
			{

				String legFlightNumber = leg.getFlightNumber();

				if (StringHelper.isBlank(legFlightNumber))
					super.state(context, false, "flightNumber", "javax.validation.constraints.NotBlank.message");

				String iataFlightNumberCode = legFlightNumber.substring(0, 3);

				String iatairlineCode = leg.getAircraft().getAirline().getIataCode();

				boolean validLeg = StringHelper.isEqual(iataFlightNumberCode, iatairlineCode, true);

				super.state(context, validLeg, "flightNumber", "acme.validation.legs.flight-number.message");

			}
			List<Leg> allLegs = this.repository.findAllLegs();
			for (Leg otherLeg : allLegs)
				if (!otherLeg.equals(leg) && otherLeg.getAircraft() != null && otherLeg.getAircraft().equals(leg.getAircraft()))
					if (leg.getScheduledDeparture().before(otherLeg.getScheduledArrival()) && leg.getScheduledArrival().after(otherLeg.getScheduledDeparture())) {
						super.state(context, false, "aircraft", "acme.validation.legs.aircraft-in-use.message");
						break;
					}
		}

		{
			if (leg.getDepartureAirport() != null && leg.getArrivalAirport() != null) {
				String originCity = leg.getDepartureAirport().getCity();
				String destinationCity = leg.getArrivalAirport().getCity();
				if (originCity != null && destinationCity != null)
					if (originCity.equalsIgnoreCase(destinationCity))
						super.state(context, false, "arrivalAirport", "acme.validation.legs.same-city.error");
			}
		}

		result = !super.hasErrors(context);

		return result;
	}
}
