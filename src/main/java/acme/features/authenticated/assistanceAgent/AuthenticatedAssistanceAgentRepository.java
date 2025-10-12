
package acme.features.authenticated.assistanceAgent;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.components.principals.UserAccount;
import acme.client.repositories.AbstractRepository;
import acme.entities.airline.Airline;
import acme.realms.assistanceAgents.AssistanceAgent;

@Repository
public interface AuthenticatedAssistanceAgentRepository extends AbstractRepository {

	@Query("select a from Airline a")
	Collection<Airline> findAirlines();

	@Query("select a from Airline a where a.id = :id")
	Airline findAirlineById(int id);

	@Query("select u from UserAccount u where u.id = :id")
	UserAccount findUserAccountById(int id);

	@Query("select aa from AssistanceAgent aa")
	Collection<AssistanceAgent> findAssistanceAgents();

	@Query("select aa from AssistanceAgent aa where aa.userAccount.id = :id")
	AssistanceAgent findAssistanceAgentByUserAccountId(int id);

}
