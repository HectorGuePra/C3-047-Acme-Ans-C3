
package acme.features.manager.flights;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flight.Flight;
import acme.realms.manager.Manager;

@GuiService
public class ManagerFlightListService extends AbstractGuiService<Manager, Flight> {

	@Autowired
	private ManagerFlightRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		List<Flight> managerFlights;
		int managerId;

		managerId = super.getRequest().getPrincipal().getActiveRealm().getId();

		managerFlights = this.repository.findManagerFlightsByManagerId(managerId);

		super.getBuffer().addData(managerFlights);
	}

	@Override
	public void unbind(final Flight flight) {
		Dataset dataset;

		dataset = super.unbindObject(flight, "tag");
		dataset.put("departure", flight.getDeparture() != null ? flight.getDeparture().getName() : flight.getDeparture());
		dataset.put("arrival", flight.getArrival() != null ? flight.getArrival().getName() : flight.getArrival());

		super.getResponse().addData(dataset);
	}
}
