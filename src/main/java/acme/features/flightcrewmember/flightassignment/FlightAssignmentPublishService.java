
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
		List<FlightAssignment> OverlappingFlightAssignments;
		boolean isCompleted;
		boolean alreadyHasPilot;
		boolean alreadyHasCoPilot;
		boolean availableMember;
		boolean alreadyOccupied;
		boolean validStatus;
		CrewsDuty pilot = CrewsDuty.PILOT;
		CrewsDuty coPilot = CrewsDuty.CO_PILOT;

		member = super.getRequest().getData("allocatedFlightCrewMember", FlightCrewMember.class);
		leg = super.getRequest().getData("leg", Leg.class);
		OverlappingFlightAssignments = this.repository.findFlightAssignmentsByFlightCrewMemberDuring(member.getId(), leg.getScheduledDeparture(), leg.getScheduledArrival());
		CrewsDuty duty = super.getRequest().getData("duty", CrewsDuty.class);
		List<FlightAssignment> flightsWithPilots = this.repository.findFlightAssignmentByLegAndPilotDuty(leg.getId(), pilot);
		List<FlightAssignment> flightsWithCoPilots = this.repository.findFlightAssignmentByLegAndCoPilotDuty(leg.getId(), coPilot);

		isCompleted = leg.getStatus() == LegStatus.LANDED;
		alreadyOccupied = OverlappingFlightAssignments.isEmpty();
		availableMember = member.getAvailabilityStatus().equals(AvailabilityStatus.AVAILABLE);

		alreadyHasPilot = flightsWithPilots.isEmpty() && duty.equals(CrewsDuty.PILOT);
		alreadyHasCoPilot = flightsWithCoPilots.isEmpty() && duty.equals(CrewsDuty.CO_PILOT);

		validStatus = flightAssignment.getCurrentStatus().equals(AssigmentStatus.CONFIRMED) || flightAssignment.getCurrentStatus().equals(AssigmentStatus.CANCELLED);
		//TODO corregir el validador pq no se pq a la segunda no funciona
		super.state(!alreadyHasPilot, "duty", "acme.validation.pilot.message");
		super.state(!alreadyHasCoPilot, "duty", "acme.validation.co-pilot.message");
		super.state(validStatus, "currentStatus", "acme.validation.currentStatus");
		super.state(alreadyOccupied, "leg", "acme.validation.overlapping.message");
		super.state(availableMember, "flightCrewMember", "acme.validation.member-available.message");
		super.state(!isCompleted, "leg", "acme.validation.leg-complete.message");
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

		legs = this.repository.findAllLegs();
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
