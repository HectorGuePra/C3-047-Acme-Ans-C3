
package acme.features.flightcrewmember.flightassignment;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flightassignment.AssigmentStatus;
import acme.entities.flightassignment.CrewsDuty;
import acme.entities.flightassignment.FlightAssignment;
import acme.entities.legs.Leg;
import acme.realms.flightcrewmember.AvailabilityStatus;
import acme.realms.flightcrewmember.FlightCrewMember;

@GuiService
public class FlightAssignmentUpdateService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	@Autowired
	private CrewMemberFlightAssignmentRepository repository;


	@Override
	public void authorise() {
		FlightAssignment flightAssignment;
		boolean status;
		int assignmentId;
		int memberId;

		assignmentId = super.getRequest().getData("id", int.class);
		flightAssignment = this.repository.findFlightAssignmentById(assignmentId);
		memberId = flightAssignment == null ? null : super.getRequest().getPrincipal().getActiveRealm().getId();
		status = flightAssignment != null && flightAssignment.getAllocatedFlightCrewMember().getId() == memberId && flightAssignment.getDraftMode();

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
	public void bind(final FlightAssignment assignment) {
		super.bindObject(assignment, "duty", "currentStatus", "remarks", "leg", "allocatedFlightCrewMember");
	}

	@Override
	public void validate(final FlightAssignment assignment) {
		;
	}

	@Override
	public void perform(final FlightAssignment assignment) {
		assignment.setMomentLastUpdate(MomentHelper.getCurrentMoment());
		this.repository.save(assignment);
	}

	@Override
	public void unbind(final FlightAssignment assignment) {
		Dataset dataset;
		SelectChoices dutyChoice;
		SelectChoices currentStatusChoice;
		AvailabilityStatus available;
		available = AvailabilityStatus.AVAILABLE;

		SelectChoices legChoice;
		Collection<Leg> legs;

		SelectChoices flightCrewMemberChoice;
		Collection<FlightCrewMember> flightCrewMembers;

		dutyChoice = SelectChoices.from(CrewsDuty.class, assignment.getDuty());
		currentStatusChoice = SelectChoices.from(AssigmentStatus.class, assignment.getCurrentStatus());

		//todo cambiar para que la leg sea de las pendings
		legs = this.repository.findAllLegs();
		legChoice = SelectChoices.from(legs, "id", assignment.getLeg());

		//todo cambiar para que los crew members esten en available
		flightCrewMembers = this.repository.findAllCrewMembersAvailables(available);
		flightCrewMemberChoice = SelectChoices.from(flightCrewMembers, "id", assignment.getAllocatedFlightCrewMember());

		dataset = super.unbindObject(assignment, "duty", "momentLastUpdate", "currentStatus", "remarks", "allocatedFlightCrewMember", "leg", "draftMode");
		dataset.put("dutyChoice", dutyChoice);
		dataset.put("currentStatusChoice", currentStatusChoice);
		dataset.put("legChoice", legChoice);
		dataset.put("flightCrewMemberChoice", flightCrewMemberChoice);

		super.getResponse().addData(dataset);
	}

}
