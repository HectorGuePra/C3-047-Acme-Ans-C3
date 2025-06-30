package acme.features.any.flightassignment;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Any;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flightassignment.FlightAssignment;

@GuiService
public class AnyFlightAssignmentListService extends AbstractGuiService<Any, FlightAssignment> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AnyFlightAssignmentRepository repository;

	// AbstractService interface ----------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Collection<FlightAssignment> objects;
		objects = this.repository.findPublishedFlightAssignments();

		super.getBuffer().addData(objects);
	}

	@Override
	public void unbind(final FlightAssignment object) {
		Dataset dataset;

		dataset = super.unbindObject(object, "duty", "momentLastUpdate", "currentStatus");
		
		// Add additional information from relationships
		if (object.getLeg() != null) {
			dataset.put("legDescription", object.getLeg().getDescription());
			dataset.put("legStatus", object.getLeg().getStatus());
		}
		
		if (object.getAllocatedFlightCrewMember() != null) {
			dataset.put("crewMemberEmployeeCode", object.getAllocatedFlightCrewMember().getEmployeeCode());
		}

		super.getResponse().addData(dataset);
	}

}
