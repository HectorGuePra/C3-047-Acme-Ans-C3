
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
		boolean authorised = true;

		if (super.getRequest().hasData("id")) {
			int passengerId = super.getRequest().getData("id", int.class);
			Passenger passenger = this.repository.findPassengerById(passengerId);
			if (passenger == null)
				authorised = false;
			else {
				int customerId = super.getRequest().getPrincipal().getActiveRealm().getId();
				Customer customer = this.repository.findCustomerById(customerId);
				authorised = super.getRequest().getPrincipal().hasRealm(customer) && passenger != null && passenger.getCustomer().equals(customer);
			}

		} else
			authorised = false;

		super.getResponse().setAuthorised(authorised);

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
