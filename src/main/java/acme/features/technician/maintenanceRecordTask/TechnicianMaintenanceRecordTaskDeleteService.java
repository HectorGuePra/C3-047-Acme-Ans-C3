package acme.features.technician.maintenanceRecordTask;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.maintenancerecord.MaintenanceRecord;
import acme.entities.tasks.MaintenanceRecordTask;
import acme.entities.tasks.Task;
import acme.realms.technician.Technician;

@GuiService
public class TechnicianMaintenanceRecordTaskDeleteService extends AbstractGuiService<Technician, MaintenanceRecordTask> {

	@Autowired
	private TechnicianMaintenanceRecordTaskRepository repository;


	// AbstractGuiService interface -------------------------------------------
	@Override
	public void authorise() {
		boolean exist;
		Technician technician;
		int mrtId;
		MaintenanceRecordTask mrt;
		
		mrtId = super.getRequest().getData("id", int.class);
		mrt = this.repository.findById(mrtId);

		exist = mrt != null;
		if (exist) {
			technician = (Technician) super.getRequest().getPrincipal().getActiveRealm();
			if (technician.equals(mrt.getMaintenanceRecord().getTechnician()))
				super.getResponse().setAuthorised(true);
		}
	}

	@Override
	public void load() {
		int id;
		MaintenanceRecordTask mrt;

		id = super.getRequest().getData("id", int.class);
		mrt = this.repository.findById(id);

		super.getBuffer().addData(mrt);
	}
	
	@Override
	public void bind(final MaintenanceRecordTask mrt) {
		super.bindObject(mrt, "maintenanceRecord", "task");
	}
	
	@Override
	public void validate(final MaintenanceRecordTask mrt) {

	}

	@Override
	public void perform(final MaintenanceRecordTask mrt) {
		this.repository.delete(mrt);
	}

	@Override
	public void unbind(final MaintenanceRecordTask mrt) {

		Technician technician;
		SelectChoices choichesTasks;
		MaintenanceRecord mr;
		Collection<Task> tasks;

		Dataset dataset;
		technician = (Technician) super.getRequest().getPrincipal().getActiveRealm();
		mr = mrt.getMaintenanceRecord();
		tasks = mr.getDraftMode() ? this.repository.findTasksByTechnicianId(technician.getId()) 
				: this.repository.findPublishedTasksByTechnicianId(technician.getId());
		choichesTasks = SelectChoices.from(tasks, "id", mrt.getTask());

		dataset = super.unbindObject(mrt, "task");
		dataset.put("tasks", choichesTasks);
		dataset.put("task", choichesTasks.getSelected().getKey());
		dataset.put("maintenanceRecord", mr);

		super.getResponse().addData(dataset);
	}
}