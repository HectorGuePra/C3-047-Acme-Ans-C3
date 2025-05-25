
package acme.features.manager.flights;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flight.Flight;
import acme.realms.manager.Manager;

@GuiService
public class ManagerFlightUpdateService extends AbstractGuiService<Manager, Flight> {

	@Autowired
	private ManagerFlightRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int flightId;
		Flight flight;
		Manager manager;
		if (!super.getRequest().hasData("id"))
			status = false;
		else {

			flightId = super.getRequest().getData("id", int.class);
			flight = this.repository.findFlightById(flightId);
			manager = flight == null ? null : flight.getManager();
			status = flight != null && super.getRequest().getPrincipal().hasRealm(manager) && flight.getDraftMode();
		}
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Flight flight;
		int id;

		id = super.getRequest().getData("id", int.class);
		flight = this.repository.findFlightById(id);

		super.getBuffer().addData(flight);
	}

	@Override
	public void bind(final Flight flight) {
		super.bindObject(flight, "tag", "requiresSelfTransfer", "cost", "description");

	}

	@Override
	public void validate(final Flight flight) {
		boolean availableCurrency;
		List<String> currencies;
		currencies = this.repository.findAllCurrencies();
		String currency;
		currency = super.getRequest().getData("cost", String.class);
		currency = currency.length() >= 3 ? currency.substring(0, 3).toUpperCase() : currency;
		availableCurrency = currencies.contains(currency);

		super.state(availableCurrency, "cost", "acme.validation.invalid-currency.message");
	}

	@Override
	public void perform(final Flight flight) {
		this.repository.save(flight);
	}

	@Override
	public void unbind(final Flight flight) {
		Dataset dataset;

		dataset = super.unbindObject(flight, "tag", "requiresSelfTransfer", "cost", "description", "draftMode");
		dataset.put("origin", flight.getDeparture());
		dataset.put("destination", flight.getArrival());
		dataset.put("scheduledDeparture", flight.getFlightDeparture());
		dataset.put("scheduledArrival", flight.getFlightArrival());
		dataset.put("layovers", flight.getLayovers());

		super.getResponse().addData(dataset);
	}

}
