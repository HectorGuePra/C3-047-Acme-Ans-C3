
package acme.features.assistanceAgent.claim;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claims.Claim;
import acme.entities.trackingLogs.AcceptanceStatus;
import acme.realms.assistanceAgents.AssistanceAgent;

@GuiService
public class AssistanceAgentUndergoingPublicClaimListService extends AbstractGuiService<AssistanceAgent, Claim> {

	@Autowired
	private AssistanceAgentClaimRepository repository;


	@Override
	public void authorise() {
		boolean status;

		status = super.getRequest().getPrincipal().hasRealmOfType(AssistanceAgent.class);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		final int accountId = super.getRequest().getPrincipal().getAccountId();
		final AssistanceAgent agent = this.repository.findAssistanceAgentByUserAccountId(accountId);

		final Collection<Claim> publicClaims = this.repository.findAllPublicClaimsByCurrentUser(agent.getId());
		final Collection<Claim> pendingClaims = publicClaims.stream().filter(claim -> claim.getAccepted().equals(AcceptanceStatus.PENDING)).toList();

		super.getBuffer().addData(pendingClaims);
	}

	@Override
	public void unbind(final Claim claim) {
		Dataset dataset;

		dataset = super.unbindObject(claim, "registrationMoment", "type", "accepted");

		super.addPayload(dataset, claim, "passengerEmail", "description", "draftMode", "assistanceAgent.employeeCode", "leg.flightNumber");
		super.getResponse().addData(dataset);
	}
}
