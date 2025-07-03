
package acme.features.authenticated.manager;

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

@GuiService
public class AuthenticatedManagerCreateService extends AbstractGuiService<Authenticated, Manager> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AuthenticatedManagerRepository repository;


	@Override
	public void authorise() {
		boolean status;

		// Check that the user doesn't already have the Manager role
		status = !super.getRequest().getPrincipal().hasRealmOfType(Manager.class) && 
				!super.getRequest().getPrincipal().hasRealmOfType(FlightCrewMember.class) && 
				!super.getRequest().getPrincipal().hasRealmOfType(Consumer.class) &&
				!super.getRequest().getPrincipal().hasRealmOfType(Customer.class) && 
				!super.getRequest().getPrincipal().hasRealmOfType(Provider.class) && 
				!super.getRequest().getPrincipal().hasRealmOfType(Technician.class);

		if (status && super.getRequest().getMethod().equals("POST")) {
			// Additional validation for POST requests
			if (super.getRequest().hasData("airline")) {
				Integer airlineId = super.getRequest().getData("airline", int.class);
				if (airlineId != 0) {
					Collection<Airline> availableAirlines = this.repository.findAirlines();
					Airline airline = this.repository.findAirlineById(airlineId);
					boolean airlineIsValid = availableAirlines.contains(airline);
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
		Manager manager = new Manager();

		manager.setUserAccount(user);

		super.getBuffer().addData(manager);

	}

	@Override
	public void bind(final Manager object) {
		assert object != null;

		super.bindObject(object, "identifierNumber", "experience", "birthdate", "linkPicture", "airline");
	}

	@Override
	public void validate(final Manager object) {
		assert object != null;

		// Only check for duplicate identifier numbers
		// Let the framework handle the rest through annotations (@Mandatory, @ValidIdentifierNumber, @ValidManager, etc.)
		if (object.getIdentifierNumber() != null && !object.getIdentifierNumber().trim().isEmpty()) {
			String identifierNumber = object.getIdentifierNumber().trim();
			boolean duplicatedNumber = this.repository.findManagers().stream()
					.anyMatch(manager -> manager.getIdentifierNumber().equals(identifierNumber));
			super.state(!duplicatedNumber, "identifierNumber", "acme.validation.manager.identifierNumber.duplicate.message");
		}
	}

	@Override
	public void perform(final Manager object) {
		assert object != null;
		this.repository.save(object);
	}

	@Override
	public void unbind(final Manager object) {
		assert object != null;
		Dataset dataset;
		dataset = super.unbindObject(object, "identifierNumber", "experience", "birthdate", "linkPicture");
		SelectChoices airlineChoices;
		airlineChoices = SelectChoices.from(this.repository.findAirlines(), "iataCode", object.getAirline());
		dataset.put("airlineChoices", airlineChoices);
		dataset.put("airline", airlineChoices.getSelected().getKey());
		super.getResponse().addData(dataset);
	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equals("POST"))
			PrincipalHelper.handleUpdate();
	}

}
