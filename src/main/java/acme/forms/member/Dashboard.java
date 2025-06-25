
package acme.forms.member;

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

	Integer						numberOfLegsWithIncidentSeverity_0_to_3;
	Integer						numberOfLegsWithIncidentSeverity_4_to_7;
	Integer						numberOfLegsWithIncidentSeverity_8_to_10;

	List<FlightCrewMember>		crewMembersInLastLeg;

	List<FlightAssignment>		assignmentsConfirmed;
	List<FlightAssignment>		assignmentsPending;
	List<FlightAssignment>		assignmentsLanded;

	Double						averageAssignmentsLastMonth;
	Integer						minimumAssignmentsLastMonth;
	Integer						maximumAssignmentsLastMonth;
	Double						deviationAssignmentsLastMonth;
}
