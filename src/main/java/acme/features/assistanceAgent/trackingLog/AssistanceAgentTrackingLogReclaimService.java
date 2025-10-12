
package acme.features.assistanceAgent.trackingLog;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claims.Claim;
import acme.entities.trackingLogs.AcceptanceStatus;
import acme.entities.trackingLogs.TrackingLog;
import acme.realms.assistanceAgents.AssistanceAgent;

@GuiService
public class AssistanceAgentTrackingLogReclaimService extends AbstractGuiService<AssistanceAgent, TrackingLog> {

	@Autowired
	private AssistanceAgentTrackingLogRepository repository;


	@Override
	public void authorise() {
		final int masterClaimId = super.getRequest().getData("masterId", int.class);
		final List<TrackingLog> logsByResolution = this.repository.findTrackingLogsByResolutionPercentageOrder(masterClaimId);
		final TrackingLog latestLog = !logsByResolution.isEmpty() ? logsByResolution.get(0) : null;

		final AssistanceAgent agent = latestLog != null ? latestLog.getClaim().getAssistanceAgent() : null;

		final boolean isAuthorised = latestLog != null && super.getRequest().getPrincipal().hasRealm(agent) && latestLog.getReclaim() && !latestLog.getDraftMode();

		super.getResponse().setAuthorised(isAuthorised);
	}

	@Override
	public void load() {
		final int masterClaimId = super.getRequest().getData("masterId", int.class);
		final List<TrackingLog> logsByResolution = this.repository.findTrackingLogsByResolutionPercentageOrder(masterClaimId);
		final TrackingLog latestLog = logsByResolution.get(0);

		final Claim associatedClaim = latestLog.getClaim();
		final AcceptanceStatus associatedStatus = latestLog.getStatus();

		final TrackingLog newTrackingLog = new TrackingLog();
		newTrackingLog.setCreationMoment(MomentHelper.getCurrentMoment());
		newTrackingLog.setLastUpdateMoment(MomentHelper.getCurrentMoment());
		newTrackingLog.setDraftMode(true);
		newTrackingLog.setStatus(associatedStatus);
		newTrackingLog.setResolutionPercentage(100.0);
		newTrackingLog.setClaim(associatedClaim);
		newTrackingLog.setReclaimed(true);

		super.getBuffer().addData(newTrackingLog);
	}

	@Override
	public void bind(final TrackingLog trackingLog) {
		super.bindObject(trackingLog, "step", "resolution");
	}

	@Override
	public void validate(final TrackingLog trackingLog) {
		boolean confirmation;
		confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
	}

	@Override
	public void perform(final TrackingLog trackingLog) {
		trackingLog.setDraftMode(true);
		trackingLog.setLastUpdateMoment(MomentHelper.getCurrentMoment());
		trackingLog.setReclaimed(true);
		this.repository.save(trackingLog);
	}

	@Override
	public void unbind(final TrackingLog trackingLog) {
		Dataset dataset;
		SelectChoices statusChoices;

		statusChoices = SelectChoices.from(AcceptanceStatus.class, trackingLog.getStatus());

		dataset = super.unbindObject(trackingLog, "lastUpdateMoment", "step", "resolutionPercentage", "status", "resolution", "draftMode", "reclaimed", "claim");
		dataset.put("masterId", super.getRequest().getData("masterId", int.class));
		dataset.put("trackStatus", statusChoices);
		dataset.put("reclaim", trackingLog.getReclaim());

		super.getResponse().addData(dataset);
	}
}
