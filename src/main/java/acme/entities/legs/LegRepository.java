
package acme.entities.legs;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface LegRepository extends AbstractRepository {

	@Query("SELECT l FROM Leg l")
	List<Leg> findAllLegs();

	@Query("""
		SELECT
			CASE
				WHEN COUNT(l) > 0 THEN true
				ELSE false
			END
		FROM Leg l
		WHERE
		l.id != :legId AND
		l.aircraft.airline.id = :airlineId AND
		l.flightNumberDigits = :flightNumberDigits
		""")
	public boolean isFlightNumberUsed(Integer legId, Integer airlineId, String flightNumberDigits);

	@Query("select case when count(l) > 0 then true else false end from Leg l where l.id != :legId and l.flight.id = :flightId and (l.scheduledDeparture <= :scheduledArrival and l.scheduledArrival >= :scheduledDeparture)")
	public boolean isLegOverlapping(Integer legId, Integer flightId, Date scheduledDeparture, Date scheduledArrival);

}
