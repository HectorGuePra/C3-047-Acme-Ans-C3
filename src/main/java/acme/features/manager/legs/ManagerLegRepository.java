
package acme.features.manager.legs;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.activityLog.ActivityLog;
import acme.entities.aircraft.Aircraft;
import acme.entities.airport.Airport;
import acme.entities.claim.Claim;
import acme.entities.flight.Flight;
import acme.entities.flightassignment.FlightAssignment;
import acme.entities.legs.Leg;
import acme.entities.trackinglog.TrackingLog;

@Repository
public interface ManagerLegRepository extends AbstractRepository {

	@Query("select l from Leg l where l.id = :id ")
	public Leg findLegByLegId(int id);

	@Query("select a from Aircraft a")
	public List<Aircraft> findAllAircrafts();

	@Query("select l from Leg l where l.flight.manager.id = :id ")
	public List<Leg> findManagerLegsByManagerId(int id);

	@Query("SELECT l FROM Leg l WHERE l.aircraft.airline.id IN (SELECT m.airline.id FROM Manager m WHERE m.id = :id)")
	public List<Leg> findManagerLegsByManager(int id);

	@Query("select l from Leg l where l.flight.manager.id = :id order by l.scheduledDeparture ")
	public List<Leg> findManagerLegsByManagerIOrderedByMoment(int id);

	@Query("select f from Flight f where f.id = :id")
	public Flight findFlightByFlightId(int id);

	@Query("select a from Aircraft a where a.id = :id")
	public Aircraft findAircraftByAircraftId(int id);

	@Query("select a from Airport a where a.id = :id")
	public Airport findAirportByAirportId(int id);

	@Query("select f from FlightAssignment f where f.leg.id = :id")
	public List<FlightAssignment> findFlightAssignmentsByLegId(int id);

	@Query("select c from Claim c where c.leg.id = :id")
	public List<Claim> findClaimsByLegId(int id);

	@Query("select a from ActivityLog a where a.flightAssignment.id = :id")
	public List<ActivityLog> findActivityLogsByFlightAssignmentId(int id);

	@Query("select t from TrackingLog t where t.claim.id = :id")
	public List<TrackingLog> findTrackingLogByClaimId(int id);

	@Query("select a from Airport a")
	public List<Airport> findAllAirports();

	@Query("select distinct l.aircraft from Leg l where l.flight.manager.id = :id")
	public List<Aircraft> findAllAircraftsByManagerId(int id);

	@Query("select a from Aircraft a where a.airline.id = (select m.airline.id from Manager m where m.id = :id)")
	public List<Aircraft> findAllAircraftsByManager(int id);

	@Query("select l from Leg l where l.flight.id = :id order by l.scheduledDeparture")
	public List<Leg> findAllLegsByFlightId(int id);

}
