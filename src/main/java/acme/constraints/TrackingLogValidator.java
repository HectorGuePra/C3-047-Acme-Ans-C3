
package acme.constraints;

import java.util.List;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.MomentHelper;
import acme.entities.trackingLogs.AcceptanceStatus;
import acme.entities.trackingLogs.TrackingLog;
import acme.entities.trackingLogs.TrackingLogRepository;

@Validator
public class TrackingLogValidator extends AbstractValidator<ValidTrackingLog, TrackingLog> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private TrackingLogRepository repository;

	// ConstraintValidator interface ------------------------------------------


	@Override
	protected void initialise(final ValidTrackingLog annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final TrackingLog trackingLog, final ConstraintValidatorContext context) {
		assert context != null;

		boolean result;
		if (trackingLog == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else {
			AcceptanceStatus indicator = trackingLog.getStatus();

			Double resolutionPercentage = trackingLog.getResolutionPercentage();
			boolean correctResolutionPercentage;

			String resolution = trackingLog.getResolution();
			boolean correctResolution;

			if (indicator != AcceptanceStatus.PENDING) {
				if (indicator == AcceptanceStatus.ACCEPTED || indicator == AcceptanceStatus.REJECTED) {
					{
						correctResolutionPercentage = resolutionPercentage != null && resolutionPercentage >= 0. && resolutionPercentage <= 100. ? resolutionPercentage == 100. : true;
						super.state(context, correctResolutionPercentage, "resolutionPercentage", "acme.validation.trackingLog.resolutionPercentage-mustBe100.message");
					}

					{
						correctResolution = resolution != null;
						super.state(context, correctResolution, "resolution", "acme.validation.trackingLog.resolution-notNull.message");
						correctResolution = !resolution.trim().equals("");
						super.state(context, correctResolution, "resolution", "acme.validation.trackingLog.resolution-notBlanck.message");
					}
				}
			} else {
				correctResolutionPercentage = resolutionPercentage != null && resolutionPercentage <= 100. ? resolutionPercentage < 100. : true;
				super.state(context, correctResolutionPercentage, "resolutionPercentage", "acme.validation.trackingLog.resolutionPercentage-cannotBe100.message");
			}

			{
				boolean claimAssociatedIsPublic;
				claimAssociatedIsPublic = !trackingLog.getClaim().getDraftMode();
				super.state(context, claimAssociatedIsPublic, "claim", "acme.validation.trackingLog.claimMustBePublished.message");
			}

			{
				boolean creationBeforeOrEqualUpdate;
				creationBeforeOrEqualUpdate = !MomentHelper.isAfter(trackingLog.getCreationMoment(), trackingLog.getLastUpdateMoment());
				super.state(context, creationBeforeOrEqualUpdate, "lastUpdateMoment", "acme.validation.trackingLog.creationBeforeOrEqualUpdateMoment.message");
			}

			{
				boolean correctMonotony;
				int claimId = trackingLog.getClaim().getId();
				List<TrackingLog> trackingLogsOrdered = this.repository.getTrackingLogsBycreationMomentOrderAsc(claimId);

				if (trackingLogsOrdered.contains(trackingLog) && !trackingLog.getReclaimed() && trackingLog.getResolutionPercentage() != null && trackingLog.getResolutionPercentage() <= 100. && trackingLog.getResolutionPercentage() >= 0.) {
					TrackingLog trackingLogActual = trackingLogsOrdered.stream().filter(tl -> tl.getId() == trackingLog.getId()).findFirst().get();

					int currentIndex = trackingLogsOrdered.indexOf(trackingLogActual);

					if (currentIndex == 0 && trackingLogsOrdered.size() > 1) {
						TrackingLog nextTrackingLog = trackingLogsOrdered.get(currentIndex + 1);
						if (!nextTrackingLog.getReclaimed()) {
							correctMonotony = nextTrackingLog.getResolutionPercentage() > trackingLog.getResolutionPercentage();
							super.state(context, correctMonotony, "resolutionPercentage", "acme.validation.trackingLog.monotony.message");
						}
					}

					if (currentIndex > 0 && currentIndex < trackingLogsOrdered.size() - 1) {
						TrackingLog previousTrackingLog = trackingLogsOrdered.get(currentIndex - 1);
						TrackingLog nextTrackingLog = trackingLogsOrdered.get(currentIndex + 1);
						if (!nextTrackingLog.getReclaimed()) {
							correctMonotony = trackingLog.getResolutionPercentage() > previousTrackingLog.getResolutionPercentage() && nextTrackingLog.getResolutionPercentage() > trackingLog.getResolutionPercentage();
							super.state(context, correctMonotony, "resolutionPercentage", "acme.validation.trackingLog.monotony.message");
						}
					}

					if (currentIndex == trackingLogsOrdered.size() - 1 && currentIndex > 0) {
						TrackingLog previousTrackingLog = trackingLogsOrdered.get(currentIndex - 1);
						correctMonotony = trackingLog.getResolutionPercentage() > previousTrackingLog.getResolutionPercentage();
						super.state(context, correctMonotony, "resolutionPercentage", "acme.validation.trackingLog.monotony.message");
					}
				}
			}

			{
				boolean claimBeforeTlUpdate;
				boolean claimCreationBeforeTrackingLogCreation;

				claimBeforeTlUpdate = !MomentHelper.isAfter(trackingLog.getClaim().getRegistrationMoment(), trackingLog.getLastUpdateMoment());
				claimCreationBeforeTrackingLogCreation = !MomentHelper.isAfter(trackingLog.getClaim().getRegistrationMoment(), trackingLog.getLastUpdateMoment());
				if (!claimCreationBeforeTrackingLogCreation || !claimBeforeTlUpdate) {
					super.state(context, claimCreationBeforeTrackingLogCreation, "creationMoment", "acme.validation.trackingLog.claimDateBeforeTrackingLogDates.message");
					super.state(context, claimBeforeTlUpdate, "lastUpdateMoment", "acme.validation.trackingLog.claimDateBeforeTrackingLogDates.message");
				}
			}

			{
				boolean prevTlPubToCreate;
				int claimAssociatedId;
				List<TrackingLog> tlClaimOrdered;
				int actualTrackingLogIndex;
				List<TrackingLog> prevTls;

				claimAssociatedId = trackingLog.getClaim().getId();
				tlClaimOrdered = this.repository.getTrackingLogsBycreationMomentOrderAsc(claimAssociatedId);

				actualTrackingLogIndex = tlClaimOrdered.indexOf(trackingLog);

				if (actualTrackingLogIndex != -1) {
					prevTls = tlClaimOrdered.subList(0, actualTrackingLogIndex);

					prevTlPubToCreate = prevTls.stream().allMatch(tl -> tl.getDraftMode() == false);
					super.state(context, prevTlPubToCreate, "draftMode", "acme.validation.trackingLog.previousTlMustBePublished.message");
				}
			}
		}

		result = !super.hasErrors(context);

		return result;
	}
}
