
package acme.features.customer.booking;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.booking.Booking;
import acme.entities.booking.TravelClass;
import acme.entities.flight.Flight;
import acme.realms.Customer;

@GuiService
public class CustomerBookingUpdateService extends AbstractGuiService<Customer, Booking> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerBookingRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int bookingId;
		Booking booking;
		Customer customer;

		bookingId = super.getRequest().getData("id", int.class);
		booking = this.repository.findBookingById(bookingId);
		customer = booking == null ? null : booking.getCustomer();
		status = booking != null && booking.isDraftMode() && super.getRequest().getPrincipal().hasRealm(customer);

		super.getResponse().setAuthorised(status);

	}

	@Override
	public void load() {
		Booking booking;
		int bookingId;

		bookingId = super.getRequest().getData("id", int.class);
		booking = this.repository.findBookingById(bookingId);

		super.getBuffer().addData(booking);
	}

	@Override
	public void bind(final Booking booking) {
		int flightId;
		Flight flight;

		flightId = super.getRequest().getData("flight", int.class);
		flight = this.repository.findFlightById(flightId);

		super.bindObject(booking, "travelClass", "lastCardNibble");
		booking.setFlight(flight);
		booking.setDraftMode(true);

	}

	@Override
	public void validate(final Booking booking) {
		if (booking.isDraftMode() == false)
			super.state(false, "draftMode", "acme.validation.confirmation.message.update");

		Booking b = this.repository.findBookingByLocatorCode(booking.getLocatorCode());
		if (b != null && b.getId() != booking.getId())
			super.state(false, "locatorCode", "acme.validation.confirmation.message.booking.locatorCode");

		Collection<Flight> validFlights = this.repository.findAllPublishedFlights().stream().filter(f -> this.repository.legsByFlightId(f.getId()).stream().allMatch(leg -> leg.getScheduledDeparture().after(MomentHelper.getCurrentMoment())))
			.collect(Collectors.toList());

		if (booking.getFlight() != null && !validFlights.contains(booking.getFlight()))
			super.state(false, "flight", "acme.validation.confirmation.message.booking.flight");
	}

	@Override
	public void perform(final Booking booking) {
		this.repository.save(booking);
	}

	@Override
	public void unbind(final Booking booking) {
		Collection<Flight> flights;
		SelectChoices flightChoices;
		SelectChoices classChoices;
		Dataset dataset;

		flights = this.repository.findAllPublishedFlights();
		flightChoices = SelectChoices.from(flights, "tag", booking.getFlight());
		classChoices = SelectChoices.from(TravelClass.class, booking.getTravelClass());

		dataset = super.unbindObject(booking, "locatorCode", "purchaseMoment", "travelClass", "price", "lastCardNibble", "draftMode");
		dataset.put("bookingId", booking.getId());
		dataset.put("flight", flightChoices.getSelected().getKey());
		dataset.put("flights", flightChoices);
		dataset.put("classes", classChoices);

		super.getResponse().addData(dataset);

	}
}
