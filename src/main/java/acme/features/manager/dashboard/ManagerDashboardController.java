
package acme.features.manager.dashboard;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.forms.manager.Dashboard;
import acme.realms.manager.Manager;

@GuiController
public class ManagerDashboardController extends AbstractGuiController<Manager, Dashboard> {

	@Autowired
	private ManagerDashboardShowService showService;


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("show", this.showService);
	}
}
