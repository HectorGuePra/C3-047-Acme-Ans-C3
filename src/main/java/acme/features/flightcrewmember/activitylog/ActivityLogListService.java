
package acme.features.flightcrewmember.activitylog;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activityLog.ActivityLog;
import acme.entities.flightassignment.FlightAssignment;
import acme.entities.legs.LegStatus;
import acme.realms.flightcrewmember.FlightCrewMember;

@GuiService
public class ActivityLogListService extends AbstractGuiService<FlightCrewMember, ActivityLog> {

	@Autowired
	private FlightCrewMemberActivityLogRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int masterId;
		int memberId;
		boolean isLegLanded;

		masterId = super.getRequest().getData("masterId", int.class);
		FlightAssignment assignment = this.repository.findFlightAssignmentById(masterId);
		memberId = assignment.getAllocatedFlightCrewMember().getId();

		isLegLanded = assignment.getLeg().equals(LegStatus.LANDED);

		status = memberId == super.getRequest().getPrincipal().getActiveRealm().getId() && !isLegLanded && !assignment.getDraftMode();
		super.getResponse().setAuthorised(status);

	}

	@Override
	public void load() {
		List<ActivityLog> logs;
		int masterId = super.getRequest().getData("masterId", int.class);
		logs = this.repository.findLogsByFlightAssignment(masterId);
		super.getResponse().addGlobal("masterId", masterId);
		super.getBuffer().addData(logs);
	}
	@Override
	public void bind(final ActivityLog log) {
		super.bindObject(log, "incidentType", "severityLevel");

	}
	@Override
	public void unbind(final ActivityLog log) {
		Dataset dataset;

		dataset = super.unbindObject(log, "incidentType", "registrationMoment", "severityLevel");
		super.getResponse().addData(dataset);
	}
}
