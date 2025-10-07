
package acme.features.assistanceAgent.claim;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.claims.Claim;
import acme.entities.legs.Leg;
import acme.entities.trackingLogs.TrackingLog;
import acme.realms.assistanceAgents.AssistanceAgent;

@Repository
public interface AssistanceAgentClaimRepository extends AbstractRepository {

	@Query("select c from Claim c")
	Collection<Claim> findAllClaims();

	@Query("select c from Claim c where c.id = :id")
	Claim findClaimById(int id);

	@Query("select c from Claim c where c.assistanceAgent.id = :agentId")
	Collection<Claim> findAllClaimsByCurrentUser(int agentId);

	@Query("select c from Claim c where c.assistanceAgent.id = :agentId and c.draftMode = true")
	Collection<Claim> findAllUnpublishedClaimsByCurrentUser(int agentId);

	@Query("select c from Claim c where c.assistanceAgent.id = :agentId and c.draftMode = false")
	Collection<Claim> findAllPublicClaimsByCurrentUser(int agentId);

	@Query("select a from AssistanceAgent a where a.userAccount.id = :id")
	AssistanceAgent findAssistanceAgentByUserAccountId(int id);

	@Query("select a from AssistanceAgent a")
	Collection<AssistanceAgent> findAllAssistanceAgents();

	@Query("SELECT DISTINCT l FROM Claim c JOIN c.leg l WHERE l.scheduledArrival <= c.registrationMoment AND l.draftMode = false")
	Collection<Leg> findLegsWithDepartureBeforeClaimRegistration();

	@Query("select t from TrackingLog t where t.claim.id = :claimId")
	Collection<TrackingLog> findTrackingLogsByClaimId(int claimId);

	@Query("select l from Leg l")
	Collection<Leg> findAllLegs();

}
