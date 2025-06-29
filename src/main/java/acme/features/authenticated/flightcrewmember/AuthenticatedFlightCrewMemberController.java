package acme.features.authenticated.flightcrewmember;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.principals.Authenticated;
import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.realms.flightcrewmember.FlightCrewMember;

@GuiController
public class AuthenticatedFlightCrewMemberController extends AbstractGuiController<Authenticated, FlightCrewMember> {

	@Autowired
	private AuthenticatedFlightCrewMemberCreateService createService;

	@Autowired
	private AuthenticatedFlightCrewMemberUpdateService updateService;


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("update", this.updateService);
	}

}
