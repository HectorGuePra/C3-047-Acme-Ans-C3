
package acme.features.manager.flights;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.booking.Booking;
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
		boolean status;
		int flightId;
		Flight flight;
		Manager manager;

		flightId = super.getRequest().getData("id", int.class);
		flight = this.repository.findFlightById(flightId);
		manager = flight == null ? null : flight.getManager();
		//status = flight != null && flight.isDraftMode() && super.getRequest().getPrincipal().hasRealm(manager);
		status = flight != null && super.getRequest().getPrincipal().hasRealm(manager);
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
		;
	}

	@Override
	public void perform(final Flight flight) {
		List<Booking> bookings;
		List<Leg> legs;

		bookings = this.repository.findBookingsByFlightId(flight.getId());
		bookings.stream().forEach(b -> this.repository.deleteAll(this.repository.findBookingRecordByBookingId(b.getId())));
		this.repository.deleteAll(bookings);

		legs = this.repository.findLegsByFlightId(flight.getId());
		legs.stream().forEach(l -> this.legsDeleteService.perform(l));
		this.repository.delete(flight);
	}

	@Override
	public void unbind(final Flight flight) {
		Dataset dataset;

		dataset = super.unbindObject(flight, "tag", "requiresSelfTransfer", "cost", "description", "drafMode");

		dataset.put("departure", flight.getDeparture() != null ? flight.getDeparture().getName() : flight.getDeparture());
		dataset.put("arrival", flight.getArrival() != null ? flight.getArrival().getName() : flight.getArrival());
		dataset.put("scheduledDeparture", flight.getFlightDeparture());
		dataset.put("scheduledArrival", flight.getFlightArrival());
		dataset.put("layovers", flight.getLayovers());

		super.getResponse().addData(dataset);
	}

}
