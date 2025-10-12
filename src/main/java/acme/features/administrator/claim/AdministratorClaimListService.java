
package acme.features.administrator.claim;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset; // Importante añadir este
import acme.client.components.principals.Administrator;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claims.Claim;

@GuiService
public class AdministratorClaimListService extends AbstractGuiService<Administrator, Claim> {

	@Autowired
	private AdministratorClaimRepository repository;


	@Override
	public void authorise() {
		boolean status = super.getRequest().getPrincipal().hasRealmOfType(Administrator.class);
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Collection<Claim> objects;
		objects = this.repository.findAllPublishedClaims();
		super.getBuffer().addData(objects);
	}

	@Override
	public void unbind(final Claim object) {
		assert object != null;

		Dataset dataset;

		dataset = super.unbindObject(object, "registrationMoment", "passengerEmail", "type", "accepted");

		super.getResponse().addData(dataset);
	}
}
