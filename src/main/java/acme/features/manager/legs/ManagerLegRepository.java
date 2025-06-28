
package acme.features.manager.legs;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.aircraft.Aircraft;
import acme.entities.airport.Airport;
import acme.entities.flight.Flight;
import acme.entities.legs.Leg;

@Repository
public interface ManagerLegRepository extends AbstractRepository {

	@Query("select l from Leg l where l.id = :id ")
	public Leg findLegByLegId(int id);

	@Query("select l from Leg l where l.id = :id ")
	public Optional<Leg> findByLegId(int id);

	@Query("select a from Aircraft a")
	public List<Aircraft> findAllAircrafts();

	@Query("select f from Flight f where f.id = :id")
	public Flight findFlightByFlightId(int id);

	@Query("select a from Aircraft a where a.id = :id")
	public Aircraft findAircraftByAircraftId(int id);

	@Query("select a from Airport a where a.id = :id")
	public Airport findAirportByAirportId(int id);

	@Query("select a from Airport a")
	public List<Airport> findAllAirports();

	@Query("select distinct l.aircraft from Leg l where l.flight.manager.id = :id")
	public List<Aircraft> findAllAircraftsByManagerId(int id);

	@Query("select a from Aircraft a where a.airline.id = (select m.airline.id from Manager m where m.id = :id)")
	public List<Aircraft> findAllAircraftsByManager(int id);

	@Query("select l from Leg l where l.flight.id = :id order by l.scheduledDeparture")
	public List<Leg> findAllLegsByFlightId(int id);

	Optional<Flight> findByIdAndManagerId(Integer flightId, Integer managerId);

	@Query("select l from Leg l where l.aircraft.airline.id = :id")
	Collection<Leg> findLegsByAirlineId(int id);

}
