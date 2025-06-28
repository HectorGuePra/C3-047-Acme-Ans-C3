
package acme.features.any.leg;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Any;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flight.Flight;
import acme.entities.legs.Leg;

@GuiService
public class AnyLegListService extends AbstractGuiService<Any, Leg> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AnyLegRepository repository;

	// AbstractService interface ----------------------------------------------


	@Override
	public void authorise() {
		boolean status;

		int flightId = super.getRequest().getData("masterId", int.class);
		Flight flight = this.repository.findFlightById(flightId);
		status = flight != null;

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Collection<Leg> objects;
		int flightId = super.getRequest().getData("masterId", int.class);
		Flight flight = this.repository.findFlightById(flightId);
		super.getResponse().addGlobal("masterId", flightId);
		objects = this.repository.findLegsByFlightId(flightId);
		super.getResponse().addGlobal("masterDraftMode", flight.isDraftMode());
		super.getBuffer().addData(objects);
	}

	@Override
	public void unbind(final Leg object) {
		assert object != null;

		Dataset dataset;

		dataset = super.unbindObject(object, "flightNumber", "scheduledDeparture", "scheduledArrival", "status");
		dataset.put("departureAirport", object.getDepartureAirport().getIataCode());
		dataset.put("arrivalAirport", object.getArrivalAirport().getIataCode());

		super.getResponse().addData(dataset);
	}

}
