
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
import acme.entities.flight.Flight;
import acme.entities.legs.Leg;
import acme.entities.legs.LegStatus;
import acme.features.manager.flights.ManagerFlightRepository;
import acme.realms.manager.Manager;

@GuiService
public class ManagerLegShowService extends AbstractGuiService<Manager, Leg> {

	@Autowired
	private ManagerLegRepository	repository;

	@Autowired
	private ManagerFlightRepository	flightRepository;


	@Override
	public void authorise() {
		int managerId;
		int legId;
		boolean status = true;

		if (!super.getRequest().hasData("id"))
			status = false;
		else {
			managerId = super.getRequest().getPrincipal().getActiveRealm().getId();
			legId = super.getRequest().getData("id", int.class);

			if (!this.repository.findByLegId(legId).isPresent())
				status = false;

			Optional<Leg> optionalLeg = this.repository.findByLegId(legId);

			if (optionalLeg.isPresent()) {
				Leg leg = optionalLeg.get();
				Optional<Flight> flight = this.repository.findByIdAndManagerId(leg.getFlight().getId(), managerId);

				if (flight.isEmpty())
					status = false;
			}
		}

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Leg leg;
		int id;

		id = super.getRequest().getData("id", int.class);
		if (!this.repository.findByLegId(id).isPresent())
			throw new RuntimeException("No leg with id: " + id);
		leg = this.repository.findLegByLegId(id);

		super.getBuffer().addData(leg);
	}

	@Override
	public void unbind(final Leg leg) {
		SelectChoices statusChoices;
		SelectChoices flightsChoices;
		SelectChoices aircraftChoices;
		SelectChoices departureChoices;
		SelectChoices arrivalChoices;
		Dataset dataset;
		List<Flight> flights;
		List<Aircraft> aircrafts;
		List<Airport> airports;
		int managerId;

		statusChoices = SelectChoices.from(LegStatus.class, leg.getStatus());

		if (!leg.isDraftMode()) {
			flights = this.flightRepository.findAllFlights();
			aircrafts = this.repository.findAllAircrafts();
		} else {
			managerId = super.getRequest().getPrincipal().getActiveRealm().getId();
			flights = this.flightRepository.findManagerFlightsByManagerId(managerId);
			aircrafts = this.repository.findAllAircraftsByManagerId(managerId);
		}

		flightsChoices = SelectChoices.from(flights, "tag", leg.getFlight());
		aircraftChoices = SelectChoices.from(aircrafts, "regNumber", leg.getAircraft());

		airports = this.repository.findAllAirports();
		departureChoices = SelectChoices.from(airports, "name", leg.getDepartureAirport());
		arrivalChoices = SelectChoices.from(airports, "name", leg.getArrivalAirport());

		dataset = super.unbindObject(leg, "flightNumber", "scheduledDeparture", "scheduledArrival", "status", "draftMode");
		dataset.put("duration", leg.durationInHours());
		dataset.put("statuses", statusChoices);
		dataset.put("flight", flightsChoices.getSelected().getKey());
		dataset.put("flights", flightsChoices);
		dataset.put("aircraft", aircraftChoices.getSelected().getKey());
		dataset.put("aircrafts", aircraftChoices);
		dataset.put("departureAirport", departureChoices.getSelected().getKey());
		dataset.put("departureAirports", departureChoices);
		dataset.put("arrivalAiport", arrivalChoices.getSelected().getKey());
		dataset.put("arrivalAirports", arrivalChoices);

		super.getResponse().addData(dataset);
	}

}
