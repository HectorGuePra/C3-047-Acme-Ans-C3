
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.realms.manager.Manager;

public class ManagerValidator extends AbstractValidator<ValidManager, Manager> {

	@Override
	protected void initialise(final ValidManager annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Manager manager, final ConstraintValidatorContext context) {
		assert context != null;

		boolean result = true;

		if (manager == null) {
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
			result = false;
		} else {

			String managerIdentifier = manager.getIdentifierNumber();
			if (managerIdentifier == null || managerIdentifier.trim().isEmpty())
				return true;

			if (manager.getUserAccount() != null && manager.getUserAccount().getIdentity() != null) {
				String name = manager.getUserAccount().getIdentity().getName();
				String surname = manager.getUserAccount().getIdentity().getSurname();

				if (name != null && surname != null && name.length() > 0 && surname.length() > 0) {
					char firstInitial = Character.toUpperCase(name.charAt(0));
					char secondInitial = Character.toUpperCase(surname.charAt(0));

					boolean correctInitials = managerIdentifier.charAt(0) == firstInitial && managerIdentifier.charAt(1) == secondInitial;
					if (!correctInitials) {
						super.state(context, false, "identifierNumber", "acme.validation.manager.identifier.message");
						result = false;
					}
				}
			}
		}
		return result;
	}

}
