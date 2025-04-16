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
public class TechnicianMaintenanceRecordTaskCreateService extends AbstractGuiService<Technician, MaintenanceRecordTask> {

	@Autowired
	private TechnicianMaintenanceRecordTaskRepository repository;


	// AbstractGuiService interface -------------------------------------------
	@Override
	public void authorise() {
		boolean authorised = false;
		Technician technician;
		int maintenanceRecordId;
		MaintenanceRecord mr;
		
		technician = (Technician) super.getRequest().getPrincipal().getActiveRealm();
		maintenanceRecordId = super.getRequest().getData("id", int.class);
		mr = this.repository.findMaintenanceRecordById(maintenanceRecordId);

		if (mr.getTechnician().equals(technician))
			authorised = true;

		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		int mrId;
		MaintenanceRecord mr;
		MaintenanceRecordTask mrt;

		mrId = super.getRequest().getData("id", int.class);
		mr = this.repository.findMaintenanceRecordById(mrId);

		mrt = new MaintenanceRecordTask();
		mrt.setMaintenanceRecord(mr);
		super.getBuffer().addData(mrt);
	}

	@Override
	public void bind(final MaintenanceRecordTask mrt) {
		super.bindObject(mrt, "task");
	}

	@Override
	public void validate(final MaintenanceRecordTask mrt) {
		
	}

	@Override
	public void perform(final MaintenanceRecordTask mrt) {
		this.repository.save(mrt);
	}

	@Override
	public void unbind(final MaintenanceRecordTask mrt) {

		int mrId;
		SelectChoices task;
		Collection<Task> tasks;

		Dataset dataset;
		mrId = super.getRequest().getData("id", int.class);
		tasks = this.repository.findAllTasks();
		task = SelectChoices.from(tasks, "id", mrt.getTask());

		dataset = super.unbindObject(mrt, "task");
		dataset.put("maintenanceRecord", mrId);
		dataset.put("tasks", task);
		dataset.put("task", task.getSelected().getKey());

		super.getResponse().addData(dataset);
	}
}