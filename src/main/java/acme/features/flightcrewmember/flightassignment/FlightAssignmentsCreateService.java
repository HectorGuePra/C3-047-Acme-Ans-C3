
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
public class FlightAssignmentsCreateService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	@Autowired
	private CrewMemberFlightAssignmentRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void validate(final FlightAssignment assignment) {
		;
	}

	@Override
	public void load() {
		FlightAssignment assignment;
		FlightCrewMember member;
		member = (FlightCrewMember) super.getRequest().getPrincipal().getActiveRealm();

		assignment = new FlightAssignment();
		assignment.setDraftMode(true);
		assignment.setMomentLastUpdate(MomentHelper.getCurrentMoment());
		assignment.setAllocatedFlightCrewMember(member);
		super.getBuffer().addData(assignment);
	}

	@Override
	public void bind(final FlightAssignment assignment) {
		super.bindObject(assignment, "duty", "currentStatus", "remarks", "allocatedFlightCrewMember", "leg");
	}

	@Override
	public void perform(final FlightAssignment flightAssignment) {
		flightAssignment.setMomentLastUpdate(MomentHelper.getCurrentMoment());
		this.repository.save(flightAssignment);
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
