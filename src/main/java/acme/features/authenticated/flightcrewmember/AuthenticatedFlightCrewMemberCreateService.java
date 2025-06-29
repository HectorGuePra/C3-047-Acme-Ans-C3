
package acme.features.authenticated.flightcrewmember;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Authenticated;
import acme.client.components.principals.UserAccount;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airline.Airline;
import acme.realms.Consumer;
import acme.realms.Customer;
import acme.realms.Provider;
import acme.realms.flightcrewmember.FlightCrewMember;
import acme.realms.manager.Manager;
import acme.realms.technician.Technician;
import acme.realms.flightcrewmember.FlightCrewMember;

@GuiService
public class AuthenticatedFlightCrewMemberCreateService extends AbstractGuiService<Authenticated, FlightCrewMember> {

	@Autowired
	private AuthenticatedFlightCrewMemberRepository repository;


	@Override
	public void authorise() {
		boolean status;

		// Solo usuarios sin ningún rol pueden registrarse como FlightCrewMember
		status = !super.getRequest().getPrincipal().hasRealmOfType(FlightCrewMember.class) &&
				 !super.getRequest().getPrincipal().hasRealmOfType(Manager.class) &&
				 !super.getRequest().getPrincipal().hasRealmOfType(Consumer.class) &&
				 !super.getRequest().getPrincipal().hasRealmOfType(Provider.class) &&
				 !super.getRequest().getPrincipal().hasRealmOfType(Customer.class) &&
				 !super.getRequest().getPrincipal().hasRealmOfType(Technician.class);

		if (status && super.getRequest().getMethod().equals("POST")) {

			if (super.getRequest().hasData("availabilityStatus")) {
				String availabilityStatus = super.getRequest().getData("availabilityStatus", String.class);
				status = availabilityStatus.equals("AVAILABLE") || availabilityStatus.equals("ON_VACATION") || availabilityStatus.equals("ON_LEAVE");
			}

			if (status && super.getRequest().hasData("airline")) {
				Integer airlineId = super.getRequest().getData("airline", int.class);
				if (airlineId != 0) {
					Collection<Airline> availableAirlines = this.repository.findAllAirlines();
					boolean airlineIsValid = availableAirlines.stream().anyMatch(airline -> airline.getId() == airlineId);
					status = airlineIsValid;
				}
			}
		}

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {

		int userId = super.getRequest().getPrincipal().getAccountId();
		UserAccount user = this.repository.findUserAccountById(userId);
		FlightCrewMember member = new FlightCrewMember();

		member.setUserAccount(user);

		super.getBuffer().addData(member);

	}

	@Override
	public void bind(final FlightCrewMember object) {
		assert object != null;

		super.bindObject(object, "employeeCode", "phoneNumber", "languageSkills", "availabilityStatus", "airline", "salary", "yearsOfExperience");
	}

	@Override
	public void validate(final FlightCrewMember object) {
		assert object != null;

		// Employee Code validation
		if (object.getEmployeeCode() == null || object.getEmployeeCode().trim().isEmpty())
			super.state(false, "employeeCode", "acme.validation.flightcrewmember.employeeCode.blank.message");
		else {
			String employeeCode = object.getEmployeeCode().trim();

			if (!employeeCode.matches("^[A-Z]{2,3}\\d{6}$"))
				super.state(false, "employeeCode", "acme.validation.flightcrewmember.employeeCode.pattern.message");
			else if (employeeCode.length() < 8 || employeeCode.length() > 9)
				super.state(false, "employeeCode", "acme.validation.flightcrewmember.employeeCode.length.message");
			else {
				boolean duplicatedCode = this.repository.findAllMembers().stream().anyMatch(member -> member.getEmployeeCode().equals(employeeCode));
				super.state(!duplicatedCode, "employeeCode", "acme.validation.flightcrewmember.employeeCode.duplicate.message");
			}
		}

		// Phone Number validation
		if (object.getPhoneNumber() == null || object.getPhoneNumber().trim().isEmpty())
			super.state(false, "phoneNumber", "acme.validation.flightcrewmember.phoneNumber.null.message");
		else {
			String phoneNumber = object.getPhoneNumber().trim();
			if (!phoneNumber.matches("^\\+?\\d{6,15}$"))
				super.state(false, "phoneNumber", "acme.validation.flightcrewmember.phoneNumber.format.message");
		}

		// Language Skills validation
		if (object.getLanguageSkills() == null || object.getLanguageSkills().trim().isEmpty())
			super.state(false, "languageSkills", "acme.validation.flightcrewmember.languageSkills.null.message");
		else {
			String languageSkills = object.getLanguageSkills().trim();
			if (languageSkills.length() < 1 || languageSkills.length() > 255)
				super.state(false, "languageSkills", "acme.validation.flightcrewmember.languageSkills.length.message");
		}

		// Availability Status validation
		if (object.getAvailabilityStatus() == null)
			super.state(false, "availabilityStatus", "acme.validation.flightcrewmember.availabilityStatus.null.message");

		// Airline validation
		if (object.getAirline() == null)
			super.state(false, "airline", "acme.validation.flightcrewmember.airline.null.message");

		// Salary validation - handle the case when input is just a number without currency
		if (object.getSalary() == null)
			super.state(false, "salary", "acme.validation.flightcrewmember.salary.null.message");
		else {
			String currency = object.getSalary().getCurrency();
			if (currency == null || currency.trim().isEmpty())
				super.state(false, "salary", "acme.validation.flightcrewmember.salary.format.message");
			else if (!currency.matches("^(EUR|USD|GBP)$"))
				super.state(false, "salary", "acme.validation.flightcrewmember.salary.currency.message");
			else {
				double amount = object.getSalary().getAmount();
				if (amount < 0.0 || amount > 1000000.0)
					super.state(false, "salary", "acme.validation.flightcrewmember.salary.amount.message");
			}
		}

		// Years of Experience validation
		if (object.getYearsOfExperience() != null) {
			int years = object.getYearsOfExperience();
			if (years < 0 || years > 120)
				super.state(false, "yearsOfExperience", "acme.validation.flightcrewmember.yearsOfExperience.range.message");
		}

	}

	@Override
	public void perform(final FlightCrewMember object) {
		assert object != null;
		this.repository.save(object);
	}

	@Override
	public void unbind(final FlightCrewMember object) {
		assert object != null;
		Dataset dataset;

		dataset = super.unbindObject(object, "employeeCode", "phoneNumber", "languageSkills", "availabilityStatus", "salary", "yearsOfExperience");

		SelectChoices airlineChoices;
		airlineChoices = SelectChoices.from(this.repository.findAllAirlines(), "iataCode", object.getAirline());
		dataset.put("airlineChoices", airlineChoices);

		SelectChoices availabilityStatusChoices;
		availabilityStatusChoices = SelectChoices.from(acme.realms.flightcrewmember.AvailabilityStatus.class, object.getAvailabilityStatus());
		dataset.put("availabilityStatusChoices", availabilityStatusChoices);

		super.getResponse().addData(dataset);
	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equals("POST"))
			PrincipalHelper.handleUpdate();
	}
}
