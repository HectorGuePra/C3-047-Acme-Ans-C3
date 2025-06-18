
package acme.features.flightcrewmember.flightassignment;

import java.util.Collection;
import java.util.List;

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
import acme.entities.legs.LegStatus;
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

		if (status && super.getRequest().getMethod().equals("POST")) {
			if (super.getRequest().hasData("leg")) {
				Integer legId = super.getRequest().getData("leg", int.class);
				Leg leg = this.repository.findLegById(legId);
				Collection<LegStatus> legStatus = List.of(LegStatus.ON_TIME, LegStatus.DELAYED);
				Collection<Leg> availableLegs = this.repository.findAllLegsAvailables(legStatus);
				boolean legIsAvailable = availableLegs.contains(leg);
				status = legId == 0 || legIsAvailable;
			}

			if (status && super.getRequest().hasData("duty")) {
				String duty = super.getRequest().getData("duty", String.class);
				status = duty.equals("0") || duty.equals("PILOT") || duty.equals("CO_PILOT") || duty.equals("LEAD_ATTENDANT") || duty.equals("CABIN_ATTENDANT");
			}

			if (status && super.getRequest().hasData("currentStatus")) {
				String currentStatus = super.getRequest().getData("currentStatus", String.class);
				status = currentStatus.equals("0") || currentStatus.equals("CONFIRMED") || currentStatus.equals("PENDING") || currentStatus.equals("CANCELLED");
			}
		}
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
		super.bindObject(assignment, "duty", "currentStatus", "remarks", "leg");
	}

	@Override
	public void validate(final FlightAssignment assignment) {

		if (this.getBuffer().getErrors().hasErrors("duty"))
			super.state(assignment.getDuty() != null, "duty", "acme.validation.member.noDuty.message");

		if (this.getBuffer().getErrors().hasErrors("currentStatus"))
			super.state(assignment.getCurrentStatus() != null, "currentStatus", "acme.validation.member.noStatus.message");

		if (this.getBuffer().getErrors().hasErrors("leg"))
			super.state(assignment.getLeg() != null, "leg", "acme.validation.member.noLeg.message");

		if (this.getBuffer().getErrors().hasErrors("remarks"))
			super.state(assignment.getRemarks().length() <= 255, "remarks", "acme.validation.member.remarks.message");
		;
	}

	@Override
	public void perform(final FlightAssignment assignment) {
		assert assignment != null;
		assignment.setMomentLastUpdate(MomentHelper.getCurrentMoment());
		this.repository.save(assignment);
	}

	@Override
	public void unbind(final FlightAssignment assignment) {
		Dataset dataset;
		SelectChoices dutyChoice;
		SelectChoices currentStatusChoice;

		SelectChoices legChoice;
		Collection<Leg> legs;

		FlightCrewMember member;
		member = (FlightCrewMember) super.getRequest().getPrincipal().getActiveRealm();
		dutyChoice = SelectChoices.from(CrewsDuty.class, assignment.getDuty());
		currentStatusChoice = SelectChoices.from(AssigmentStatus.class, assignment.getCurrentStatus());

		Collection<LegStatus> legStatus = List.of(LegStatus.ON_TIME, LegStatus.DELAYED);
		legs = this.repository.findAllLegsAvailables(legStatus);
		legChoice = SelectChoices.from(legs, "description", assignment.getLeg());

		dataset = super.unbindObject(assignment, "duty", "momentLastUpdate", "currentStatus", "remarks", "allocatedFlightCrewMember", "leg", "draftMode");
		dataset.put("dutyChoice", dutyChoice);
		dataset.put("currentStatusChoice", currentStatusChoice);
		dataset.put("legChoice", legChoice);
		dataset.put("allocatedFlightCrewMember", member.getId());

		super.getResponse().addData(dataset);
	}

}
