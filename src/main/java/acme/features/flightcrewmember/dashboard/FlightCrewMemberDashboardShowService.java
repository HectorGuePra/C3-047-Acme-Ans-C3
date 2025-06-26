
package acme.features.flightcrewmember.dashboard;

import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flightassignment.FlightAssignment;
import acme.forms.flightcrewmember.Dashboard;
import acme.realms.flightcrewmember.FlightCrewMember;

@GuiService
public class FlightCrewMemberDashboardShowService extends AbstractGuiService<FlightCrewMember, Dashboard> {

	// Internal state
	@Autowired
	private FlightCrewMemberDashboardRepository repository;

	// AbstractGuiService methods


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Dashboard dashboard;
		int memberId = super.getRequest().getPrincipal().getActiveRealm().getId();

		// Fechas para el último mes
		Date endDate = MomentHelper.getCurrentMoment();
		Date startDate = MomentHelper.deltaFromMoment(endDate, -30, ChronoUnit.DAYS);

		// Consultas
		List<String> lastFiveDestinations = this.repository.findLastFiveDestinations(memberId, PageRequest.of(0, 5));

		Integer countSeverity0to3 = this.repository.countLegsWithIncidentSeverity0to3(memberId);
		Integer countSeverity4to7 = this.repository.countLegsWithIncidentSeverity4to7(memberId);
		Integer countSeverity8to10 = this.repository.countLegsWithIncidentSeverity8to10(memberId);

		List<FlightCrewMember> crewInLastLeg = this.repository.findCrewMembersInLastLeg(memberId);

		List<FlightAssignment> confirmedAssignments = this.repository.findAssignmentsConfirmed(memberId);
		List<FlightAssignment> pendingAssignments = this.repository.findAssignmentsPending(memberId);
		List<FlightAssignment> cancelledAssignments = this.repository.findAssignmentsCancelled(memberId);

		Integer totalAssignmentsLastMonth = this.repository.countAssignmentsLastMonth(memberId, startDate, endDate);

		dashboard = new Dashboard();

		dashboard.setLastFiveDestinations(lastFiveDestinations);
		dashboard.setNumberOfLegsWithIncidentSeverity0To3(countSeverity0to3);
		dashboard.setNumberOfLegsWithIncidentSeverity4To7(countSeverity4to7);
		dashboard.setNumberOfLegsWithIncidentSeverity8To10(countSeverity8to10);
		dashboard.setCrewMembersInLastLeg(crewInLastLeg);
		dashboard.setAssignmentsConfirmed(confirmedAssignments);
		dashboard.setAssignmentsPending(pendingAssignments);
		dashboard.setAssignmentsCancelled(cancelledAssignments);

		dashboard.setAverageAssignmentsLastMonth(totalAssignmentsLastMonth.doubleValue());
		dashboard.setMinimumAssignmentsLastMonth(totalAssignmentsLastMonth);
		dashboard.setMaximumAssignmentsLastMonth(totalAssignmentsLastMonth);
		dashboard.setDeviationAssignmentsLastMonth(0.0);

		super.getBuffer().addData(dashboard);
	}

	@Override
	public void unbind(final Dashboard dashboard) {
		Dataset dataset = super.unbindObject(dashboard, "lastFiveDestinations", "numberOfLegsWithIncidentSeverity0To3", "numberOfLegsWithIncidentSeverity4To7", "numberOfLegsWithIncidentSeverity8To10", "crewMembersInLastLeg", "assignmentsConfirmed",
			"assignmentsPending", "assignmentsCancelled", "averageAssignmentsLastMonth", "minimumAssignmentsLastMonth", "maximumAssignmentsLastMonth", "deviationAssignmentsLastMonth");
		super.getResponse().addData(dataset);
	}
}
