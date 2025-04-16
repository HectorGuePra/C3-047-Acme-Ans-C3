
package acme.features.customer.passenger;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.Passenger;
import acme.realms.Customer;

@GuiService
public class CustomerPassengerCreateService extends AbstractGuiService<Customer, Passenger> {

	@Autowired
	private CustomerPassengerRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Passenger passenger;
		Customer customer = (Customer) super.getRequest().getPrincipal().getActiveRealm();
		passenger = new Passenger();
		passenger.setCustomer(customer);
		passenger.setDraftMode(true);
		super.getBuffer().addData(passenger);
	}

	@Override
	public void bind(final Passenger passenger) {
		super.bindObject(passenger, "fullName", "email", "passport", "birthDate", "specialNeeds");
	}

	@Override
	public void perform(final Passenger passenger) {
		this.repository.save(passenger);
	}

	@Override
	public void validate(final Passenger passenger) {
		int passengerId;
		boolean existsDuplicatedPassport;

		passengerId = super.getRequest().getData("id", int.class);
		existsDuplicatedPassport = this.repository.existsPassengerWithDuplicatedPassport(passenger.getPassport(), passengerId);
		super.state(!existsDuplicatedPassport, "passport", "acme.validation.confirmation.passenger.passport");
	}

	@Override
	public void unbind(final Passenger passenger) {
		Dataset dataset;
		dataset = super.unbindObject(passenger, "fullName", "email", "passport", "birthDate", "specialNeeds", "draftMode");
		super.getResponse().addData(dataset);

	}

}
