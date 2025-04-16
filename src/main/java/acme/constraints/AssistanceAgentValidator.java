
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.principals.DefaultUserIdentity;
import acme.client.components.validation.AbstractValidator;
import acme.client.helpers.StringHelper;
import acme.entities.AssistanceAgent;

public class AssistanceAgentValidator extends AbstractValidator<ValidAssistanceAgent, AssistanceAgent> {

	@Override
	public void initialise(final ValidAssistanceAgent annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final AssistanceAgent agent, final ConstraintValidatorContext context) {
		assert context != null;

		if (agent == null || agent.getEmployeeCode() == null || agent.getIdentity() == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else if (StringHelper.isBlank(agent.getEmployeeCode()))
			super.state(context, false, "employeeCode", "javax.validation.constraints.NotBlank.message");
		else {
			DefaultUserIdentity identity = agent.getIdentity();
			char nameFirstLetter = identity.getName().charAt(0);
			char surnameFirstLetter = identity.getSurname().charAt(0);
			String requiredInitials = "" + nameFirstLetter + surnameFirstLetter;

			String employeeCodeInitials = agent.getEmployeeCode().substring(0, 2);

			boolean validInitials = employeeCodeInitials.equals(requiredInitials);

			super.state(context, validInitials, "employeeCode", "acme.validation.assistanceagent.code.message");
		}

		return !super.hasErrors(context);
	}
}
