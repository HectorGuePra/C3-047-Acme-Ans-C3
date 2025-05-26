
package acme.features.administrator.aircraft;

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
public class AdministratorAircraftShowService extends AbstractGuiService<Administrator, Aircraft> {

	// Internal state -------------------------------------------------------------------

	@Autowired
	private AdministratorAircraftRepository repository;

	// AbstractGuiService interface -----------------------------------------------------


	@Override
	public void authorise() {
		boolean status = true;
		if (!super.getRequest().hasData("id"))
			status = false;
		else {
			int id = super.getRequest().getData("id", int.class);
			Aircraft aircraft = this.repository.findById(id);
			if (aircraft == null)
				status = false;
		}
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Aircraft aircraft;
		int aircraftId;

		aircraftId = super.getRequest().getData("id", int.class);
		aircraft = this.repository.findById(aircraftId);

		super.getBuffer().addData(aircraft);
	}

	@Override
	public void unbind(final Aircraft aircraft) {
		SelectChoices choiceStatuses;
		SelectChoices choicesAirlines;
		Collection<Airline> airlines;

		Dataset dataset;

		airlines = this.repository.findAllAirlines();
		choicesAirlines = SelectChoices.from(airlines, "iataCode", aircraft.getAirline());

		choiceStatuses = SelectChoices.from(AircraftStatus.class, aircraft.getStatus());

		dataset = super.unbindObject(aircraft, "model", "regNumber", "capacity", "cargoWeight", "status", "notes", "draftMode");
		dataset.put("airlines", choicesAirlines);
		dataset.put("statuses", choiceStatuses);
		dataset.put("confirmation", false);

		super.getResponse().addData(dataset);

	}
}
