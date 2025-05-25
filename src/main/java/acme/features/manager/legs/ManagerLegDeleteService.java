
package acme.features.manager.legs;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircraft.Aircraft;
import acme.entities.airport.Airport;
import acme.entities.legs.Leg;
import acme.entities.legs.LegStatus;
import acme.realms.manager.Manager;

@GuiService
public class ManagerLegDeleteService extends AbstractGuiService<Manager, Leg> {

	@Autowired
	private ManagerLegRepository repository;


	@Override
	public void authorise() {
		boolean status = false;

		if (super.getRequest().hasData("id")) {
			int legId = super.getRequest().getData("id", int.class);
			int managerId = super.getRequest().getPrincipal().getActiveRealm().getId();

			Optional<Leg> optionalLeg = this.repository.findByLegId(legId);

			if (optionalLeg.isPresent()) {
				Leg leg = optionalLeg.get();

				if (leg.isDraftMode() && this.repository.findByIdAndManagerId(leg.getFlight().getId(), managerId).isPresent()) {
					status = true;

					if (super.getRequest().hasData("aircraft")) {
						int aircraftId = super.getRequest().getData("aircraft", int.class);
						Aircraft aircraft = this.repository.findAircraftByAircraftId(aircraftId);
						List<Aircraft> aircrafts = this.repository.findAllAircraftsByManagerId(managerId);

						if (aircraftId != 0 && aircraft == null || aircraft != null && !aircrafts.contains(aircraft))
							status = false;
					}

					List<Airport> airports = this.repository.findAllAirports();

					if (super.getRequest().hasData("departureAirport")) {
						int departureId = super.getRequest().getData("departureAirport", int.class);
						Airport departure = this.repository.findAirportByAirportId(departureId);

						if (departureId != 0 && departure == null || departure != null && !airports.contains(departure))
							status = false;
					}

					if (super.getRequest().hasData("arrivalAirport")) {
						int arrivalId = super.getRequest().getData("arrivalAirport", int.class);
						Airport arrival = this.repository.findAirportByAirportId(arrivalId);

						if (arrivalId != 0 && arrival == null || arrival != null && !airports.contains(arrival))
							status = false;
					}
				}
			}
		}

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Leg leg;
		int id;

		id = super.getRequest().getData("id", int.class);
		leg = this.repository.findLegByLegId(id);

		super.getBuffer().addData(leg);
	}

	@Override
	public void bind(final Leg leg) {
		int aircraftId;
		int airportArrivalId;
		int airportDepartureId;
		Aircraft aircraft;
		Airport departure;
		Airport arrival;

		aircraftId = super.getRequest().getData("aircraft", int.class);
		aircraft = this.repository.findAircraftByAircraftId(aircraftId);
		airportArrivalId = super.getRequest().getData("arrivalAirport", int.class);
		departure = this.repository.findAirportByAirportId(airportArrivalId);
		airportDepartureId = super.getRequest().getData("departureAirport", int.class);
		arrival = this.repository.findAirportByAirportId(airportDepartureId);

		super.bindObject(leg, "flightNumber", "scheduledDeparture", "scheduledArrival", "status");
		leg.setAircraft(aircraft);
		leg.setDepartureAirport(departure);
		leg.setArrivalAirport(arrival);
		leg.durationInHours();
	}

	@Override
	public void validate(final Leg leg) {
		;
	}

	@Override
	public void perform(final Leg leg) {
		this.repository.delete(leg);
	}

	@Override
	public void unbind(final Leg leg) {
		SelectChoices statusChoices;
		SelectChoices aircraftChoices;
		SelectChoices departureChoices;
		SelectChoices arrivalChoices;
		Dataset dataset;
		List<Aircraft> aircrafts;
		List<Airport> airports;
		int managerId;

		statusChoices = SelectChoices.from(LegStatus.class, leg.getStatus());
		managerId = super.getRequest().getPrincipal().getActiveRealm().getId();

		aircrafts = this.repository.findAllAircraftsByManagerId(managerId);
		aircraftChoices = SelectChoices.from(aircrafts, "regNumber", leg.getAircraft());
		airports = this.repository.findAllAirports();
		departureChoices = SelectChoices.from(airports, "name", leg.getDepartureAirport());
		arrivalChoices = SelectChoices.from(airports, "name", leg.getArrivalAirport());

		dataset = super.unbindObject(leg, "flightNumber", "scheduledDeparture", "scheduledArrival", "status", "draftMode");
		dataset.put("duration", leg.durationInHours());
		dataset.put("statuses", statusChoices);
		dataset.put("aircraft", aircraftChoices.getSelected().getKey());
		dataset.put("aircrafts", aircraftChoices);
		dataset.put("departureAirport", departureChoices.getSelected().getKey());
		dataset.put("departureAirports", departureChoices);
		dataset.put("arrivalAirport", arrivalChoices.getSelected().getKey());
		dataset.put("arrivalAirports", arrivalChoices);

		super.getResponse().addData(dataset);
	}

}
