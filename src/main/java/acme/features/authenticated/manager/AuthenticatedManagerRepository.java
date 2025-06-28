
package acme.features.authenticated.manager;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.components.principals.UserAccount;
import acme.client.repositories.AbstractRepository;
import acme.entities.airline.Airline;
import acme.realms.manager.Manager;

@Repository
public interface AuthenticatedManagerRepository extends AbstractRepository {

	@Query("select a from Airline a")
	Collection<Airline> findAirlines();

	@Query("select u from UserAccount u where u.id = :id")
	UserAccount findUserAccountById(int id);

	@Query("select a from Manager a where a.id = :id")
	Manager findManagerById(int id);

	@Query("select a from Manager a")
	Collection<Manager> findManagers();

	@Query("select a from Manager a where a.userAccount.id = :id")
	Manager findManagerByUserAccountId(int id);

}
