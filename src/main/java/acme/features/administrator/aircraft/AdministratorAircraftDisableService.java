
package acme.features.administrator.aircraft;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircraft.Aircraft;
import acme.entities.aircraft.AircraftStatus;
import acme.entities.airline.Airline;

@GuiService
public class AdministratorAircraftDisableService extends AbstractGuiService<Administrator, Aircraft> {

	// Internal state -------------------------------------------------------------------

	@Autowired
	private AdministratorAircraftRepository repository;

	// AbstractGuiService interface -----------------------------------------------------


	@Override
	public void authorise() {
		boolean status = true;
		String metodo = super.getRequest().getMethod();
		if (!super.getRequest().hasData("id"))
			status = false;
		else {
			int id = super.getRequest().getData("id", int.class);
			Aircraft aircraft = this.repository.findById(id);
			if (aircraft == null)
				status = false;
			if (metodo.equals("POST")) {
				int airlineId = super.getRequest().getData("airline", int.class);
				String aStatus = super.getRequest().getData("status", String.class);
				if (aStatus == null || aStatus.trim().isEmpty() || Arrays.stream(AircraftStatus.values()).noneMatch(s -> s.name().equals(aStatus)) && !aStatus.equals("0"))
					status = false;
				Airline airline = this.repository.findAirlineById(airlineId);
				if (airline == null && airlineId != 0)
					status = false;
				if (aircraft.getDraftMode())
					status = false;
			}
		}
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Aircraft aircraft;
		int id;

		id = super.getRequest().getData("id", int.class);
		aircraft = this.repository.findById(id);

		super.getBuffer().addData(aircraft);
	}

	@Override
	public void bind(final Aircraft aircraft) {
		super.bindObject(aircraft, "status");
	}

	@Override
	public void validate(final Aircraft aircraft) {
		boolean confirmation;

		confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
	}

	@Override
	public void perform(final Aircraft aircraft) {
		aircraft.setStatus(AircraftStatus.UNDER_MAINTENANCE);
		this.repository.save(aircraft);
	}

	@Override
	public void unbind(final Aircraft aircraft) {
		SelectChoices choicesStatuses;
		SelectChoices choicesAirlines;
		Collection<Airline> airlines;

		Dataset dataset;

		choicesStatuses = SelectChoices.from(AircraftStatus.class, aircraft.getStatus());
		airlines = this.repository.findAllAirlines();
		choicesAirlines = SelectChoices.from(airlines, "name", aircraft.getAirline());

		dataset = super.unbindObject(aircraft, "model", "regNumber", "capacity", "cargoWeight", "status", "notes", "draftMode");
		dataset.put("statuses", choicesStatuses);
		dataset.put("airlines", choicesAirlines);
		dataset.put("confirmation", false);

		super.getResponse().addData(dataset);
	}
}
