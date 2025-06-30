
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

		boolean result = true;

		if (flightCrewMember == null) {
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
			return false;
		}

		// Validación del Employee Code
		if (flightCrewMember.getEmployeeCode() == null) {
			super.state(context, false, "employeeCode", "acme.validation.flightcrewmember.employeeCode.null.message");
			result = false;
		} else if (StringHelper.isBlank(flightCrewMember.getEmployeeCode())) {
			super.state(context, false, "employeeCode", "acme.validation.flightcrewmember.employeeCode.blank.message");
			result = false;
		} else {
			String employeeCode = flightCrewMember.getEmployeeCode();
			
			// Validar formato del código de empleado
			if (!employeeCode.matches("^[A-Z]{2,3}\\d{6}$")) {
				super.state(context, false, "employeeCode", "acme.validation.flightcrewmember.employeeCode.pattern.message");
				result = false;
			}
			
			// Validar longitud del código de empleado
			if (employeeCode.length() < 8 || employeeCode.length() > 9) {
				super.state(context, false, "employeeCode", "acme.validation.flightcrewmember.employeeCode.length.message");
				result = false;
			}
			
			// Validar unicidad del código de empleado
			FlightCrewMember existingFlightCrewMember = this.repository.findFlightCrewMemberByEmployeeCode(employeeCode);
			boolean uniqueEmployeeCode = existingFlightCrewMember == null || existingFlightCrewMember.equals(flightCrewMember);
			if (!uniqueEmployeeCode) {
				super.state(context, false, "employeeCode", "acme.validation.flightcrewmember.employeeCode.duplicate.message");
				result = false;
			}

			// Validar que las iniciales coincidan (si hay información de identidad disponible)
			if (flightCrewMember.getIdentity() != null) {
				String name = flightCrewMember.getIdentity().getName();
				String surname = flightCrewMember.getIdentity().getSurname();
				
				if (name != null && surname != null && name.length() > 0 && surname.length() > 0) {
					char firstInitial = Character.toUpperCase(name.charAt(0));
					char secondInitial = Character.toUpperCase(surname.charAt(0));
					
					boolean correctInitials = employeeCode.charAt(0) == firstInitial && employeeCode.charAt(1) == secondInitial;
					if (!correctInitials) {
						super.state(context, false, "employeeCode", "acme.validation.flightcrewmember.employeeCode.initials.message");
						result = false;
					}
				}
			}
		}

		// Validación del número de teléfono
		if (flightCrewMember.getPhoneNumber() == null) {
			super.state(context, false, "phoneNumber", "acme.validation.flightcrewmember.phoneNumber.null.message");
			result = false;
		} else if (StringHelper.isBlank(flightCrewMember.getPhoneNumber())) {
			super.state(context, false, "phoneNumber", "acme.validation.flightcrewmember.phoneNumber.null.message");
			result = false;
		} else if (!flightCrewMember.getPhoneNumber().matches("^\\+?\\d{6,15}$")) {
			super.state(context, false, "phoneNumber", "acme.validation.flightcrewmember.phoneNumber.format.message");
			result = false;
		}

		// Validación de las habilidades lingüísticas
		if (flightCrewMember.getLanguageSkills() == null) {
			super.state(context, false, "languageSkills", "acme.validation.flightcrewmember.languageSkills.null.message");
			result = false;
		} else if (StringHelper.isBlank(flightCrewMember.getLanguageSkills())) {
			super.state(context, false, "languageSkills", "acme.validation.flightcrewmember.languageSkills.null.message");
			result = false;
		} else if (flightCrewMember.getLanguageSkills().length() > 255) {
			super.state(context, false, "languageSkills", "acme.validation.flightcrewmember.languageSkills.length.message");
			result = false;
		}

		// Validación del estado de disponibilidad
		if (flightCrewMember.getAvailabilityStatus() == null) {
			super.state(context, false, "availabilityStatus", "acme.validation.flightcrewmember.availabilityStatus.null.message");
			result = false;
		}

		// Validación de la aerolínea
		if (flightCrewMember.getAirline() == null) {
			super.state(context, false, "airline", "acme.validation.flightcrewmember.airline.null.message");
			result = false;
		}

		// Validación del salario
		if (flightCrewMember.getSalary() == null) {
			super.state(context, false, "salary", "acme.validation.flightcrewmember.salary.null.message");
			result = false;
		} else {
			// Validar que el salario tenga una moneda válida
			String currency = flightCrewMember.getSalary().getCurrency();
			if (currency == null || currency.trim().isEmpty()) {
				super.state(context, false, "salary", "acme.validation.flightcrewmember.salary.format.message");
				result = false;
			} else if (!currency.matches("^(EUR|USD|GBP)$")) {
				super.state(context, false, "salary", "acme.validation.flightcrewmember.salary.currency.message");
				result = false;
			}
			
			// Validar el rango del salario
			double amount = flightCrewMember.getSalary().getAmount();
			if (amount < 0.0 || amount > 1000000.0) {
				super.state(context, false, "salary", "acme.validation.flightcrewmember.salary.amount.message");
				result = false;
			}
		}

		// Validación de años de experiencia
		if (flightCrewMember.getYearsOfExperience() != null) {
			int years = flightCrewMember.getYearsOfExperience();
			if (years < 0 || years > 120) {
				super.state(context, false, "yearsOfExperience", "acme.validation.flightcrewmember.yearsOfExperience.range.message");
				result = false;
			}
		}

		return result;
	}
}
