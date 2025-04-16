
package acme.features.flightcrewmember.activitylog;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import acme.client.repositories.AbstractRepository;
import acme.entities.activityLog.ActivityLog;
import acme.entities.flightassignment.FlightAssignment;

public interface FlightCrewMemberActivityLogRepository extends AbstractRepository {

	@Query("select l from ActivityLog l where l.flightAssignment.id = ?1")
	List<ActivityLog> findLogsByFlightAssignment(int id);

	@Query("select a from FlightAssignment a where a.id = ?1")
	FlightAssignment findFlightAssignmentById(int id);

	@Query("select l from ActivityLog l where l.id = ?1")
	ActivityLog findActivityLogById(int id);

	@Query("select a from FlightAssignment a")
	List<FlightAssignment> findAllFlightAssignments();

	@Query("select a from FlightAssignment a where a.allocatedFlightCrewMember.id = ?1")
	List<FlightAssignment> findAllFlightAssignmentsByFlightCrewMemberId(int id);
}
