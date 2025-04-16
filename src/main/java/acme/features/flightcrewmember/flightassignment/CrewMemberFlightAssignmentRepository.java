
package acme.features.flightcrewmember.flightassignment;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.activityLog.ActivityLog;
import acme.entities.flightassignment.FlightAssignment;
import acme.entities.legs.Leg;
import acme.entities.legs.LegStatus;
import acme.realms.flightcrewmember.AvailabilityStatus;
import acme.realms.flightcrewmember.FlightCrewMember;

@Repository
public interface CrewMemberFlightAssignmentRepository extends AbstractRepository {

	@Query("select f from FlightAssignment f where f.leg.status = ?1 and f.allocatedFlightCrewMember.id = ?2")
	Collection<FlightAssignment> assignmentsLandedLegs(LegStatus legStatus, Integer member);

	@Query("select f from FlightAssignment f where f.leg.status in ?1 and f.allocatedFlightCrewMember.id = ?2")
	Collection<FlightAssignment> assignmentsPlannedLegs(Collection<LegStatus> legStatuses, Integer member);

	@Query("select f from FlightAssignment f where f.id = ?1")
	FlightAssignment findFlightAssignmentById(int id);

	@Query("select m from FlightCrewMember m where m.availabilityStatus = ?1")
	Collection<FlightCrewMember> findAllCrewMembersAvailables(AvailabilityStatus status);

	@Query("select f from FlightAssignment f where f.allocatedFlightCrewMember.id= ?1")
	Collection<FlightAssignment> findFlightAssignmentByCrewMemberId(int id);

	@Query("select l from Leg l where l.id = ?1")
	Leg findLegById(int id);

	@Query("select f.leg from FlightAssignment f where f.allocatedFlightCrewMember.id = ?1")
	Collection<Leg> findLegsByFlightCrewMemberId(int memberId);

	@Query("SELECT l FROM Leg l")
	Collection<Leg> findAllLegs();

	@Query("SELECT fcm FROM FlightCrewMember fcm")
	Collection<FlightCrewMember> findAllFlightCrewMembers();

	@Query("select al from ActivityLog al where al.flightAssignment.id = ?1")
	Collection<ActivityLog> findActivityLogsByAssignmentId(int id);
}
