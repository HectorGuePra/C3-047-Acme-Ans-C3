
package acme.features.customer.bookingRecord;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.booking.BookingRecord;
import acme.realms.Customer;

@GuiService
public class CustomerBookingRecordListService extends AbstractGuiService<Customer, BookingRecord> {

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
		Collection<BookingRecord> BookingRecord;
		int customerId = super.getRequest().getPrincipal().getActiveRealm().getId();

		BookingRecord = this.repository.findBookingRecordByCustomerId(customerId);
		super.getBuffer().addData(BookingRecord);

	}

	@Override
	public void unbind(final BookingRecord BookingRecord) {
		Dataset dataset;

		dataset = super.unbindObject(BookingRecord, "booking.locatorCode", "passenger.passport");

		super.getResponse().addData(dataset);
	}

}
