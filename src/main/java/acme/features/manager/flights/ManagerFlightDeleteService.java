
package acme.features.manager.flights;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flight.Flight;
import acme.entities.legs.Leg;
import acme.features.manager.legs.ManagerLegDeleteService;
import acme.realms.manager.Manager;

@GuiService
public class ManagerFlightDeleteService extends AbstractGuiService<Manager, Flight> {

	@Autowired
	private ManagerFlightRepository	repository;

	@Autowired
	private ManagerLegDeleteService	legsDeleteService;


	@Override
	public void authorise() {
		boolean status = false;

		if (super.getRequest().hasData("id")) {
			int flightId = super.getRequest().getData("id", int.class);
			Flight flight = this.repository.findFlightById(flightId);

			if (flight != null) {
				Manager manager = flight.getManager();
				status = flight.isDraftMode() && super.getRequest().getPrincipal().hasRealm(manager);
			}
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
		boolean isPublished;
		isPublished = true;
		if (this.repository.findLegsByFlightId(flight.getId()).size() > 0) {
			List<Leg> legs = this.repository.findLegsByFlightId(flight.getId());
			for (Leg leg : legs)
				if (!leg.isDraftMode())
					isPublished = leg.isDraftMode();
		}
		super.state(isPublished, "tag", "acme.validation.flight.unable-to-delete-flight-published-leg.message");
	}

	@Override
	public void perform(final Flight flight) {
		List<Leg> legs;

		legs = this.repository.findLegsByFlightId(flight.getId());
		legs.stream().forEach(l -> this.legsDeleteService.perform(l));
		this.repository.delete(flight);
	}

	@Override
	public void unbind(final Flight flight) {
		Dataset dataset;

		dataset = super.unbindObject(flight, "tag", "requiresSelfTransfer", "cost", "description", "draftMode");

		dataset.put("departure", flight.getDeparture());
		dataset.put("arrival", flight.getArrival());
		dataset.put("scheduledDeparture", flight.getFlightDeparture());
		dataset.put("scheduledArrival", flight.getFlightArrival());
		dataset.put("layovers", flight.getLayovers());

		super.getResponse().addData(dataset);
	}

}
