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
