
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.StringHelper;
import acme.realms.flightcrewmember.FlightCrewMember;
import acme.realms.flightcrewmember.flightCrewMemberRepository;

@Validator
public class FlightCrewMemberValidator extends AbstractValidator<ValidFlightCrewMember, FlightCrewMember> {

	@Autowired
	private flightCrewMemberRepository repository;


	@Override
	public void initialise(final ValidFlightCrewMember annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final FlightCrewMember flightCrewMember, final ConstraintValidatorContext context) {
		assert context != null;

		boolean result;

		if (flightCrewMember == null || flightCrewMember.getEmployeeCode() == null || flightCrewMember.getIdentity() == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");

		else if (StringHelper.isBlank(flightCrewMember.getEmployeeCode()))
			super.state(context, false, "identifier", "javax.validation.constraints.NotBlank.message");
		else {
			// ------------- ponerlo en el servicio ----------------------------
			{
				boolean uniqueFlightCrewMember;
				FlightCrewMember existingFlightCrewMember;

				existingFlightCrewMember = this.repository.findFlightCrewMemberByEmployeeCode(flightCrewMember.getEmployeeCode());
				uniqueFlightCrewMember = existingFlightCrewMember == null || existingFlightCrewMember.equals(flightCrewMember);

				super.state(context, uniqueFlightCrewMember, "employeeCode", "acme.validation.flightCrewMember.duplicated-employeeCode.message");
			}
			// -----------------------------------------------------------------
			{
				boolean correctEmployeeCode;

				correctEmployeeCode = flightCrewMember.getEmployeeCode().charAt(0) == flightCrewMember.getUserAccount().getIdentity().getName().charAt(0)
					&& flightCrewMember.getEmployeeCode().charAt(1) == flightCrewMember.getUserAccount().getIdentity().getSurname().charAt(0);

				super.state(context, correctEmployeeCode, "identifier", "acme.validation.flightcrewmember.identifier.message");
			}
		}

		result = !super.hasErrors(context);
		return result;
	}
}
