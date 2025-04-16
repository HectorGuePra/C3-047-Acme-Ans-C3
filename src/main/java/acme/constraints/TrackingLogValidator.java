
package acme.constraints;

import java.util.List;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.helpers.SpringHelper;
import acme.entities.trackinglog.TrackingLog;
import acme.entities.trackinglog.TrackingLogRepository;
import acme.entities.trackinglog.TrackingLogStatus;

public class TrackingLogValidator extends AbstractValidator<ValidTrackingLog, TrackingLog> {

	private TrackingLogRepository trackingLogRepository;


	public TrackingLogValidator() {
		this.trackingLogRepository = SpringHelper.getBean(TrackingLogRepository.class);
	}

	@Override
	public void initialise(final ValidTrackingLog annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final TrackingLog trackingLog, final ConstraintValidatorContext context) {
		assert context != null;

		// The intermediate tracking logs can keep in state “PENDING”,
		// whereas the last one (when the resolution percentage is 100%),
		// must set state to either “ACCEPTED” or “REJECTED”.
		// The status can be “ACCEPTED” or “REJECTED” only when the resolution percentage gets to 100%.
		boolean validStatus = true;

		if (trackingLog.getResolutionPercentage() == 100.00)
			validStatus &= trackingLog.getStatus() == TrackingLogStatus.ACCEPTED || trackingLog.getStatus() == TrackingLogStatus.REJECTED;
		else
			validStatus &= trackingLog.getStatus() == TrackingLogStatus.PENDING;

		// The resolution percentage must be monotonically increasing.
		List<TrackingLog> trackingLogs = this.trackingLogRepository.findByClaimIdOrderByLastUpdateMomentAsc(trackingLog.getClaim().getId());

		for (int i = 1; i < trackingLogs.size(); i++)
			if (trackingLogs.get(i).getResolutionPercentage() < trackingLogs.get(i - 1).getResolutionPercentage()) {
				validStatus &= false;
				break;
			}

		// If the status is not “PENDING”, then the resolution is mandatory; otherwise, it’s optional.
		if (trackingLog.getStatus() != TrackingLogStatus.PENDING)
			validStatus &= trackingLog.getResolution() != null;

		super.state(context, validStatus, "status", "acme.validation.trackinglog.status.message");

		return !super.hasErrors(context);
	}
}
