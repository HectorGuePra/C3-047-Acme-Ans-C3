
package acme.features.manager.flights;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.flight.Flight;
import acme.entities.legs.Leg;

@Repository
public interface ManagerFlightRepository extends AbstractRepository {

	@Query("select f from Flight f")
	public List<Flight> findAllFlights();

	@Query("select f from Flight f where f.manager.id = :id")
	public List<Flight> findManagerFlightsByManagerId(int id);

	@Query("select f from Flight f where f.id = :id")
	public Flight findFlightById(int id);

	@Query("select l from Leg l where l.flight.id = :id")
	public List<Leg> findLegsByFlightId(int id);

	@Query("select sc.currency from SystemCurrencies sc")
	List<String> findAllCurrencies();

}
