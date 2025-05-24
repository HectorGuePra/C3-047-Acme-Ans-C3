
package acme.features.manager.legs;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircraft.Aircraft;
import acme.entities.aircraft.AircraftStatus;
import acme.entities.airport.Airport;
import acme.entities.flight.Flight;
import acme.entities.legs.Leg;
import acme.entities.legs.LegStatus;
import acme.features.manager.flights.ManagerFlightRepository;
import acme.realms.manager.Manager;

@GuiService
public class ManagerLegCreateService extends AbstractGuiService<Manager, Leg> {

	@Autowired
	private ManagerLegRepository	repository;

	@Autowired
	private ManagerFlightRepository	flightRepository;


	@Override
	public void authorise() {
		boolean authorized = true;
		int aircraftId;
		int departureId;
		int arrivalId;
		Aircraft aircraft;
		Airport departure;
		Airport arrival;
		List<Aircraft> aircrafts;
		List<Airport> airports;
		String metodo = super.getRequest().getMethod();

		Integer flightId = super.getRequest().getData("flightId", int.class);

		if (this.flightRepository.findFlightById(flightId) == null)
			authorized = false;

		Flight flight = this.flightRepository.findFlightById(flightId);
		if (this.flightRepository.findFlightById(flightId) != null)
			if (!flight.getDraftMode())
				authorized = false;
		Integer managerId = super.getRequest().getPrincipal().getActiveRealm().getId();
		Optional<Flight> optionalFlight = this.repository.findByIdAndManagerId(flightId, managerId);

		if (super.getRequest().hasData("id", boolean.class)) {
			int legId = super.getRequest().getData("id", int.class);
			authorized &= legId == 0;
		}
		super.getResponse().setAuthorised(authorized);

		if (optionalFlight.isEmpty())
			authorized = false;
		else if (metodo.equals("POST")) {
			aircraftId = super.getRequest().getData("aircraft", int.class);
			aircraft = this.repository.findAircraftByAircraftId(aircraftId);
			aircrafts = this.repository.findAllAircraftsByManagerId(managerId);

			if (aircraft == null && aircraftId != 0)
				authorized = false;

			if (aircraft != null && !aircrafts.contains(aircraft))
				authorized = false;

			departureId = super.getRequest().getData("departureAirport", int.class);
			departure = this.repository.findAirportByAirportId(departureId);

			arrivalId = super.getRequest().getData("arrivalAirport", int.class);
			arrival = this.repository.findAirportByAirportId(arrivalId);

			airports = this.repository.findAllAirports();

			if (departure == null && departureId != 0)
				authorized = false;

			if (departure != null && !airports.contains(departure))
				authorized = false;

			if (arrival == null && arrivalId != 0)
				authorized = false;

			if (arrival != null && !airports.contains(arrival))
				authorized = false;

		}

		super.getResponse().setAuthorised(authorized);
	}

	@Override
	public void load() {
		Leg leg;
		Flight flight;

		Integer flightId = super.getRequest().getData("flightId", int.class);
		flight = this.flightRepository.findFlightById(flightId);

		leg = new Leg();
		leg.setFlight(flight);
		leg.setDraftMode(true);

		super.getBuffer().addData(leg);
	}

	@Override
	public void bind(final Leg leg) {
		int aircraftId;
		int departureId;
		int arrivalId;
		Aircraft aircraft;
		Airport departure;
		Airport arrival;

		aircraftId = super.getRequest().getData("aircraft", int.class);
		aircraft = this.repository.findAircraftByAircraftId(aircraftId);
		departureId = super.getRequest().getData("departureAirport", int.class);
		departure = this.repository.findAirportByAirportId(departureId);
		arrivalId = super.getRequest().getData("arrivalAirport", int.class);
		arrival = this.repository.findAirportByAirportId(arrivalId);
		super.bindObject(leg, "flightNumber", "scheduledDeparture", "scheduledArrival", "status");
		leg.setAircraft(aircraft);
		leg.setDepartureAirport(departure);
		leg.setArrivalAirport(arrival);
	}

	@Override
	public void validate(final Leg leg) {
		if (leg.getAircraft() != null) {
			boolean isAircraftActive = leg.getAircraft().getStatus().equals(AircraftStatus.IN_SERVICE);
			super.state(isAircraftActive, "aircraft", "acme.validation.flight.aircraft-under-maintenance.message");
		}

		if (leg.getScheduledArrival() != null && leg.getScheduledDeparture() != null) {
			Date currentDate = MomentHelper.getCurrentMoment();
			super.state(currentDate.before(leg.getScheduledDeparture()), "scheduledDeparture", "acme.validation.leg.past-date.message");
			super.state(currentDate.before(leg.getScheduledArrival()), "scheduledArrival", "acme.validation.leg.past-date.message");
		}
	}

	@Override
	public void perform(final Leg leg) {
		this.repository.save(leg);
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
		dataset.put("statuses", statusChoices);
		dataset.put("aircraft", aircraftChoices.getSelected().getKey());
		dataset.put("aircrafts", aircraftChoices);
		dataset.put("departureAirport", departureChoices.getSelected().getKey());
		dataset.put("departureAirports", departureChoices);
		dataset.put("arrivalAirport", arrivalChoices.getSelected().getKey());
		dataset.put("arrivalAirports", arrivalChoices);
		dataset.put("flight", leg.getFlight());
		dataset.put("flightId", leg.getFlight().getId());

		super.getResponse().addData(dataset);
	}

}
