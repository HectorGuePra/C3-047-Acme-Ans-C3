
package acme.features.customer.booking;

import java.security.SecureRandom;
import java.util.Collection;
import java.util.Date;
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
public class CustomerBookingCreateService extends AbstractGuiService<Customer, Booking> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerBookingRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean authorised = true;

		if (super.getRequest().getMethod().equals("POST")) {
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
				Booking b = this.repository.findBookingByLocatorCode(locatorCode);
				authorised = locatorCode.equals("") || b == null;
			}

		}

		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		Booking booking;
		Customer customer;

		customer = (Customer) super.getRequest().getPrincipal().getActiveRealm();

		booking = new Booking();
		booking.setCustomer(customer);

		super.getBuffer().addData(booking);
	}

	@Override
	public void bind(final Booking booking) {
		int flightId;
		Flight flight;
		Date moment = MomentHelper.getCurrentMoment();

		flightId = super.getRequest().getData("flight", int.class);
		flight = this.repository.findFlightById(flightId);

		super.bindObject(booking, "travelClass", "lastCardNibble");

		booking.setFlight(flight);
		booking.setPurchaseMoment(moment);
		booking.setLocatorCode(this.randomLocatorCode());
		booking.setDraftMode(true);

	}

	@Override
	public void validate(final Booking booking) {
		/*
		 * Booking b = this.repository.findBookingByLocatorCode(booking.getLocatorCode());
		 * if (b != null)
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
		dataset.put("flight", flightChoices.getSelected().getKey());
		dataset.put("flights", flightChoices);
		dataset.put("classes", classChoices);
		dataset.put("travelClass", classChoices.getSelected().getKey());

		super.getResponse().addData(dataset);

	}

	private String randomLocatorCode() {
		String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		SecureRandom random = new SecureRandom();
		int length = 6 + random.nextInt(2);
		StringBuilder codigo = new StringBuilder(length);

		for (int i = 0; i < length; i++)
			codigo.append(characters.charAt(random.nextInt(characters.length())));

		return codigo.toString();
	}

}
