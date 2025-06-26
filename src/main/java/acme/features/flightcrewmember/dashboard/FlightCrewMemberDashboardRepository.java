
package acme.features.flightcrewmember.dashboard;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.flightassignment.FlightAssignment;
import acme.realms.flightcrewmember.FlightCrewMember;

@Repository
public interface FlightCrewMemberDashboardRepository extends AbstractRepository {

	// Últimos 5 destinos
	@Query("""
		    select fa.leg.arrivalAirport.city
		    from FlightAssignment fa
		    where fa.allocatedFlightCrewMember.id = :memberId
		    order by fa.leg.scheduledArrival desc
		""")
	List<String> findLastFiveDestinations(int memberId, PageRequest pageable);

	// Número de legs con incidentes de severidad 0-3
	@Query("""
		    select count(distinct fa.leg.id)
		    from ActivityLog al
		    join al.flightAssignment fa
		    where fa.allocatedFlightCrewMember.id = :memberId
		      and al.severityLevel between 0 and 3
		""")
	Integer countLegsWithIncidentSeverity0to3(int memberId);

	// Número de legs con incidentes de severidad 4-7
	@Query("""
		    select count(distinct fa.leg.id)
		    from ActivityLog al
		    join al.flightAssignment fa
		    where fa.allocatedFlightCrewMember.id = :memberId
		      and al.severityLevel between 4 and 7
		""")
	Integer countLegsWithIncidentSeverity4to7(int memberId);

	// Número de legs con incidentes de severidad 8-10
	@Query("""
		    select count(distinct fa.leg.id)
		    from ActivityLog al
		    join al.flightAssignment fa
		    where fa.allocatedFlightCrewMember.id = :memberId
		      and al.severityLevel between 8 and 10
		""")
	Integer countLegsWithIncidentSeverity8to10(int memberId);

	// Miembros de tripulación en el último leg asignado
	@Query("""
		    select fa2.allocatedFlightCrewMember
		    from FlightAssignment fa1
		    join FlightAssignment fa2 on fa1.leg.id = fa2.leg.id
		    where fa1.allocatedFlightCrewMember.id = :memberId
		      and fa1.leg.scheduledArrival = (
		        select max(fa3.leg.scheduledArrival)
		        from FlightAssignment fa3
		        where fa3.allocatedFlightCrewMember.id = :memberId
		      )
		""")
	List<FlightCrewMember> findCrewMembersInLastLeg(int memberId);

	@Query("""
		    select fa
		    from FlightAssignment fa
		    where fa.allocatedFlightCrewMember.id = :memberId
		      and fa.currentStatus = acme.entities.flightassignment.AssigmentStatus.CONFIRMED
		""")
	List<FlightAssignment> findAssignmentsConfirmed(int memberId);

	// Asignaciones pendientes
	@Query("""
		    select fa
		    from FlightAssignment fa
		    where fa.allocatedFlightCrewMember.id = :memberId
		      and fa.currentStatus = acme.entities.flightassignment.AssigmentStatus.PENDING
		""")
	List<FlightAssignment> findAssignmentsPending(int memberId);

	// Asignaciones canceladas
	@Query("""
		    select fa
		    from FlightAssignment fa
		    where fa.allocatedFlightCrewMember.id = :memberId
		      and fa.currentStatus = acme.entities.flightassignment.AssigmentStatus.CANCELLED
		""")
	List<FlightAssignment> findAssignmentsCancelled(int memberId);

	@Query("""
		    select count(fa)
		    from FlightAssignment fa
		    where fa.allocatedFlightCrewMember.id = :memberId
		      and fa.leg.scheduledDeparture >= :startDate
		      and fa.leg.scheduledDeparture < :endDate
		""")
	Integer countAssignmentsLastMonth(int memberId, Date startDate, Date endDate);

}
