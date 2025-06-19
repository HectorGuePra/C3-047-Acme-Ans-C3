
package acme.features.customer.bookingRecord;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.Passenger;
import acme.entities.booking.Booking;
import acme.entities.booking.BookingRecord;
import acme.realms.Customer;

@GuiService
public class CustomerBookingRecordCreateService extends AbstractGuiService<Customer, BookingRecord> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerBookingRecordRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean authorised = true;
		int customerId = this.getRequest().getPrincipal().getActiveRealm().getId();

		if (super.getRequest().hasData("bookingId")) {
			int bookingId = super.getRequest().getData("bookingId", int.class);
			Booking booking = this.repository.findBookingById(bookingId);
			if (booking == null || booking.getCustomer().getId() != customerId)
				authorised = false;
			else if (super.getRequest().getMethod().equals("POST")) {
				int pId = super.getRequest().getData("passenger", int.class);
				Passenger passenger = this.repository.findPassengerById(pId);
				Collection<Passenger> myValidPassengers = this.repository.findPassengersByCustomerIdNotInBooking(customerId, bookingId);
				if (passenger == null && pId != 0 || passenger != null && !myValidPassengers.contains(passenger))
					authorised = false;
			}
		} else
			authorised = false;

		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		BookingRecord BookingRecord;
		Booking booking;
		int bId;

		bId = super.getRequest().getData("bookingId", int.class);
		booking = this.repository.findBookingById(bId);

		BookingRecord = new BookingRecord();
		BookingRecord.setBooking(booking);

		super.getBuffer().addData(BookingRecord);
	}

	@Override
	public void bind(final BookingRecord BookingRecord) {
		int pId;

		Passenger passenger;

		pId = super.getRequest().getData("passenger", int.class);

		passenger = this.repository.findPassengerById(pId);

		BookingRecord.setPassenger(passenger);
	}

	@Override
	public void validate(final BookingRecord BookingRecord) {

	}

	@Override
	public void perform(final BookingRecord BookingRecord) {
		this.repository.save(BookingRecord);
	}

	@Override
	public void unbind(final BookingRecord BookingRecord) {
		Collection<Passenger> validPassengers;
		SelectChoices pass;
		Dataset dataset;
		int bookingId = super.getRequest().getData("bookingId", int.class);
		int customerId = super.getRequest().getPrincipal().getActiveRealm().getId();

		validPassengers = this.repository.findPassengersByCustomerIdNotInBooking(customerId, bookingId);
		pass = SelectChoices.from(validPassengers, "passport", BookingRecord.getPassenger());
		dataset = super.unbindObject(BookingRecord);
		dataset.put("passenger", pass.getSelected().getKey());
		dataset.put("passengers", pass);
		dataset.put("booking", BookingRecord.getBooking());
		dataset.put("bookingId", BookingRecord.getBooking().getId());
		dataset.put("bDraftMode", BookingRecord.getBooking().isDraftMode());

		super.getResponse().addData(dataset);

	}

}
