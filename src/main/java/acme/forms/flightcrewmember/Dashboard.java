
package acme.forms.flightcrewmember;

import java.util.List;

import acme.client.components.basis.AbstractForm;
import acme.entities.flightassignment.FlightAssignment;
import acme.realms.flightcrewmember.FlightCrewMember;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Dashboard extends AbstractForm {

	private static final long	serialVersionUID	= 1L;

	List<String>				lastFiveDestinations;

	Integer						numberOfLegsWithIncidentSeverity0To3;
	Integer						numberOfLegsWithIncidentSeverity4To7;
	Integer						numberOfLegsWithIncidentSeverity8To10;

	List<FlightCrewMember>		crewMembersInLastLeg;

	List<FlightAssignment>		assignmentsConfirmed;
	List<FlightAssignment>		assignmentsPending;
	List<FlightAssignment>		assignmentsCancelled;

	Double						averageAssignmentsLastMonth;
	Integer						minimumAssignmentsLastMonth;
	Integer						maximumAssignmentsLastMonth;
	Double						deviationAssignmentsLastMonth;
}
