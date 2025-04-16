
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
public class CustomerBookingRecordUpdateService extends AbstractGuiService<Customer, BookingRecord> {

	@Autowired
	private CustomerBookingRecordRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int BookingRecordId;
		BookingRecord BookingRecord;
		Customer customer;

		BookingRecordId = super.getRequest().getData("id", int.class);
		BookingRecord = this.repository.findBookingRecordById(BookingRecordId);
		customer = BookingRecord == null ? null : BookingRecord.getBooking().getCustomer();
		status = BookingRecord != null && super.getRequest().getPrincipal().hasRealm(customer);

		super.getResponse().setAuthorised(status);

	}

	@Override
	public void load() {
		BookingRecord BookingRecord;
		int BookingRecordId;

		BookingRecordId = super.getRequest().getData("id", int.class);
		BookingRecord = this.repository.findBookingRecordById(BookingRecordId);

		super.getBuffer().addData(BookingRecord);
	}

	@Override
	public void bind(final BookingRecord BookingRecord) {
		int bId;
		int pId;
		Booking booking;
		Passenger passenger;

		bId = super.getRequest().getData("booking", int.class);
		pId = super.getRequest().getData("passenger", int.class);
		booking = this.repository.findBookingById(bId);
		passenger = this.repository.findPassengerById(pId);

		super.bindObject(BookingRecord, "booking", "passenger");

		BookingRecord.setBooking(booking);
		BookingRecord.setPassenger(passenger);

	}

	@Override
	public void validate(final BookingRecord BookingRecord) {
		//		boolean confirmation;
		//
		//		confirmation = super.getRequest().getData("confirmation", boolean.class);
		//		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
	}

	@Override
	public void perform(final BookingRecord BookingRecord) {
		this.repository.save(BookingRecord);
	}

	@Override
	public void unbind(final BookingRecord BookingRecord) {
		Collection<Booking> bookings;
		Collection<Passenger> passengers;
		SelectChoices books;
		SelectChoices passs;
		Dataset dataset;
		int customerId = super.getRequest().getPrincipal().getActiveRealm().getId();

		bookings = this.repository.findBookingsByCustomerId(customerId);
		passengers = this.repository.findPassengersByCustomerId(customerId);
		books = SelectChoices.from(bookings, "locatorCode", BookingRecord.getBooking());
		passs = SelectChoices.from(passengers, "passport", BookingRecord.getPassenger());
		dataset = super.unbindObject(BookingRecord, "booking", "passenger", "draftMode");
		dataset.put("booking", books.getSelected().getKey());
		dataset.put("bookings", books);
		dataset.put("passenger", passs.getSelected().getKey());
		dataset.put("passengers", passs);

		super.getResponse().addData(dataset);

	}

}
