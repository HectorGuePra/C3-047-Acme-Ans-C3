
package acme.features.manager.flights;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airport.Airport;
import acme.entities.flight.Flight;
import acme.realms.manager.Manager;

@GuiService
public class ManagerFlightShowService extends AbstractGuiService<Manager, Flight> {

	@Autowired
	private ManagerFlightRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int flightId = super.getRequest().getData("id", int.class);
		Flight flight = this.repository.findFlightById(flightId);
		status = flight != null && super.getRequest().getPrincipal().getActiveRealm().getId() == flight.getManager().getId();
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
	public void unbind(final Flight flight) {
		Dataset dataset;

		dataset = super.unbindObject(flight, "tag", "requiresSelfTransfer", "cost", "description", "draftMode");
		dataset.put("departure", Optional.ofNullable(flight.getDeparture()).map(Airport::getName).orElse(null));
		dataset.put("arrival", Optional.ofNullable(flight.getArrival()).map(Airport::getName).orElse(null));
		dataset.put("scheduledDeparture", flight.getFlightDeparture());
		dataset.put("scheduledArrival", flight.getFlightArrival());
		dataset.put("layovers", flight.getLayovers());

		super.getResponse().addData(dataset);
	}

}
