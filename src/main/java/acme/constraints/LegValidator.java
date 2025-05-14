
package acme.constraints;

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
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
			return false;
		}

		else {
			if (leg.getFlightNumber() != null) {
				boolean uniqueLeg;
				Leg existingLeg;
				existingLeg = this.repository.getLegFromFlightNumber(leg.getFlightNumber());
				uniqueLeg = existingLeg == null || existingLeg.equals(leg);
				super.state(context, uniqueLeg, "flightNumber", "acme.validation.leg.flight-number-in-use.message");
			}

			if (leg.getScheduledDeparture() != null && leg.getScheduledArrival() != null)
				if (MomentHelper.isAfterOrEqual(leg.getScheduledDeparture(), leg.getScheduledArrival()))
					super.state(context, false, "scheduledDeparture", "acme.validation.leg.date.message");

			if (leg.getAircraft() != null && leg.getFlightNumber() != null && leg.getFlightNumber().length() >= 3) {
				String legFlightNumber = leg.getFlightNumber();
				String IataFlight = legFlightNumber.substring(0, 3);
				String IataAirline = leg.getAircraft().getAirline().getIataCode();
				boolean validLeg = StringHelper.isEqual(IataFlight, IataAirline, true);
				super.state(context, validLeg, "flightNumber", "acme.validation.leg.flight-number-incorrect.message");
			}

			if (leg.getFlight() != null && leg.getScheduledDeparture() != null && leg.getScheduledArrival() != null) {
				boolean isLegOverlapping = this.repository.isLegOverlapping(leg.getId(), leg.getFlight().getId(), leg.getScheduledDeparture(), leg.getScheduledArrival());
				super.state(context, !isLegOverlapping, "scheduledDeparture", "acme.validation.leg.overlapping-legs.message");
			}
			if (leg.getDepartureAirport() != null && leg.getArrivalAirport() != null) {
				boolean sameAirport = leg.getDepartureAirport().getId() == leg.getArrivalAirport().getId();
				super.state(context, !sameAirport, "airportDeparture", "acme.validation.leg.same-airport.error");
			}

			result = !super.hasErrors(context);

			return result;
		}
	}
}
