
package acme.features.any.flight;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Any;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flight.Flight;

@GuiService
public class AnyFlightListService extends AbstractGuiService<Any, Flight> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AnyFlightRepository repository;

	// AbstractService interface ----------------------------------------------


	@Override
	public void authorise() {

		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Collection<Flight> objects;
		objects = this.repository.findPublishedFlights();

		super.getBuffer().addData(objects);
	}

	@Override
	public void unbind(final Flight object) {
		Dataset dataset;

		dataset = super.unbindObject(object, "tag", "departure", "arrival");
		super.getResponse().addData(dataset);
	}

}
