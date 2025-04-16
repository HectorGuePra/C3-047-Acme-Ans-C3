
package acme.entities.legs;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface LegRepository extends AbstractRepository {

	@Query("SELECT l FROM Leg l")
	List<Leg> findAllLegs();

	@Query("select l from Leg l where l.flightNumber = :flightNumber")
	public Leg getLegFromFlightNumber(String flightNumber);
}
