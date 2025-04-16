
package acme.entities.flight;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.components.datatypes.Money;
import acme.client.repositories.AbstractRepository;
import acme.entities.legs.Leg;

@Repository
public interface FlightRepository extends AbstractRepository {

	@Query("select l from Leg l where l.flight.id = :flightId")
	List<Leg> legsDuringFlight(int flightId);

	@Query("select f.cost from Flight f where f.id = :flightId")
	Money findCostByFlight(int flightId);
}
