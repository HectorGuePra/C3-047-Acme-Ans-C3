
package acme.features.flightcrewmember.activitylog;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activityLog.ActivityLog;
import acme.entities.flightassignment.FlightAssignment;
import acme.realms.flightcrewmember.FlightCrewMember;

@GuiService
public class ActivityLogListService extends AbstractGuiService<FlightCrewMember, ActivityLog> {

	@Autowired
	private FlightCrewMemberActivityLogRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int masterId;
		FlightAssignment flightAssignment;

		masterId = super.getRequest().getData("masterId", int.class);
		flightAssignment = this.repository.findFlightAssignmentById(masterId);
		status = flightAssignment != null;
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		List<ActivityLog> logs;
		int masterId = super.getRequest().getData("masterId", int.class);
		logs = this.repository.findLogsByFlightAssignment(masterId);

		super.getBuffer().addData(logs);
	}
	@Override
	public void bind(final ActivityLog log) {
		super.bindObject(log, "incidentType", "description", "severityLevel");

	}
	@Override
	public void unbind(final ActivityLog log) {
		Dataset dataset;

		dataset = super.unbindObject(log, "incidentType", "description", "severityLevel");
		super.getResponse().addData(dataset);
	}
}
