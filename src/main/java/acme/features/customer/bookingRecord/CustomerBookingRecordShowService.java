
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
public class CustomerBookingRecordShowService extends AbstractGuiService<Customer, BookingRecord> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerBookingRecordRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int BookingRecordId;
		BookingRecord BookingRecord;
		Customer customer;

		BookingRecordId = super.getRequest().getData("id", int.class);
		BookingRecord = this.repository.findBookingRecordById(BookingRecordId);
		customer = BookingRecord == null ? null : BookingRecord.getBooking().getCustomer();
		status = super.getRequest().getPrincipal().hasRealm(customer) && BookingRecord != null;

		super.getResponse().setAuthorised(status);

	}

	@Override
	public void load() {
		int BookingRecordId;
		BookingRecord BookingRecord;

		BookingRecordId = super.getRequest().getData("id", int.class);
		BookingRecord = this.repository.findBookingRecordById(BookingRecordId);

		this.getBuffer().addData(BookingRecord);

	}

	@Override
	public void validate(final BookingRecord BookingRecord) {
		//		boolean confirmation;
		//
		//		confirmation = super.getRequest().getData("confirmation", boolean.class);
		//		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
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
