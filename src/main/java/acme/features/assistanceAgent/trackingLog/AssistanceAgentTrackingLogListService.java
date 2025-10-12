
package acme.features.assistanceAgent.trackingLog;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claims.Claim;
import acme.entities.trackingLogs.TrackingLog;
import acme.realms.assistanceAgents.AssistanceAgent;

@GuiService
public class AssistanceAgentTrackingLogListService extends AbstractGuiService<AssistanceAgent, TrackingLog> {

	@Autowired
	private AssistanceAgentTrackingLogRepository repository;


	@Override
	public void authorise() {
		final int masterClaimId = super.getRequest().getData("masterId", int.class);
		final Claim claim = this.repository.findClaimById(masterClaimId);
		final AssistanceAgent agent = claim != null ? claim.getAssistanceAgent() : null;

		final boolean isAuthorised = claim != null && super.getRequest().getPrincipal().hasRealm(agent) && !claim.getDraftMode();

		super.getResponse().setAuthorised(isAuthorised);
	}

	@Override
	public void load() {
		Collection<TrackingLog> trackingLogs;
		int claimId;

		claimId = super.getRequest().getData("masterId", int.class);

		trackingLogs = this.repository.findAllTrackingLogsByClaimId(claimId);

		super.getBuffer().addData(trackingLogs);
	}

	@Override
	public void unbind(final TrackingLog trackingLog) {
		Dataset dataset;

		dataset = super.unbindObject(trackingLog, "lastUpdateMoment", "step", "resolutionPercentage", "status");

		super.addPayload(dataset, trackingLog, "resolution", "draftMode", "claim.passengerEmail", "reclaimed");
		super.getResponse().addData(dataset);
	}

	@Override
	public void unbind(final Collection<TrackingLog> trackingLogsInput) {
		final int masterClaimId = super.getRequest().getData("masterId", int.class);
		final Collection<TrackingLog> allLogs = this.repository.findAllTrackingLogsByClaimId(masterClaimId);

		final boolean noLogAt100Percent = allLogs.stream().noneMatch(tl -> tl.getResolutionPercentage() == 100.0);
		final boolean allPreviousLogsPublished = allLogs.stream().allMatch(tl -> !tl.getDraftMode());

		final boolean canCreate = noLogAt100Percent && allPreviousLogsPublished;
		final boolean canReclaim = allLogs.stream().anyMatch(TrackingLog::getReclaim);

		super.getResponse().addGlobal("masterId", masterClaimId);
		super.getResponse().addGlobal("couldCreate", canCreate);
		super.getResponse().addGlobal("couldReclaim", canReclaim);
	}

}
