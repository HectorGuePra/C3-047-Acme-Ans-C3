
package acme.entities.airport;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface AirportRepository extends AbstractRepository {

	@Query("SELECT a FROM Airport a")
	List<Airport> findAllAirports();
}
