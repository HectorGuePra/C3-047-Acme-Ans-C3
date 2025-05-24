
package acme.features.flightcrewmember.flightassignment;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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
import acme.realms.flightcrewmember.AvailabilityStatus;
import acme.realms.flightcrewmember.FlightCrewMember;

@GuiService
public class FlightAssignmentPublishService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	@Autowired
	private CrewMemberFlightAssignmentRepository repository;


	@Override
	public void authorise() {

		boolean status;
		int assignmentId;
		int userId;
		int memberId;
		FlightAssignment flightAssignment;

		assignmentId = super.getRequest().getData("id", int.class);
		flightAssignment = this.repository.findFlightAssignmentById(assignmentId);
		memberId = flightAssignment.getAllocatedFlightCrewMember().getId();
		userId = super.getRequest().getPrincipal().getActiveRealm().getId();
		status = flightAssignment.getDraftMode() && MomentHelper.isFuture(flightAssignment.getLeg().getScheduledDeparture()) && userId == memberId;

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

		int id = super.getRequest().getData("id", int.class);
		assignment = this.repository.findFlightAssignmentById(id);

		super.getBuffer().addData(assignment);
	}

	@Override
	public void bind(final FlightAssignment flightAssignment) {
		super.bindObject(flightAssignment, "duty", "currentStatus", "remarks", "leg", "allocatedFlightCrewMember");
	}

	@Override
	public void validate(final FlightAssignment flightAssignment) {
		Leg leg;
		FlightCrewMember member;
		boolean isCompleted;
		boolean availableMember;
		boolean alreadyOccupied;
		boolean validStatus;

		member = super.getRequest().getData("allocatedFlightCrewMember", FlightCrewMember.class);
		leg = super.getRequest().getData("leg", Leg.class);

		List<FlightAssignment> OverlappingFlightAssignments = this.repository.findFlightAssignmentsByFlightCrewMemberDuring(member.getId(), leg.getScheduledDeparture(), leg.getScheduledArrival()).stream()
			.filter(fa -> fa.getId() != flightAssignment.getId()).collect(Collectors.toList());

		isCompleted = leg.getStatus() == LegStatus.LANDED;
		super.state(!isCompleted, "leg", "acme.validation.member.leg-complete.message");

		alreadyOccupied = !OverlappingFlightAssignments.isEmpty();
		super.state(!alreadyOccupied, "leg", "acme.validation.member.overlapping.message");

		availableMember = member.getAvailabilityStatus().equals(AvailabilityStatus.AVAILABLE);
		super.state(availableMember, "flightCrewMember", "acme.validation.member.member-available.message");

		if (flightAssignment.getDuty() == CrewsDuty.PILOT) {
			boolean alreadyHasPilot = !this.repository.findFlightAssignmentByLegAndPilotDuty(leg.getId(), CrewsDuty.PILOT).isEmpty();
			super.state(!alreadyHasPilot, "duty", "acme.validation.member.pilot.message");
		}
		if (flightAssignment.getDuty() == CrewsDuty.CO_PILOT) {
			boolean alreadyHasCoPilot = !this.repository.findFlightAssignmentByLegAndCoPilotDuty(leg.getId(), CrewsDuty.CO_PILOT).isEmpty();
			super.state(!alreadyHasCoPilot, "duty", "acme.validation.member.co-pilot.message");
		}

		validStatus = flightAssignment.getCurrentStatus().equals(AssigmentStatus.CONFIRMED) || flightAssignment.getCurrentStatus().equals(AssigmentStatus.CANCELLED);
		super.state(validStatus, "currentStatus", "acme.validation.member.currentStatus");

		if (this.getBuffer().getErrors().hasErrors("duty"))
			super.state(flightAssignment.getDuty() != null, "duty", "acme.validation.member.noDuty.message");

		if (this.getBuffer().getErrors().hasErrors("currentStatus"))
			super.state(flightAssignment.getCurrentStatus() != null, "currentStatus", "acme.validation.member.noStatus.message");

		if (this.getBuffer().getErrors().hasErrors("leg"))
			super.state(flightAssignment.getLeg() != null, "leg", "acme.validation.member.noLeg.message");

		if (this.getBuffer().getErrors().hasErrors("remarks"))
			super.state(flightAssignment.getRemarks().length() <= 255, "remarks", "acme.validation.member.remarks.message");
		;
	}

	@Override
	public void perform(final FlightAssignment flightAssignment) {
		flightAssignment.setDraftMode(false);
		flightAssignment.setMomentLastUpdate(MomentHelper.getCurrentMoment());
		this.repository.save(flightAssignment);
	}

	@Override
	public void unbind(final FlightAssignment flightAssignment) {
		Dataset dataset;
		SelectChoices dutyChoice;
		SelectChoices currentStatusChoice;

		SelectChoices legChoice;
		Collection<Leg> legs;

		SelectChoices flightCrewMemberChoice;
		Collection<FlightCrewMember> flightCrewMembers;

		dutyChoice = SelectChoices.from(CrewsDuty.class, flightAssignment.getDuty());
		currentStatusChoice = SelectChoices.from(AssigmentStatus.class, flightAssignment.getCurrentStatus());

		Collection<LegStatus> legStatus = List.of(LegStatus.ON_TIME, LegStatus.DELAYED);
		legs = this.repository.findAllLegsAvailables(legStatus);
		legChoice = SelectChoices.from(legs, "description", flightAssignment.getLeg());

		flightCrewMembers = this.repository.findAllFlightCrewMembers();
		flightCrewMemberChoice = SelectChoices.from(flightCrewMembers, "id", flightAssignment.getAllocatedFlightCrewMember());

		dataset = super.unbindObject(flightAssignment, "duty", "momentLastUpdate", "currentStatus", "remarks", "leg", "allocatedFlightCrewMember", "draftMode");
		dataset.put("dutyChoice", dutyChoice);
		dataset.put("currentStatusChoice", currentStatusChoice);
		dataset.put("legChoice", legChoice);
		dataset.put("flightCrewMemberChoice", flightCrewMemberChoice);

		dataset.put("memberId", flightAssignment.getAllocatedFlightCrewMember().getId());

		super.getResponse().addData(dataset);
	}
}
