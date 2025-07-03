
package acme.features.authenticated.manager;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Authenticated;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.realms.manager.Manager;

@GuiService
public class AuthenticatedManagerUpdateService extends AbstractGuiService<Authenticated, Manager> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AuthenticatedManagerRepository repository;


	@Override
	public void authorise() {
		boolean status;

		status = super.getRequest().getPrincipal().hasRealmOfType(Manager.class);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {

		int managerId = super.getRequest().getPrincipal().getAccountId();
		Manager manager = this.repository.findManagerByUserAccountId(managerId);

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

		boolean duplicatedNumber = this.repository.findManagers().stream().anyMatch(manager -> manager.getIdentifierNumber().equals(object.getIdentifierNumber()) && manager.getId() != object.getId());
		super.state(!duplicatedNumber, "identifierNumber", "authenticated.airline-manager.form.error.duplicatedIdentifierNumber");
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
