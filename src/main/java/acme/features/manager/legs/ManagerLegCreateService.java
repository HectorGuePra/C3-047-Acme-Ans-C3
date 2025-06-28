
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
		boolean authorised = false;

		if (super.getRequest().hasData("flightId", int.class)) {
			int flightId = super.getRequest().getData("flightId", int.class);
			Flight flight = this.flightRepository.findFlightById(flightId);

			if (flight != null && flight.isDraftMode()) {
				int managerId = super.getRequest().getPrincipal().getActiveRealm().getId();
				Optional<Flight> optionalFlight = this.repository.findByIdAndManagerId(flightId, managerId);

				if (optionalFlight.isPresent()) {
					authorised = true;

					if (super.getRequest().hasData("id", boolean.class)) {
						int legId = super.getRequest().getData("id", int.class);
						if (legId != 0)
							authorised = false;
					}

					if (authorised && "POST".equals(super.getRequest().getMethod())) {
						int aircraftId = super.getRequest().getData("aircraft", int.class);
						int departureId = super.getRequest().getData("departureAirport", int.class);
						int arrivalId = super.getRequest().getData("arrivalAirport", int.class);

						Aircraft aircraft = this.repository.findAircraftByAircraftId(aircraftId);
						Airport departure = this.repository.findAirportByAirportId(departureId);
						Airport arrival = this.repository.findAirportByAirportId(arrivalId);

						List<Aircraft> managerAircrafts = this.repository.findAllAircraftsByManagerId(managerId);
						List<Airport> allAirports = this.repository.findAllAirports();

						if (aircraftId != 0 && (aircraft == null || !managerAircrafts.contains(aircraft)) || departureId != 0 && (departure == null || !allAirports.contains(departure))
							|| arrivalId != 0 && (arrival == null || !allAirports.contains(arrival)))
							authorised = false;
					}
				}
			}
		}

		super.getResponse().setAuthorised(authorised);
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
			boolean res;
			String flightNumber = leg.getAircraft().getAirline().getIataCode();
			res = leg.getFlightNumber().startsWith(flightNumber);
			super.state(res, "flightNumber", "manager.leg.form.error.flightNumberNotStartingWithAirlineIATACode");
			boolean duplicatedNumber = this.repository.findLegsByAirlineId(leg.getFlight().getManager().getAirline().getId()).stream().anyMatch(leg1 -> leg.getFlightNumber().equals(leg.getFlightNumber()) && leg.getId() != leg.getId());
			super.state(!duplicatedNumber, "flightNumber", "airline-manager.leg.form.error.duplicatedFlightNumber");
		}

		if (leg.getAircraft() != null) {
			boolean isAircraftActive = leg.getAircraft().getStatus().equals(AircraftStatus.IN_SERVICE);
			super.state(isAircraftActive, "aircraft", "acme.validation.flight.aircraft-under-maintenance.message");
		}

		Date now = MomentHelper.getCurrentMoment();
		boolean departureInFuture;
		boolean arrivalAfterDeparture;

		if (leg.getScheduledDeparture() != null && leg.getScheduledArrival() != null) {
			if (leg.getScheduledDeparture().after(now))
				departureInFuture = true;
			else
				departureInFuture = false;
			if (leg.getScheduledArrival().after(leg.getScheduledDeparture()))
				arrivalAfterDeparture = true;
			else
				arrivalAfterDeparture = false;
			super.state(departureInFuture, "scheduledDeparture", "acme.validation.leg.scheduledDeparture");
			super.state(arrivalAfterDeparture, "scheduledArrival", "acme.validation.leg.scheduledArrival");
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
