
package acme.features.assistanceAgent.trackingLog;

import java.util.Collection;
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
public class AssistanceAgentTrackingLogCreateService extends AbstractGuiService<AssistanceAgent, TrackingLog> {

	@Autowired
	private AssistanceAgentTrackingLogRepository repository;


	@Override
	public void authorise() {
		final int masterClaimId = super.getRequest().getData("masterId", int.class);
		final Claim claim = this.repository.findClaimById(masterClaimId);

		final Collection<TrackingLog> logs = this.repository.findAllTrackingLogsByClaimId(masterClaimId);
		final boolean allPreviousLogsPublished = logs.stream().allMatch(tl -> !tl.getDraftMode());
		final boolean anyLogAt100Percent = logs.stream().anyMatch(tl -> tl.getResolutionPercentage() == 100.0);

		if (super.getRequest().hasData("id"))
			super.getRequest().getData("status", AcceptanceStatus.class);

		final boolean isAuthorised = claim != null && !claim.getDraftMode() && super.getRequest().getPrincipal().hasRealm(claim.getAssistanceAgent()) && !anyLogAt100Percent && allPreviousLogsPublished;

		super.getResponse().setAuthorised(isAuthorised);
	}

	@Override
	public void load() {
		final int masterClaimId = super.getRequest().getData("masterId", int.class);
		final Claim claim = this.repository.findClaimById(masterClaimId);

		final TrackingLog newTrackingLog = new TrackingLog();
		newTrackingLog.setDraftMode(true);
		newTrackingLog.setCreationMoment(MomentHelper.getCurrentMoment());
		newTrackingLog.setLastUpdateMoment(MomentHelper.getCurrentMoment());
		newTrackingLog.setClaim(claim);
		newTrackingLog.setReclaimed(false);

		super.getBuffer().addData(newTrackingLog);
	}

	@Override
	public void bind(final TrackingLog trackingLog) {
		super.bindObject(trackingLog, "step", "resolutionPercentage", "status", "resolution");
	}

	@Override
	public void validate(final TrackingLog trackingLog) {
		final int claimId = trackingLog.getClaim().getId();
		final List<TrackingLog> logsByResolution = this.repository.findTrackingLogsByResolutionPercentageOrder(claimId);

		if (logsByResolution != null && !logsByResolution.isEmpty()) {
			final TrackingLog highestLog = logsByResolution.get(0);

			final Double currentResolution = trackingLog.getResolutionPercentage();
			if (currentResolution != null && currentResolution >= 0.0 && currentResolution <= 100.0) {
				final boolean isMonotonic = currentResolution > highestLog.getResolutionPercentage();
				super.state(isMonotonic, "resolutionPercentage", "acme.validation.trackingLog.resolutionPercentage-monotony.message");
			}
		}

		final boolean confirmed = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmed, "confirmation", "acme.validation.confirmation.message");
	}

	@Override
	public void perform(final TrackingLog trackingLog) {
		trackingLog.setDraftMode(true);
		trackingLog.setLastUpdateMoment(MomentHelper.getCurrentMoment());
		trackingLog.setReclaimed(false);
		this.repository.save(trackingLog);
	}

	@Override
	public void unbind(final TrackingLog trackingLog) {
		Dataset dataset;
		SelectChoices statusChoices;

		statusChoices = SelectChoices.from(AcceptanceStatus.class, trackingLog.getStatus());

		dataset = super.unbindObject(trackingLog, "lastUpdateMoment", "step", "resolutionPercentage", "status", "resolution", "draftMode", "reclaimed");
		dataset.put("masterId", super.getRequest().getData("masterId", int.class));
		dataset.put("trackStatus", statusChoices);
		dataset.put("reclaim", trackingLog.getReclaim());

		super.getResponse().addData(dataset);
	}
}
