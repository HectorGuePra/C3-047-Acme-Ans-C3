package acme.features.technician.maintenanceRecord;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.constraints.ValidCurrencies;
import acme.entities.aircraft.Aircraft;
import acme.entities.maintenancerecord.MaintenanceRecord;
import acme.entities.maintenancerecord.MaintenanceRecordStatus;
import acme.realms.technician.Technician;

@GuiService
public class TechnicianMaintenanceRecordUpdateService extends AbstractGuiService<Technician, MaintenanceRecord> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private TechnicianMaintenanceRecordRepository repository;


	// AbstractGuiService interface -------------------------------------------
	@Override
	public void authorise() {
		boolean exist;
		Boolean authorised = true;
		MaintenanceRecord maintenanceRecord;
		Technician technician;
		int id;

		id = super.getRequest().getData("id", int.class);
		maintenanceRecord = this.repository.findById(id);

		exist = maintenanceRecord != null ;
		if (exist) {
			technician = (Technician) super.getRequest().getPrincipal().getActiveRealm();
			if (!technician.equals(maintenanceRecord.getTechnician()))
				authorised = false;
		} else authorised = false;
		if (authorised && super.getRequest().getMethod().equals("POST")) {
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
		int id;

		id = super.getRequest().getData("id", int.class);
		maintenanceRecord = this.repository.findById(id);

		super.getBuffer().addData(maintenanceRecord);
	}

	@Override
	public void bind(final MaintenanceRecord maintenanceRecord) {
		if(maintenanceRecord.getDraftMode()) {
			super.bindObject(maintenanceRecord, "status", "nextInspectionDate", "estimatedCost", "notes", "aircraft");
		} else {
			super.bindObject(maintenanceRecord, "status");
		}
	}

	@Override
	public void validate(final MaintenanceRecord maintenanceRecord) {

		if (!this.getBuffer().getErrors().hasErrors("status"))
			super.state(maintenanceRecord.getStatus() != null, "status", "acme.validation.maintenancerecord.status.message", maintenanceRecord);

		if (!this.getBuffer().getErrors().hasErrors("nextInspectionDate") && maintenanceRecord.getNextInspectionDate() != null)
			super.state(maintenanceRecord.getNextInspectionDate().after(maintenanceRecord.getMaintenanceMoment()), 
					"nextInspectionDate", "acme.validation.maintenancerecord.nextinspectiondate.message", maintenanceRecord);

		if (!this.getBuffer().getErrors().hasErrors("estimatedCost") && maintenanceRecord.getEstimatedCost() != null)
			super.state(0.00 <= maintenanceRecord.getEstimatedCost().getAmount() && maintenanceRecord.getEstimatedCost().getAmount() <= 1000000.00, 
					"estimatedCost", "acme.validation.maintenancerecord.estimatedCost.message", maintenanceRecord);
		
		if (!this.getBuffer().getErrors().hasErrors("estimatedCost") && maintenanceRecord.getEstimatedCost() != null)
			super.state(ValidCurrencies.isValidCurrency(maintenanceRecord.getEstimatedCost().getCurrency()), "estimatedCost", "acme.validation.technician.maintenance-record.estimatedCost.currency.message", maintenanceRecord);

		if (!this.getBuffer().getErrors().hasErrors("notes") && maintenanceRecord.getNotes() != null)
			super.state(maintenanceRecord.getNotes().length() <= 255, "notes", "acme.validation.maintenancerecord.notes.message", maintenanceRecord);

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

		dataset.put("status", choices.getSelected().getKey());
		dataset.put("statuses", choices);
		dataset.put("aircraft", choicesAircraft.getSelected().getLabel());
		dataset.put("aircrafts", choicesAircraft);

		super.getResponse().addData(dataset);
	}

}