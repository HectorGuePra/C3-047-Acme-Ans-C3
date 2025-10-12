
package acme.features.assistanceAgent.claim;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claims.Claim;
import acme.entities.claims.ClaimType;
import acme.entities.legs.Leg;
import acme.realms.assistanceAgents.AssistanceAgent;

@GuiService
public class AssistanceAgentClaimPublishService extends AbstractGuiService<AssistanceAgent, Claim> {

	@Autowired
	private AssistanceAgentClaimRepository repository;


	@Override
	public void authorise() {
		boolean status;
		AssistanceAgent currentAgent;
		int id = super.getRequest().getData("id", int.class);
		Claim claim = this.repository.findClaimById(id);

		currentAgent = claim == null ? null : claim.getAssistanceAgent();

		status = claim != null && super.getRequest().getPrincipal().hasRealm(currentAgent) && claim.getDraftMode();

		if (status) {
			final String requestMethod = super.getRequest().getMethod();

			if (!"GET".equals(requestMethod)) {
				super.getRequest().getData("type", ClaimType.class);

				final int legId = super.getRequest().getData("leg", int.class);
				final Leg leg = super.getRequest().getData("leg", Leg.class);

				boolean hasValidDeparture = legId == 0 || this.repository.findLegsWithDepartureBeforeClaimRegistration().contains(leg);

				status = hasValidDeparture;
			}
		}

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Claim claim;
		int id;

		id = super.getRequest().getData("id", int.class);
		claim = this.repository.findClaimById(id);

		super.getBuffer().addData(claim);
	}

	@Override
	public void bind(final Claim claim) {
		super.bindObject(claim, "passengerEmail", "description", "type", "leg");
	}

	@Override
	public void validate(final Claim claim) {
		boolean confirmation;
		confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
	}

	@Override
	public void perform(final Claim claim) {
		claim.setDraftMode(false);
		this.repository.save(claim);
	}

	@Override
	public void unbind(final Claim claim) {
		Dataset dataset;
		Collection<Leg> legs;
		SelectChoices legChoices;
		SelectChoices claimTypes;

		legs = this.repository.findLegsWithDepartureBeforeClaimRegistration();
		claimTypes = SelectChoices.from(ClaimType.class, claim.getType());
		legChoices = SelectChoices.from(legs, "flightNumber", claim.getLeg());

		dataset = super.unbindObject(claim, "registrationMoment", "passengerEmail", "description", "type", "accepted", "draftMode", "leg");
		dataset.put("leg", legChoices.getSelected().getKey());
		dataset.put("legs", legChoices);
		dataset.put("types", claimTypes);

		super.getResponse().addData(dataset);
	}
}
