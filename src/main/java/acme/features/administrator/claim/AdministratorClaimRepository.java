
package acme.features.administrator.claim;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.claims.Claim;
import acme.entities.trackingLogs.TrackingLog;

@Repository
public interface AdministratorClaimRepository extends AbstractRepository {

	@Query("select c from Claim c where c.draftMode = false")
	Collection<Claim> findAllPublishedClaims();

	@Query("select c from Claim c where c.id = :id")
	Claim findClaimById(int id);

	@Query("select tl from TrackingLog tl where tl.claim.id = :claimId AND tl.draftMode = false order by tl.creationMoment DESC")
	Collection<TrackingLog> findTrackingLogsByClaimId(int claimId);
}
