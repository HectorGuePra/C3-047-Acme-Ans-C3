
package acme.features.administrator.airline;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airline.Airline;
import acme.entities.airline.AirlineType;

@GuiService
public class AdministratorAirlineUpdateService extends AbstractGuiService<Administrator, Airline> {

	@Autowired
	private AdministratorAirlineRepository repository;


	@Override
	public void authorise() {
		boolean authorised = true;

		if (super.getRequest().hasData("id")) {
			if (super.getRequest().getMethod().equals("POST"))
				if (authorised && super.getRequest().hasData("type")) {
					String type = super.getRequest().getData("type", String.class);
					authorised = type.equals("0") || type.equals("") || type.equals("LUXURY") || type.equals("STANDARD") || type.equals("LOW_COST");
				}
		} else
			authorised = false;

		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		Airline airline;
		int id;

		id = super.getRequest().getData("id", int.class);
		airline = this.repository.findAirlineById(id);

		super.getBuffer().addData(airline);
	}

	@Override
	public void bind(final Airline airline) {
		super.bindObject(airline, "name", "iataCode", "websiteUrl", "type", "phoneNumber", "email", "foundationMoment");
	}

	@Override
	public void validate(final Airline airline) {
		boolean confirmation;
		boolean uniqueIataCode;
		Airline existingAirline;

		existingAirline = this.repository.findAirlineByIataCode(airline.getIataCode());
		uniqueIataCode = existingAirline == null || existingAirline.equals(airline);
		super.state(uniqueIataCode, "iataCode", "acme.validation.airline.duplicated-iataCode.message");

		confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
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

		dataset = super.unbindObject(airline, "name", "iataCode", "websiteUrl", "type", "phoneNumber", "email", "foundationMoment");
		dataset.put("types", typeChoices);

		super.getResponse().addData(dataset);

	}

}
