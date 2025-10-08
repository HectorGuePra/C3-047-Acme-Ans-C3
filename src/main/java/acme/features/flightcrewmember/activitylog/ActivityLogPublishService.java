
package acme.features.flightcrewmember.activitylog;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activityLog.ActivityLog;
import acme.entities.flightassignment.FlightAssignment;
import acme.entities.legs.LegStatus;
import acme.realms.flightcrewmember.FlightCrewMember;

@GuiService
public class ActivityLogPublishService extends AbstractGuiService<FlightCrewMember, ActivityLog> {

	@Autowired
	private FlightCrewMemberActivityLogRepository repository;


	@Override
	public void authorise() {
		boolean status;
		ActivityLog log;
		int logId;
		int memberId;
		boolean isLegLanded;
		FlightAssignment assignment;

		logId = super.getRequest().getData("id", int.class);
		log = this.repository.findActivityLogById(logId);
		assignment = log.getFlightAssignment();
		memberId = assignment.getAllocatedFlightCrewMember().getId();

		isLegLanded = assignment.getLeg().getStatus().equals(LegStatus.LANDED);

		status = log.getDraftMode() && memberId == super.getRequest().getPrincipal().getActiveRealm().getId() && isLegLanded && !assignment.getDraftMode();
		;
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
		super.bindObject(activityLog, "incidentType", "description", "severityLevel");
	}

	@Override
	public void validate(final ActivityLog activityLog) {
		;
	}

	@Override
	public void perform(final ActivityLog activityLog) {
		activityLog.setDraftMode(false);
		this.repository.save(activityLog);
	}

	@Override
	public void unbind(final ActivityLog activityLog) {
		Dataset dataset;

		dataset = super.unbindObject(activityLog, "registrationMoment", "incidentType", "description", "severityLevel", "draftMode", "flightAssignment");
		dataset.put("masterId", activityLog.getFlightAssignment().getId());
		dataset.put("masterDraftMode", activityLog.getFlightAssignment().getDraftMode());

		super.getResponse().addData(dataset);
	}

}
