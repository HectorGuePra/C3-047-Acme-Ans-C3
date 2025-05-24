package acme.features.technician.maintenanceRecord;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.constraints.ValidCurrencies;
import acme.entities.aircraft.Aircraft;
import acme.entities.maintenancerecord.MaintenanceRecord;
import acme.entities.maintenancerecord.MaintenanceRecordStatus;
import acme.realms.technician.Technician;

@GuiService
public class TechnicianMaintenanceRecordCreateService extends AbstractGuiService<Technician, MaintenanceRecord> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private TechnicianMaintenanceRecordRepository repository;


	// AbstractGuiService interface -------------------------------------------
	@Override
	public void authorise() {
		Boolean authorised = true;
		
		if (super.getRequest().getMethod().equals("POST")) {
			if (super.getRequest().hasData("aircraft")) {
				Integer aircraftId = super.getRequest().getData("aircraft", int.class);
				authorised = aircraftId == 0 || this.repository.findAircraftByAircraftId(aircraftId) != null;
			} 
			if (authorised && super.getRequest().hasData("status")) {
				String status = super.getRequest().getData("status", String.class);
				authorised = status.equals("0") || status.equals("PENDING") || status.equals("IN_PROGRESS") || 
						status.equals("COMPLETED");
			}
		}
		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		MaintenanceRecord maintenanceRecord;
		Date moment = MomentHelper.getCurrentMoment();
		Technician technician = (Technician) super.getRequest().getPrincipal().getActiveRealm();
		Boolean draftMode = true;

		maintenanceRecord = new MaintenanceRecord();
		maintenanceRecord.setTechnician(technician);
		maintenanceRecord.setMaintenanceMoment(moment);
		maintenanceRecord.setDraftMode(draftMode);
		super.getBuffer().addData(maintenanceRecord);
	}

	@Override
	public void bind(final MaintenanceRecord maintenanceRecord) {
		super.bindObject(maintenanceRecord, "status", "nextInspectionDate", "estimatedCost", "notes", "aircraft");
	}

	@Override
	public void validate(final MaintenanceRecord maintenanceRecord) {

		if (!this.getBuffer().getErrors().hasErrors("status"))
			super.state(maintenanceRecord.getStatus() != null, "status", "acme.validation.technician.maintenance-record.noStatus.message", maintenanceRecord);

		if (!this.getBuffer().getErrors().hasErrors("nextInspectionDate") && maintenanceRecord.getNextInspectionDate() != null)
			super.state(maintenanceRecord.getNextInspectionDate().compareTo(maintenanceRecord.getMaintenanceMoment()) > 0, "nextInspectionDate", "acme.validation.technician.maintenance-record.nextInspectionDate.message", maintenanceRecord);

		if (!this.getBuffer().getErrors().hasErrors("estimatedCost") && maintenanceRecord.getEstimatedCost() != null)
			super.state(0.00 <= maintenanceRecord.getEstimatedCost().getAmount() && maintenanceRecord.getEstimatedCost().getAmount() <= 1000000.00, "estimatedCost", "acme.validation.technician.maintenance-record.estimatedCost.message", maintenanceRecord);

		if (!this.getBuffer().getErrors().hasErrors("estimatedCost") && maintenanceRecord.getEstimatedCost() != null)
			super.state(ValidCurrencies.isValidCurrency(maintenanceRecord.getEstimatedCost().getCurrency()), "estimatedCost", "acme.validation.technician.maintenance-record.estimatedCost.currency.message", maintenanceRecord);
		
		if (!this.getBuffer().getErrors().hasErrors("notes") && maintenanceRecord.getNotes() != null)
			super.state(maintenanceRecord.getNotes().length() <= 255, "notes", "acme.validation.technician.maintenance-record.notes.message", maintenanceRecord);

		if (!this.getBuffer().getErrors().hasErrors("aircraft") && maintenanceRecord.getAircraft() != null)
			super.state(this.repository.findAllAircrafts().contains(maintenanceRecord.getAircraft()), "aircraft", "acme.validation.technician.maintenance-record.aircraft.message", maintenanceRecord);
	}

	@Override
	public void perform(final MaintenanceRecord maintenanceRecord) {
		assert maintenanceRecord != null;

		this.repository.save(maintenanceRecord);
	}

	@Override
	public void unbind(final MaintenanceRecord maintenanceRecord) {
		SelectChoices choices;
		Collection<Aircraft> aircrafts;
		SelectChoices choicesAircraft;

		Dataset dataset;
		aircrafts = this.repository.findAllAircrafts();
		choices = SelectChoices.from(MaintenanceRecordStatus.class, maintenanceRecord.getStatus());
		choicesAircraft = SelectChoices.from(aircrafts, "regNumber", maintenanceRecord.getAircraft());

		dataset = super.unbindObject(maintenanceRecord, "status", "nextInspectionDate", "estimatedCost", "notes", "aircraft");

		dataset.put("statuses", choices);
		dataset.put("aircrafts", choicesAircraft);

		super.getResponse().addData(dataset);
	}

}