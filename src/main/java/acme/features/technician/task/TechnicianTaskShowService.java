package acme.features.technician.task;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.tasks.Task;
import acme.entities.tasks.TaskType;
import acme.realms.technician.Technician;

@GuiService
public class TechnicianTaskShowService extends AbstractGuiService<Technician, Task> {

	//Internal state ----------------------------------------------------------

	@Autowired
	private TechnicianTaskRepository repository;

	//AbstractService Interface -----------------------------------------------


	@Override
	public void authorise() {
		Task task;
		int id;

		id = super.getRequest().getData("id", int.class);
		task = this.repository.findById(id);
		Technician technician = (Technician) super.getRequest().getPrincipal().getActiveRealm();

		if (technician.equals(task.getTechnician()))
			super.getResponse().setAuthorised(true);

	}

	@Override
	public void load() {
		Task task;
		int id;

		id = super.getRequest().getData("id", int.class);
		task = this.repository.findById(id);

		super.getBuffer().addData(task);
	}

	@Override
	public void unbind(final Task task) {

		SelectChoices types;

		Dataset dataset;
		types = SelectChoices.from(TaskType.class, task.getType());

		dataset = super.unbindObject(task, "type", "priority", "duration", "description", "draftMode");
		dataset.put("type", types);

		super.getResponse().addData(dataset);
	}

}
