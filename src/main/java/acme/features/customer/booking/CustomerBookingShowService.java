
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
public class CustomerBookingShowService extends AbstractGuiService<Customer, Booking> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerBookingRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int bookingId;
		Booking booking;
		Customer customer;

		bookingId = super.getRequest().getData("id", int.class);
		booking = this.repository.findBookingById(bookingId);
		customer = booking == null ? null : booking.getCustomer();
		status = super.getRequest().getPrincipal().hasRealm(customer) && booking != null;

		super.getResponse().setAuthorised(status);
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
	public void unbind(final Booking booking) {
		Dataset dataset;
		SelectChoices classChoices;
		SelectChoices flightChoices;

		Collection<Flight> validFlights;
		Collection<Passenger> passengersInDraftMode;
		Collection<Passenger> passengers;

		boolean status = true;
		boolean anyPassengerInDraftMode = true;
		boolean atLeastOnePassenger = true;

		validFlights = this.repository.findAllPublishedFlights().stream().filter(f -> f.getFlightDeparture() != null && f.getFlightArrival() != null && f.getDeparture() != null && f.getArrival() != null)
			.filter(f -> this.repository.legsByFlightId(f.getId()).stream().allMatch(leg -> leg.getScheduledDeparture().after(MomentHelper.getCurrentMoment()))).collect(Collectors.toList());

		flightChoices = SelectChoices.from(validFlights, "description", booking.getFlight());
		classChoices = SelectChoices.from(TravelClass.class, booking.getTravelClass());
		passengersInDraftMode = this.repository.findPassengersInDraftMode(booking.getId());
		passengers = this.repository.findPassengersByBooking(booking.getId());

		dataset = super.unbindObject(booking, "locatorCode", "purchaseMoment", "travelClass", "price", "lastCardNibble", "draftMode");
		dataset.put("bookingId", booking.getId());
		dataset.put("classes", classChoices);
		dataset.put("flight", flightChoices.getSelected().getKey());
		dataset.put("flights", flightChoices);

		if (!booking.getLastCardNibble().isEmpty())
			status = false;
		dataset.put("lastCardNibbleIsEmpty", status);
		if (passengersInDraftMode.isEmpty())
			anyPassengerInDraftMode = false;
		dataset.put("anyPassengerInDraftMode", anyPassengerInDraftMode);
		if (passengers.isEmpty())
			atLeastOnePassenger = false;
		dataset.put("atLeastOnePassenger", atLeastOnePassenger);

		super.getResponse().addData(dataset);
	}
}
