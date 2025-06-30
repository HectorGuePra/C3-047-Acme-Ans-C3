
package acme.features.any.leg;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.aircraft.Aircraft;
import acme.entities.airport.Airport;
import acme.entities.flight.Flight;
import acme.entities.legs.Leg;

@Repository
public interface AnyLegRepository extends AbstractRepository {

	@Query("select l from Leg l where l.id = :id")
	Leg findLegById(int id);

	@Query("select f from Flight f")
	List<Flight> findAllFlights();

	@Query("select f from Flight f where f.manager.id = :id")
	List<Flight> findManagerFlightsByManagerId(int id);

	@Query("select a from Aircraft a")
	List<Aircraft> findAllAircrafts();

	@Query("select distinct l.aircraft from Leg l where l.flight.manager.id = :id")
	List<Aircraft> findAllAircraftsByManagerId(int id);

	@Query("SELECT a FROM Airport a")
	List<Airport> findAllAirports();

	@Query("select l from Leg l where l.flight.id = :id")
	Collection<Leg> findLegsByFlightId(int id);

	@Query("select f from Flight f where f.id = :id")
	Flight findFlightById(int id);

	@Query("select a from Aircraft a where a.airline.id = :id and a.status = 'IN_SERVICE'")
	Collection<Aircraft> findActiveAircraftsByAirlineId(int id);

}
