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
public class TechnicianMaintenanceRecordTaskShowService extends AbstractGuiService<Technician, MaintenanceRecordTask> {

	@Autowired
	private TechnicianMaintenanceRecordTaskRepository repository;

	//AbstractGuiService state ----------------------------------------------------------


	@Override
	public void authorise() {
		boolean authorised = false;
		Technician technician;
		int mrtId;
		MaintenanceRecordTask mrt;
		
		technician = (Technician) super.getRequest().getPrincipal().getActiveRealm();
		mrtId = super.getRequest().getData("id", int.class);
		mrt = this.repository.findById(mrtId);

		if (mrt.getMaintenanceRecord().getTechnician().equals(technician))
			authorised = true;

		super.getResponse().setAuthorised(authorised);
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
	public void unbind(final MaintenanceRecordTask mrt) {
		
		SelectChoices task;
		SelectChoices mr;
		Collection<Task> tasks;
		Collection<MaintenanceRecord> mrs;

		Dataset dataset;
		
		tasks = this.repository.findAllTasks();
		mrs = this.repository.findMaintenanceRecordsByTechnicianId(super.getRequest().getPrincipal().getActiveRealm().getId());
		task = SelectChoices.from(tasks, "id", mrt.getTask());
		mr = SelectChoices.from(mrs, "id", mrt.getMaintenanceRecord());

		dataset = super.unbindObject(mrt, "task");
		dataset.put("tasks", task);
		dataset.put("task", task.getSelected().getKey());
		dataset.put("maintenanceRecord", mr.getSelected().getKey());

		super.getResponse().addData(dataset);
	}
}
