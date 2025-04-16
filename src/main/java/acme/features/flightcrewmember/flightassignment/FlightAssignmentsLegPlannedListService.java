
package acme.features.flightcrewmember.flightassignment;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flightassignment.FlightAssignment;
import acme.entities.legs.LegStatus;
import acme.realms.flightcrewmember.FlightCrewMember;

@GuiService
public class FlightAssignmentsLegPlannedListService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	@Autowired
	private CrewMemberFlightAssignmentRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Collection<LegStatus> legStatus = List.of(LegStatus.ON_TIME, LegStatus.DELAYED);
		int memberId;
		Collection<FlightAssignment> assignments;

		memberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		assignments = this.repository.assignmentsPlannedLegs(legStatus, memberId);

		super.getBuffer().addData(assignments);

	}
	@Override
	public void unbind(final FlightAssignment assignments) {
		Dataset dataset;

		dataset = super.unbindObject(assignments, "duty", "momentLastUpdate", "currentStatus");
		//Revisarlo 
		super.addPayload(dataset, assignments, "leg.status", "draftMode", "allocatedFlightCrewMember.identity.fullName", "remarks");
		super.getResponse().addData(dataset);
	}
}
