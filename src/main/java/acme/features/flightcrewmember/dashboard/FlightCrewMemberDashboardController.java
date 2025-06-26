
package acme.features.flightcrewmember.dashboard;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.forms.flightcrewmember.Dashboard;
import acme.realms.flightcrewmember.FlightCrewMember;

@GuiController
public class FlightCrewMemberDashboardController extends AbstractGuiController<FlightCrewMember, Dashboard> {

	@Autowired
	private FlightCrewMemberDashboardShowService showService;


	@PostConstruct
	protected void initialise() {

		super.addBasicCommand("show", this.showService);
	}
}
