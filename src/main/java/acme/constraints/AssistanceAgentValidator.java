
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.realms.assistanceAgents.AssistanceAgent;
import acme.realms.assistanceAgents.AssistanceAgentRepository;

@Validator
public class AssistanceAgentValidator extends AbstractValidator<ValidAssistanceAgent, AssistanceAgent> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AssistanceAgentRepository repository;

	// ConstraintValidator interface ------------------------------------------


	@Override
	protected void initialise(final ValidAssistanceAgent annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final AssistanceAgent assistanceAgent, final ConstraintValidatorContext context) {
		assert context != null;

		if (assistanceAgent == null) {
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
			return false;
		}

		String name = assistanceAgent.getIdentity().getName();
		String surname = assistanceAgent.getIdentity().getSurname();
		String employeeCode = assistanceAgent.getEmployeeCode();

		if (StringUtils.hasText(name) && StringUtils.hasText(surname) && StringUtils.hasText(employeeCode)) {
			boolean correctInitials = employeeCode.toUpperCase().charAt(0) == name.toUpperCase().charAt(0) && employeeCode.toUpperCase().charAt(1) == surname.toUpperCase().charAt(0);
			super.state(context, correctInitials, "employeeCode", "acme.validation.assistanceAgent.employeeCode.initials.message");
		}

		if (StringUtils.hasText(employeeCode)) {
			AssistanceAgent agentInDB = this.repository.findAssistanceAgentByEmployeeCode(employeeCode);

			boolean isUnique = agentInDB == null || agentInDB.getId() == assistanceAgent.getId();

			super.state(context, isUnique, "employeeCode", "acme.validation.assistanceAgent.employeeCode.unique.message");
		}

		return !super.hasErrors(context);
	}
}
