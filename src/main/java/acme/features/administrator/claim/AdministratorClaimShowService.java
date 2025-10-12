
package acme.features.administrator.claim;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claims.Claim;
import acme.entities.trackingLogs.TrackingLog;

@GuiService
public class AdministratorClaimShowService extends AbstractGuiService<Administrator, Claim> {

	@Autowired
	private AdministratorClaimRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int claimId;
		Claim claim;

		claimId = super.getRequest().getData("id", int.class);
		claim = this.repository.findClaimById(claimId);

		status = super.getRequest().getPrincipal().hasRealmOfType(Administrator.class) && claim != null && !claim.getDraftMode();
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		Claim object = this.repository.findClaimById(id);
		super.getBuffer().addData(object);
	}

	@Override
	public void unbind(final Claim object) {
		assert object != null;

		Dataset dataset;
		Collection<TrackingLog> trackingLogs;

		trackingLogs = this.repository.findTrackingLogsByClaimId(object.getId());

		dataset = super.unbindObject(object, "registrationMoment", "passengerEmail", "description", "type", "accepted", "draftMode");

		dataset.put("trackingLogs", trackingLogs);

		if (object.getLeg() != null)
			dataset.put("legFlightNumber", object.getLeg().getFlightNumber());
		else
			dataset.put("legFlightNumber", "N/A");

		super.getResponse().addData(dataset);
	}
}
