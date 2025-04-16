package acme.features.technician.maintenanceRecordTask;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.maintenancerecord.MaintenanceRecord;
import acme.entities.tasks.MaintenanceRecordTask;
import acme.realms.technician.Technician;

@GuiService
public class TechnicianMaintenanceRecordTaskListService extends AbstractGuiService<Technician, MaintenanceRecordTask> {

	@Autowired
	private TechnicianMaintenanceRecordTaskRepository repository;


	@Override
	public void authorise() {
		boolean authorised = false;
		Technician technician;
		MaintenanceRecord mr;
		int maintenanceRecordId;
		
		maintenanceRecordId = super.getRequest().getData("id", int.class);
		technician = (Technician) super.getRequest().getPrincipal().getActiveRealm();
		mr = this.repository.findMaintenanceRecordById(maintenanceRecordId);

		if (mr.getTechnician().equals(technician))
			authorised = true;

		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {

		Collection<MaintenanceRecordTask> mrts;
		int id;

		id = super.getRequest().getData("id", int.class);
		mrts = this.repository.findAllByMaintenanceRecordId(id);

		super.getBuffer().addData(mrts);
	}

	@Override
	public void unbind(final MaintenanceRecordTask mrts) {

		Dataset dataset;

		dataset = super.unbindObject(mrts, "task.id", "task.type", "task.priority", "task.duration");

		super.getResponse().addData(dataset);
	}
}
