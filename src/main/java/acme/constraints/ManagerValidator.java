
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.helpers.StringHelper;
import acme.realms.manager.Manager;

public class ManagerValidator extends AbstractValidator<ValidManager, Manager> {

	@Override
	protected void initialise(final ValidManager annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Manager manager, final ConstraintValidatorContext context) {
		assert context != null;
		String initials = "";

		boolean result;

		if (manager == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else {
			String fullName = manager.getUserAccount().getIdentity().getFullName();
			String[] nameParts = fullName.split(", ");

			String[] surnameParts = nameParts[0].split(" ");
			initials = nameParts[1].substring(0, 1);
			initials += surnameParts[0].substring(0, 1);

			if (surnameParts.length > 1)
				initials += surnameParts[1].substring(0, 1);

			boolean validIdentifier;

			String managerIdentifier = manager.getIdentifierNumber();

			boolean validLength = managerIdentifier.length() >= 8 && managerIdentifier.length() <= 9;
			String initialsFromIdentifier = managerIdentifier.subSequence(0, initials.length()).toString();
			boolean validPattern = StringHelper.isEqual(initialsFromIdentifier, initials, true) && managerIdentifier.matches("^[A-Z]{2,3}\\d{6}$");

			validIdentifier = validLength && validPattern;

			super.state(context, validIdentifier, "managerIdentifier", "acme.validation.manager.identifier.message");
		}

		result = !super.hasErrors(context);

		return result;
	}

}
