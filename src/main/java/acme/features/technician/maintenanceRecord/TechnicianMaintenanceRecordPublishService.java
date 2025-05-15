package acme.features.technician.maintenanceRecord;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircraft.Aircraft;
import acme.entities.maintenancerecord.MaintenanceRecord;
import acme.entities.maintenancerecord.MaintenanceRecordStatus;
import acme.entities.tasks.Task;
import acme.realms.technician.Technician;

@GuiService
public class TechnicianMaintenanceRecordPublishService extends AbstractGuiService<Technician, MaintenanceRecord> {

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

		exist = maintenanceRecord != null && maintenanceRecord.getDraftMode();
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
		super.bindObject(maintenanceRecord, "status", "nextInspectionDate", "estimatedCost", "notes", "aircraft");
	}

	@Override
	public void validate(final MaintenanceRecord maintenanceRecord) {
		int id;
		id = super.getRequest().getData("id", int.class);
		Collection<Task> tasks = this.repository.findTasksByMaintenanceRecordId(id);

		if (!this.getBuffer().getErrors().hasErrors("draftMode") && maintenanceRecord.getDraftMode())
			super.state(tasks.size() > 0 && tasks.stream().allMatch(t -> !t.getDraftMode()), 
					"aircraft", "acme.validation.technician.maintenance-record.published.message", maintenanceRecord);
	}

	@Override
	public void perform(final MaintenanceRecord maintenanceRecord) {
		Boolean draftMode = false;
		maintenanceRecord.setDraftMode(draftMode);
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
		choicesAircraft = SelectChoices.from(aircrafts, "id", maintenanceRecord.getAircraft());

		dataset = super.unbindObject(maintenanceRecord, "status", "nextInspectionDate", "estimatedCost", "notes", "aircraft", "draftMode");

		dataset.put("status", choices.getSelected().getKey());
		dataset.put("statuses", choices);
		dataset.put("aircraft", choicesAircraft.getSelected().getKey());
		dataset.put("aircrafts", choicesAircraft);

		super.getResponse().addData(dataset);
	}

}