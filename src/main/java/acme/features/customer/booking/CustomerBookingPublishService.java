
package acme.features.customer.booking;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.Passenger;
import acme.entities.booking.Booking;
import acme.entities.booking.TravelClass;
import acme.entities.flight.Flight;
import acme.realms.Customer;

@GuiService
public class CustomerBookingPublishService extends AbstractGuiService<Customer, Booking> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerBookingRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean authorised = true;
		int bookingId;
		Booking booking;
		Customer customer;

		if (super.getRequest().hasData("id")) {
			bookingId = super.getRequest().getData("id", int.class);
			booking = this.repository.findBookingById(bookingId);
			customer = booking == null ? null : booking.getCustomer();
			Collection<Passenger> passengers = this.repository.findPassengersByBooking(bookingId);
			Collection<Passenger> pInDraftMode = this.repository.findPassengersInDraftMode(bookingId);
			authorised = booking != null && booking.isDraftMode() && !passengers.isEmpty() && pInDraftMode.isEmpty() && super.getRequest().getPrincipal().hasRealm(customer);

		} else
			authorised = false;

		if (authorised && super.getRequest().getMethod().equals("POST")) {
			if (super.getRequest().hasData("flight", int.class)) {
				int flightId = super.getRequest().getData("flight", int.class);
				Flight flight = this.repository.findFlightById(flightId);

				Collection<Flight> validFlights = this.repository.findAllPublishedFlights().stream().filter(f -> f.getFlightDeparture() != null && f.getFlightArrival() != null && f.getDeparture() != null && f.getArrival() != null)
					.filter(f -> this.repository.legsByFlightId(f.getId()).stream().allMatch(leg -> leg.getScheduledDeparture().after(MomentHelper.getCurrentMoment()))).collect(Collectors.toList());

				authorised = flightId == 0 || flight != null && validFlights.contains(flight);
			}
			if (authorised && super.getRequest().hasData("travelClass")) {
				String travelClass = super.getRequest().getData("travelClass", String.class);
				authorised = travelClass.equals("0") || travelClass.equals("") || travelClass.equals("ECONOMY") || travelClass.equals("BUSINESS");
			}
		}
		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		Booking booking;
		int id;

		id = super.getRequest().getData("id", int.class);
		booking = this.repository.findBookingById(id);

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

	}

	@Override
	public void validate(final Booking booking) {
		if (booking.getLastCardNibble() == null || booking.getLastCardNibble().isBlank())
			super.state(false, "lastCardNibble", "acme.validation.confirmation.message.lastCardNibble");
	}

	@Override
	public void perform(final Booking booking) {
		booking.setDraftMode(false);
		this.repository.save(booking);
	}

	@Override
	public void unbind(final Booking booking) {
		Dataset dataset;
		SelectChoices classChoices;
		SelectChoices flightChoices;

		Collection<Flight> flights;

		flights = this.repository.findAllPublishedFlights();
		flightChoices = SelectChoices.from(flights, "info", booking.getFlight());
		classChoices = SelectChoices.from(TravelClass.class, booking.getTravelClass());

		dataset = super.unbindObject(booking, "locatorCode", "purchaseMoment", "travelClass", "price", "lastCardNibble", "draftMode");

		dataset.put("bookingId", booking.getId());
		dataset.put("classes", classChoices);
		dataset.put("flight", flightChoices.getSelected().getKey());
		dataset.put("flights", flightChoices);

		super.getResponse().addData(dataset);
	}

}
