package acme.features.any.flightassignment;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Any;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flightassignment.FlightAssignment;

@GuiService
public class AnyFlightAssignmentShowService extends AbstractGuiService<Any, FlightAssignment> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AnyFlightAssignmentRepository repository;

	// AbstractService interface ----------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		FlightAssignment assignment;
		int id;

		id = super.getRequest().getData("id", int.class);
		assignment = this.repository.findFlightAssignmentById(id);
		status = assignment != null && !assignment.getDraftMode();

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		FlightAssignment assignment;
		int id;

		id = super.getRequest().getData("id", int.class);
		assignment = this.repository.findFlightAssignmentById(id);

		super.getBuffer().addData(assignment);
	}

	@Override
	public void unbind(final FlightAssignment assignment) {
		Dataset dataset;

		dataset = super.unbindObject(assignment, "duty", "momentLastUpdate", "currentStatus", "remarks", "draftMode");
		
		// Add information from relationships  
		if (assignment.getLeg() != null) {
			dataset.put("leg", assignment.getLeg().getDescription());
			dataset.put("legStatus", assignment.getLeg().getStatus());
			dataset.put("scheduledDeparture", assignment.getLeg().getScheduledDeparture());
			dataset.put("scheduledArrival", assignment.getLeg().getScheduledArrival());
			
			if (assignment.getLeg().getDepartureAirport() != null) {
				dataset.put("departureAirport", assignment.getLeg().getDepartureAirport().getName());
			}
			
			if (assignment.getLeg().getArrivalAirport() != null) {
				dataset.put("arrivalAirport", assignment.getLeg().getArrivalAirport().getName());
			}
		}
		
		if (assignment.getAllocatedFlightCrewMember() != null) {
			dataset.put("crewMember", "Flight Crew Member");
			dataset.put("crewMemberEmployeeCode", assignment.getAllocatedFlightCrewMember().getEmployeeCode());
		}

		super.getResponse().addData(dataset);
	}

}
