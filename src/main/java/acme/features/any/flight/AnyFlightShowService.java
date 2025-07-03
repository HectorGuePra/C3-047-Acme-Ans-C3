
package acme.features.any.flight;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Any;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airport.Airport;
import acme.entities.flight.Flight;

@GuiService
public class AnyFlightShowService extends AbstractGuiService<Any, Flight> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AnyFlightRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
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
	public void unbind(final Flight object) {
		assert object != null;

		Dataset dataset;

		dataset = super.unbindObject(object, "tag", "requiresSelfTransfer", "cost", "description", "draftMode");
		dataset.put("departure", Optional.ofNullable(object.getDeparture()).map(Airport::getName).orElse(null));
		dataset.put("arrival", Optional.ofNullable(object.getArrival()).map(Airport::getName).orElse(null));
		dataset.put("scheduledDeparture", object.getFlightDeparture());
		dataset.put("scheduledArrival", object.getFlightArrival());
		dataset.put("layovers", object.getLayovers());

		super.getResponse().addData(dataset);
	}

}
