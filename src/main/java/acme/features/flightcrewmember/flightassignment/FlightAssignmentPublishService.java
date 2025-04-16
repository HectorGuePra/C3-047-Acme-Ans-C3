
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
public class FlightAssignmentPublishService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	@Autowired
	private CrewMemberFlightAssignmentRepository repository;


	@Override
	public void authorise() {

		boolean status;
		int masterId;
		FlightAssignment flightAssignment;

		masterId = super.getRequest().getData("id", int.class);
		flightAssignment = this.repository.findFlightAssignmentById(masterId);

		status = flightAssignment.getDraftMode() && MomentHelper.isFuture(flightAssignment.getLeg().getScheduledDeparture());

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
		AvailabilityStatus available;
		available = AvailabilityStatus.AVAILABLE;

		SelectChoices legChoice;
		Collection<Leg> legs;

		SelectChoices flightCrewMemberChoice;
		Collection<FlightCrewMember> flightCrewMembers;

		dutyChoice = SelectChoices.from(CrewsDuty.class, flightAssignment.getDuty());
		currentStatusChoice = SelectChoices.from(AssigmentStatus.class, flightAssignment.getCurrentStatus());

		legs = this.repository.findAllLegs();
		legChoice = SelectChoices.from(legs, "id", flightAssignment.getLeg());

		flightCrewMembers = this.repository.findAllCrewMembersAvailables(available);
		flightCrewMemberChoice = SelectChoices.from(flightCrewMembers, "id", flightAssignment.getAllocatedFlightCrewMember());

		dataset = super.unbindObject(flightAssignment, "duty", "momentLastUpdate", "currentStatus", "remarks", "leg", "allocatedFlightCrewMember", "draftMode");
		dataset.put("dutyChoice", dutyChoice);
		dataset.put("currentStatusChoice", currentStatusChoice);
		dataset.put("legChoice", legChoice);
		dataset.put("flightCrewMemberChoice", flightCrewMemberChoice);

		super.getResponse().addData(dataset);
	}
}
