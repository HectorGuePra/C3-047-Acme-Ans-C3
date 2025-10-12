
package acme.features.authenticated.assistanceAgent;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Authenticated;
import acme.client.components.principals.UserAccount;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airline.Airline;
import acme.realms.assistanceAgents.AssistanceAgent;

@GuiService
public class AuthenticatedAssistanceAgentCreateService extends AbstractGuiService<Authenticated, AssistanceAgent> {

	@Autowired
	private AuthenticatedAssistanceAgentRepository repository;


	@Override
	public void authorise() {
		boolean status;
		status = !super.getRequest().getPrincipal().hasRealmOfType(AssistanceAgent.class);
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int userId = super.getRequest().getPrincipal().getAccountId();
		UserAccount user = this.repository.findUserAccountById(userId);
		AssistanceAgent agent = new AssistanceAgent();
		agent.setUserAccount(user);
		agent.setMoment(MomentHelper.getCurrentMoment());
		super.getBuffer().addData(agent);
	}

	@Override
	public void bind(final AssistanceAgent object) {
		assert object != null;
		super.bindObject(object, "employeeCode", "spokenLanguages", "briefBio", "salary", "photo");
		Integer airlineId = super.getRequest().getData("airline", int.class);
		if (airlineId != null) {
			Airline airline = this.repository.findAirlineById(airlineId);
			object.setAirline(airline);
		}
	}

	@Override
	public void validate(final AssistanceAgent object) {
		assert object != null;

		if (object.getEmployeeCode() != null && !object.getEmployeeCode().isBlank()) {
			boolean isDuplicate = this.repository.findAssistanceAgents().stream().anyMatch(agent -> agent.getEmployeeCode() != null && agent.getEmployeeCode().equals(object.getEmployeeCode()));
			super.state(!isDuplicate, "employeeCode", "authenticated.assistance-agent.form.error.duplicated-employee-code");
		}
	}

	@Override
	public void perform(final AssistanceAgent object) {
		assert object != null;
		this.repository.save(object);
	}

	@Override
	public void unbind(final AssistanceAgent object) {
		assert object != null;
		Dataset dataset = super.unbindObject(object, "employeeCode", "spokenLanguages", "briefBio", "salary", "photo");
		Collection<Airline> airlines = this.repository.findAirlines();
		SelectChoices airlineChoices = SelectChoices.from(airlines, "iataCode", object.getAirline());
		dataset.put("airlineChoices", airlineChoices);
		dataset.put("airline", airlineChoices.getSelected().getKey());
		super.getResponse().addData(dataset);
	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equals("POST"))
			PrincipalHelper.handleUpdate();
	}
}
