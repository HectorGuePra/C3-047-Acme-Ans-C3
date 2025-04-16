
package acme.features.flightcrewmember.flightassignment;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.flightassignment.FlightAssignment;
import acme.realms.flightcrewmember.FlightCrewMember;

@GuiController
public class FlightAssignmentsController extends AbstractGuiController<FlightCrewMember, FlightAssignment> {

	@Autowired
	private FlightAssignmentsLegLandedListService	listLandedService;

	@Autowired
	private FlightAssignmentsLegPlannedListService	listPlannedService;

	@Autowired
	private FlightAssignmentShowService				showService;

	@Autowired
	private FlightAssignmentsCreateService			createService;

	@Autowired
	private FlightAssignmentUpdateService			updateService;

	@Autowired
	private FlightAssignmentPublishService			publishService;


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listLandedService);
		super.addCustomCommand("list-planned", "list", this.listPlannedService);
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("update", this.updateService);
		super.addCustomCommand("publish", "update", this.publishService);
	}
}
