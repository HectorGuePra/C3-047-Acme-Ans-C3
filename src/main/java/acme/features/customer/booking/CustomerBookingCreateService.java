
package acme.features.customer.booking;

import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.helpers.RandomHelper;
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
			/*
			 * if (authorised && super.getRequest().hasData("locatorCode", String.class)) {
			 * String locatorCode = super.getRequest().getData("locatorCode", String.class);
			 * Booking b = this.repository.findBookingByLocatorCode(locatorCode);
			 * authorised = locatorCode.equals("") || b == null;
			 * }
			 */

		}

		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		Booking booking;
		Customer customer;

		Date moment = MomentHelper.getCurrentMoment();

		customer = (Customer) super.getRequest().getPrincipal().getActiveRealm();

		booking = new Booking();
		booking.setCustomer(customer);
		booking.setPurchaseMoment(moment);
		booking.setLocatorCode(this.generateUniqueLocatorCode());
		booking.setDraftMode(true);

		super.getBuffer().addData(booking);
	}

	@Override
	public void bind(final Booking booking) {
		int flightId;
		Flight flight;

		flightId = super.getRequest().getData("flight", int.class);
		flight = this.repository.findFlightById(flightId);
		booking.setFlight(flight);

		super.bindObject(booking, "travelClass", "lastCardNibble");
	}

	@Override
	public void validate(final Booking booking) {
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

		flightChoices = SelectChoices.from(validFlights, "info", booking.getFlight());
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
		int length = RandomHelper.nextInt(6, 8);
		StringBuilder code = new StringBuilder(length);

		for (int i = 0; i < length; i++) {
			int index = RandomHelper.nextInt(characters.length());
			code.append(characters.charAt(index));
		}

		return code.toString();
	}

	private String generateUniqueLocatorCode() {
		String code;

		do
			code = this.randomLocatorCode();
		while (this.repository.findBookingByLocatorCode(code) != null);

		return code;
	}

}
