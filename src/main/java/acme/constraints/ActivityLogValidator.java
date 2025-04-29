
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.entities.activityLog.ActivityLog;
import acme.entities.legs.Leg;
import acme.entities.legs.LegStatus;

@Validator
public class ActivityLogValidator extends AbstractValidator<ValidActivityLog, ActivityLog> {

	@Override
	protected void initialise(final ValidActivityLog annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final ActivityLog activityLog, final ConstraintValidatorContext context) {
		assert context != null;

		boolean result;

		if (activityLog == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else {
			Leg existingLeg;
			boolean isLegLanded;
			existingLeg = activityLog.getFlightAssignment().getLeg();

			isLegLanded = existingLeg.getStatus().equals(LegStatus.LANDED);
			super.state(context, isLegLanded, "leg", "acme.validation.activityLog.statusLeg.message");
		}

		result = !super.hasErrors(context);

		return result;
	}
}
