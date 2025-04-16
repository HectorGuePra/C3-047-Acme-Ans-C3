
package acme.features.manager.flights;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flight.Flight;
import acme.realms.manager.Manager;

@GuiService
public class ManagerFlightShowService extends AbstractGuiService<Manager, Flight> {

	@Autowired
	private ManagerFlightRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int flightId;
		Flight flight;
		Manager manager;

		flightId = super.getRequest().getData("id", int.class);
		flight = this.repository.findFlightById(flightId);
		manager = flight == null ? null : flight.getManager();
		status = super.getRequest().getPrincipal().hasRealm(manager) && flight != null;

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
		int managerId;

		managerId = super.getRequest().getPrincipal().getActiveRealm().getId();

		dataset = super.unbindObject(flight, "tag", "requiresSelfTransfer", "cost", "description", "draftMode");
		dataset.put("departure", flight.getDeparture() != null ? flight.getDeparture().getName() : flight.getDeparture());
		dataset.put("arrival", flight.getArrival() != null ? flight.getArrival().getName() : flight.getArrival());
		dataset.put("scheduledDeparture", flight.getFlightDeparture());
		dataset.put("scheduledArrival", flight.getFlightArrival());
		dataset.put("layovers", flight.getLayovers());

		super.getResponse().addData(dataset);
	}

}
