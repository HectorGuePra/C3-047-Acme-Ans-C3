
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
		super.getResponse().setAuthorised(true);
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
		boolean notPublished = true;
		Booking booking = BookingRecord.getBooking();
		if (booking != null && !booking.isDraftMode())
			notPublished = false;
		super.state(notPublished, "booking", "acme.validation.booking.invalid-booking-publish.message");
	}

	@Override
	public void perform(final BookingRecord BookingRecord) {
		this.repository.save(BookingRecord);
	}

	@Override
	public void unbind(final BookingRecord BookingRecord) {
		Collection<Passenger> passengers;
		SelectChoices passs;
		Dataset dataset;
		int customerId = super.getRequest().getPrincipal().getActiveRealm().getId();

		passengers = this.repository.findPassengersByCustomerId(customerId);
		passs = SelectChoices.from(passengers, "passport", BookingRecord.getPassenger());
		dataset = super.unbindObject(BookingRecord);
		dataset.put("passenger", passs.getSelected().getKey());
		dataset.put("passengers", passs);
		dataset.put("booking", BookingRecord.getBooking());
		dataset.put("bookingId", BookingRecord.getBooking().getId());

		super.getResponse().addData(dataset);

	}

}
