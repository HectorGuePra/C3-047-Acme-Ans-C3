
package acme.features.flightcrewmember.activitylog;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activityLog.ActivityLog;
import acme.entities.flightassignment.FlightAssignment;
import acme.realms.flightcrewmember.FlightCrewMember;

@GuiService
public class ActivityLogUpdateService extends AbstractGuiService<FlightCrewMember, ActivityLog> {

	@Autowired
	private FlightCrewMemberActivityLogRepository repository;


	@Override
	public void authorise() {
		boolean status;
		ActivityLog log;
		int logId;

		logId = super.getRequest().getData("id", int.class);
		log = this.repository.findActivityLogById(logId);
		status = log.getDraftMode();
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		ActivityLog log;
		int id;

		id = super.getRequest().getData("id", int.class);
		log = this.repository.findActivityLogById(id);

		super.getBuffer().addData(log);
	}

	@Override
	public void bind(final ActivityLog activityLog) {
		super.bindObject(activityLog, "registrationMoment", "incidentType", "description", "severityLevel", "flightAssignment");
	}

	@Override
	public void validate(final ActivityLog activityLog) {
		;
	}

	@Override
	public void perform(final ActivityLog activityLog) {
		this.repository.save(activityLog);
	}

	@Override
	public void unbind(final ActivityLog activityLog) {
		Dataset dataset;
		List<FlightAssignment> assignments;
		assignments = this.repository.findAllFlightAssignments();

		SelectChoices assignmentChoices;
		assignmentChoices = SelectChoices.from(assignments, "leg.flightNumber", activityLog.getFlightAssignment());

		dataset = super.unbindObject(activityLog, "registrationMoment", "incidentType", "description", "severityLevel", "draftMode", "flightAssignment");
		dataset.put("assignmentChoices", assignmentChoices);
		dataset.put("masterId", activityLog.getFlightAssignment().getId());
		dataset.put("masterDraftMode", activityLog.getFlightAssignment().getDraftMode());

		super.getResponse().addData(dataset);
	}

}
