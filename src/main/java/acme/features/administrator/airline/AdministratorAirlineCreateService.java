
package acme.features.administrator.airline;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airline.Airline;
import acme.entities.airline.AirlineType;

@GuiService
public class AdministratorAirlineCreateService extends AbstractGuiService<Administrator, Airline> {

	@Autowired
	private AdministratorAirlineRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Airline airline;

		airline = new Airline();
		airline.setName("");
		airline.setIataCode("");
		airline.setWebsiteUrl("");
		airline.setType(null);
		airline.setFoundationMoment(MomentHelper.getCurrentMoment());
		airline.setEmail("");
		airline.setPhoneNumber("");

		super.getBuffer().addData(airline);
	}

	@Override
	public void bind(final Airline airline) {
		super.unbindObject(airline, "name", "iataCode", "websiteUrl", "type", "phoneNumber", "email");
	}

	@Override
	public void validate(final Airline airline) {
		;
	}

	@Override
	public void perform(final Airline airline) {
		this.repository.save(airline);
	}

	@Override
	public void unbind(final Airline airline) {
		Dataset dataset;
		SelectChoices typeChoices;

		typeChoices = SelectChoices.from(AirlineType.class, airline.getType());

		dataset = super.unbindObject(airline, "name", "iataCode", "websiteUrl", "type", "phoneNumber", "email");
		dataset.put("types", typeChoices);
		dataset.put("type", typeChoices.getSelected().getKey());

		super.getResponse().addData(dataset);

	}

}
