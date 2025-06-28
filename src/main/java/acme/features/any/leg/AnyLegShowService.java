
package acme.features.any.leg;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Any;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.legs.Leg;
import acme.entities.legs.LegStatus;

@GuiService
public class AnyLegShowService extends AbstractGuiService<Any, Leg> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AnyLegRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int legId = super.getRequest().getData("id", int.class);
		Leg leg = this.repository.findLegById(legId);
		status = leg != null;

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Leg leg;
		int id;

		id = super.getRequest().getData("id", int.class);
		leg = this.repository.findLegById(id);

		super.getBuffer().addData(leg);
	}

	@Override
	public void unbind(final Leg object) {

		Dataset dataset;

		dataset = super.unbindObject(object, "flightNumber", "scheduledDeparture", "scheduledArrival", "status", "draftMode");
		final SelectChoices departureAirportChoices;
		final SelectChoices arrivalAirportChoices;
		final SelectChoices aircraftChoices;
		final SelectChoices statusChoices;
		statusChoices = SelectChoices.from(LegStatus.class, object.getStatus());
		departureAirportChoices = SelectChoices.from(this.repository.findAirports(), "iataCode", object.getDepartureAirport());
		arrivalAirportChoices = SelectChoices.from(this.repository.findAirports(), "iataCode", object.getArrivalAirport());
		aircraftChoices = SelectChoices.from(this.repository.findActiveAircraftsByAirlineId(object.getFlight().getManager().getAirline().getId()), "regNumber", object.getAircraft());
		dataset.put("departureAirports", departureAirportChoices);
		dataset.put("arrivalAirports", arrivalAirportChoices);
		dataset.put("aircraftChoices", aircraftChoices);
		dataset.put("statuses", statusChoices);
		dataset.put("departureAirport", departureAirportChoices.getSelected().getKey());
		dataset.put("arrivalAirport", arrivalAirportChoices.getSelected().getKey());
		dataset.put("aircraft", aircraftChoices.getSelected().getKey());
		dataset.put("status", statusChoices.getSelected().getKey());
		super.getResponse().addData(dataset);
	}

}
