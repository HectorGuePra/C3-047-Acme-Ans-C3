package acme.features.technician.maintenanceRecordTask;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.tasks.MaintenanceRecordTask;
import acme.realms.technician.Technician;

@GuiController
public class TechnicianMaintenanceRecordTaskGuiController extends AbstractGuiController<Technician, MaintenanceRecordTask> {

	//Internal state --------------------------------------------------------------

	@Autowired
	private TechnicianMaintenanceRecordTaskListService	listService;

	@Autowired
	private TechnicianMaintenanceRecordTaskShowService		showService;

	@Autowired
	private TechnicianMaintenanceRecordTaskCreateService	createService;

	@Autowired
	private TechnicianMaintenanceRecordTaskDeleteService	deleteService;

	//Constructors ----------------------------------------------------------------


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("delete", this.deleteService);
	}

}