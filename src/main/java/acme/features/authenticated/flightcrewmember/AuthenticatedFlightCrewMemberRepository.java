
package acme.features.authenticated.flightcrewmember;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.components.principals.UserAccount;
import acme.client.repositories.AbstractRepository;
import acme.entities.airline.Airline;
import acme.realms.flightcrewmember.FlightCrewMember;

@Repository
public interface AuthenticatedFlightCrewMemberRepository extends AbstractRepository {

	@Query("select a from Airline a")
	Collection<Airline> findAllAirlines();

	@Query("select a from Airline a where a.id = :id")
	Airline findAirlineById(int id);

	@Query("select u from UserAccount u where u.id = :id")
	UserAccount findUserAccountById(int id);

	@Query("select a from FlightCrewMember a where a.id = :id")
	FlightCrewMember findMemberById(int id);

	@Query("select a from FlightCrewMember a")
	Collection<FlightCrewMember> findAllMembers();

	@Query("select a from FlightCrewMember a where a.userAccount.id = :id")
	FlightCrewMember findFlightCrewMemberByUserAccountId(int id);

}
