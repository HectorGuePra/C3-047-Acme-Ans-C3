
package acme.features.customer.passenger;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.Passenger;
import acme.realms.Customer;

@GuiService
public class CustomerPassengerShowService extends AbstractGuiService<Customer, Passenger> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerPassengerRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int passengerId;
		int customerId = super.getRequest().getPrincipal().getActiveRealm().getId();
		Passenger passenger;
		Customer customer;

		passengerId = super.getRequest().getData("id", int.class);
		passenger = this.repository.findPassengerById(passengerId);
		customer = this.repository.findCustomerById(customerId);
		status = super.getRequest().getPrincipal().hasRealm(customer) && passenger != null;

		super.getResponse().setAuthorised(status);

	}

	@Override
	public void load() {
		Passenger passenger;
		int id;

		id = super.getRequest().getData("id", int.class);
		passenger = this.repository.findPassengerById(id);

		this.getBuffer().addData(passenger);

	}

	@Override
	public void unbind(final Passenger passenger) {
		Dataset dataset;

		dataset = super.unbindObject(passenger, "fullName", "email", "passport", "birthDate", "specialNeeds", "draftMode");

		super.getResponse().addData(dataset);

	}
}
