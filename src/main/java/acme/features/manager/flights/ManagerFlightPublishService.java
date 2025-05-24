
package acme.features.manager.flights;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airline.Airline;
import acme.entities.flight.Flight;
import acme.entities.legs.Leg;
import acme.realms.manager.Manager;

@GuiService
public class ManagerFlightPublishService extends AbstractGuiService<Manager, Flight> {

	@Autowired
	private ManagerFlightRepository repository;


	@Override
	public void authorise() {
		Integer flightId = super.getRequest().getData("id", Integer.class);
		if (flightId == null) {
			super.getResponse().setAuthorised(false);
			return;
		}

		Flight flight = this.repository.findFlightById(flightId);
		if (flight == null || !flight.getDraftMode()) {
			super.getResponse().setAuthorised(false);
			return;
		}

		Manager manager = this.repository.findManagerByFlightManagerId(flightId);
		boolean status = super.getRequest().getPrincipal().hasRealm(manager);

		if (status && super.getRequest().hasData("airline")) {
			int airlineId = super.getRequest().getData("airline", int.class);
			Airline airline = this.repository.findAirlineByManager(manager.getId());
			if (airline != null)
				status = manager.getAirline().getId() == airlineId && status;
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
		flight.setDraftMode(false);

	}

	@Override
	public void validate(final Flight flight) {
		boolean canBePublish = false;
		List<Leg> legs = this.repository.findLegsByFlightId(flight.getId());
		if (!legs.isEmpty())
			canBePublish = legs.stream().allMatch(l -> !l.isDraftMode());
		super.state(canBePublish, "*", "acme.validation.flight.cant-be-publish.message");
	}

	@Override
	public void perform(final Flight flight) {
		flight.setDraftMode(false);
		this.repository.save(flight);
	}

	@Override
	public void unbind(final Flight flight) {
		Dataset dataset;

		dataset = super.unbindObject(flight, "tag", "requiresSelfTransfer", "cost", "description", "draftMode");
		dataset.put("origin", flight.getDeparture() != null ? flight.getDeparture().getName() : flight.getDeparture());
		dataset.put("destination", flight.getArrival() != null ? flight.getArrival().getName() : flight.getArrival());
		dataset.put("scheduledDeparture", flight.getFlightDeparture());
		dataset.put("scheduledArrival", flight.getFlightArrival());
		dataset.put("layovers", flight.getLayovers());

		super.getResponse().addData(dataset);
	}

}
