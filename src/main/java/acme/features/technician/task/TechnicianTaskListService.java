package acme.features.technician.task;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.tasks.Task;
import acme.realms.technician.Technician;

@GuiService
public class TechnicianTaskListService extends AbstractGuiService<Technician, Task> {

	//Internal state ---------------------------------------------

	@Autowired
	private TechnicianTaskRepository repository;

	//AbstractService interface ----------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Collection<Task> tasks;
		int technicianId;
		technicianId = super.getRequest().getPrincipal().getActiveRealm().getId();
		tasks = this.repository.findAllByTechnicianId(technicianId);

		super.getBuffer().addData(tasks);
	}

	@Override
	public void unbind(final Task task) {
		Dataset dataset;

		dataset = super.unbindObject(task, "id", "type", "priority", "duration");

		super.addPayload(dataset, task, "description");

		super.getResponse().addData(dataset);
	}

}
