
package acme.features.manager.flights;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airport.Airport;
import acme.entities.flight.Flight;
import acme.realms.manager.Manager;

@GuiService
public class ManagerFlightListService extends AbstractGuiService<Manager, Flight> {

	@Autowired
	private ManagerFlightRepository repository;


	@Override
	public void authorise() {
		boolean status;

		status = super.getRequest().getPrincipal().hasRealmOfType(Manager.class);

		super.getResponse().setAuthorised(status);
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
		dataset.put("departure", Optional.ofNullable(flight.getDeparture()).map(Airport::getName).orElse(null));
		dataset.put("arrival", Optional.ofNullable(flight.getArrival()).map(Airport::getName).orElse(null));

		super.getResponse().addData(dataset);
	}
}
