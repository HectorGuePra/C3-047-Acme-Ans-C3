
package acme.features.customer.booking;

import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.datatypes.Money;
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
		boolean authorised = true;
		int bookingId;
		Booking booking;
		Customer customer;

		bookingId = super.getRequest().getData("id", int.class);
		booking = this.repository.findBookingById(bookingId);
		customer = booking == null ? null : booking.getCustomer();
		authorised = booking != null && booking.isDraftMode() && super.getRequest().getPrincipal().hasRealm(customer);

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
			if (authorised && super.getRequest().hasData("locatorCode", String.class)) {
				String locatorCode = super.getRequest().getData("locatorCode", String.class);
				authorised = locatorCode != null && locatorCode.equals(booking.getLocatorCode());
			}
			if (authorised && super.getRequest().hasData("purchaseMoment", Date.class)) {
				Date purchaseMoment = super.getRequest().getData("purchaseMoment", Date.class);
				authorised = purchaseMoment != null && purchaseMoment.equals(booking.getPurchaseMoment());
			}
			if (authorised && super.getRequest().hasData("price", Money.class)) {
				Money price = super.getRequest().getData("price", Money.class);
				Money priceB = booking.getPrice();

				int flightId = super.getRequest().getData("flight", int.class);
				Money priceF = new Money();
				if (flightId == 0) {
					priceF.setAmount(0.0);
					priceF.setCurrency("EUR");
				} else
					priceF = this.repository.findCostByFlight(flightId);
				Money res = priceF;
				Integer nPassengers = this.repository.findPassengersByBooking(bookingId).size();
				double amount = priceF.getAmount() * nPassengers;
				res.setAmount(amount);

				authorised = price != null && (price.getAmount().equals(priceB.getAmount()) && price.getCurrency().equals(priceB.getCurrency()) || priceB.getAmount().equals(res.getAmount()) && priceB.getCurrency().equals(res.getCurrency()));
			}

		}

		super.getResponse().setAuthorised(authorised);

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
		/*
		 * if (booking.isDraftMode() == false)
		 * super.state(false, "draftMode", "acme.validation.confirmation.message.update");
		 * 
		 * Booking b = this.repository.findBookingByLocatorCode(booking.getLocatorCode());
		 * if (b != null && b.getId() != booking.getId())
		 * super.state(false, "locatorCode", "acme.validation.confirmation.message.booking.locatorCode");
		 * 
		 * Collection<Flight> validFlights = this.repository.findAllPublishedFlights().stream().filter(f -> f.getFlightDeparture() != null && f.getFlightArrival() != null && f.getDeparture() != null && f.getArrival() != null)
		 * .filter(f -> this.repository.legsByFlightId(f.getId()).stream().allMatch(leg -> leg.getScheduledDeparture().after(MomentHelper.getCurrentMoment()))).collect(Collectors.toList());
		 * 
		 * if (booking.getFlight() != null && !validFlights.contains(booking.getFlight()))
		 * super.state(false, "flight", "acme.validation.confirmation.message.booking.flight");
		 */
	}

	@Override
	public void perform(final Booking booking) {
		this.repository.save(booking);
	}

	@Override
	public void unbind(final Booking booking) {
		Collection<Flight> validFlights;
		SelectChoices flightChoices;
		SelectChoices classChoices;
		Dataset dataset;

		validFlights = this.repository.findAllPublishedFlights().stream().filter(f -> f.getFlightDeparture() != null && f.getFlightArrival() != null && f.getDeparture() != null && f.getArrival() != null)
			.filter(f -> this.repository.legsByFlightId(f.getId()).stream().allMatch(leg -> leg.getScheduledDeparture().after(MomentHelper.getCurrentMoment()))).collect(Collectors.toList());

		flightChoices = SelectChoices.from(validFlights, "description", booking.getFlight());
		classChoices = SelectChoices.from(TravelClass.class, booking.getTravelClass());

		dataset = super.unbindObject(booking, "locatorCode", "purchaseMoment", "travelClass", "price", "lastCardNibble", "draftMode");
		dataset.put("bookingId", booking.getId());
		dataset.put("flight", flightChoices.getSelected().getKey());
		dataset.put("flights", flightChoices);
		dataset.put("classes", classChoices);

		super.getResponse().addData(dataset);

	}
}
