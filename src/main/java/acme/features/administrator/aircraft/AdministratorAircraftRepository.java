package acme.features.administrator.aircraft;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.aircraft.Aircraft;
import acme.entities.airline.Airline;

@Repository
public interface AdministratorAircraftRepository extends AbstractRepository {
	

	@Query("SELECT a FROM Aircraft a WHERE a.id = :id")
	Aircraft findById(int id);
	
	@Query("select a from Aircraft a")
	Collection<Aircraft> findAllAircrafts();

	@Query("SELECT a FROM Aircraft a WHERE a.airline.id = :airlineId ")
	Collection<Aircraft> findAllByAirlineId(final int airlineId);
	
	@Query("select a from Airline a")
	Collection<Airline> findAllAirlines();

	@Query("select a from Airline a where a.id = :id")
	Airline findAirlineById(int id);

}
