
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.realms.Customer;

@Validator
public class CustomerValidator extends AbstractValidator<ValidCustomer, Customer> {

	@Override
	protected void initialise(final ValidCustomer annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Customer customer, final ConstraintValidatorContext context) {
		if (customer == null) {
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
			return false;
		}

		boolean result = true;

		//Validar identifier
		if (!this.isValidIdentifier(customer.getIdentifier(), customer)) {
			super.state(context, false, "identifier", "acme.validation.customer.invalid-identifier.message");
			result = false;
		}

		//Validar phoneNumber
		if (!this.isValidPhoneNumber(customer.getPhoneNumber())) {
			super.state(context, false, "phoneNumber", "acme.validation.customer.invalid-phone-number.message");
			result = false;
		}

		//Validar address
		if (!this.isValidAddress(customer.getPhysicalAddress())) {
			super.state(context, false, "address", "acme.validation.customer.invalid-address.message");
			result = false;
		}

		//Validar la ciudad (city)
		if (!this.isValidCity(customer.getCity())) {
			super.state(context, false, "city", "acme.validation.customer.invalid-city.message");
			result = false;
		}

		//Validar country
		if (!this.isValidCountry(customer.getCountry())) {
			super.state(context, false, "country", "acme.validation.customer.invalid-country.message");
			result = false;
		}

		//Validar earnedPoints - Opcional
		if (customer.getEarnedPoints() != null && !this.isValidPoints(customer.getEarnedPoints())) {
			super.state(context, false, "points", "acme.validation.customer.invalid-points.message");
			result = false;
		}

		return result;
	}

	//Métodos auxiliares para validar cada atributo
	private boolean isValidIdentifier(final String identifier, final Customer customer) {
		if (identifier == null || !identifier.matches("^[A-Z]{2,3}\\d{6}$"))
			return false;

		String initials = this.getCustomerInitials(customer);
		String identifierInitials = identifier.replaceAll("\\d", "");

		return identifierInitials.equals(initials);
	}

	private boolean isValidPhoneNumber(final String phoneNumber) {
		return phoneNumber != null && phoneNumber.matches("^\\+?\\d{6,15}$");
	}

	private boolean isValidAddress(final String address) {
		return address != null && address.length() >= 1 && address.length() <= 255;
	}

	private boolean isValidCity(final String city) {
		return city != null && city.length() >= 1 && city.length() <= 50;
	}

	private boolean isValidCountry(final String country) {
		return country != null && country.length() >= 1 && country.length() <= 50;
	}

	private boolean isValidPoints(final Integer points) {
		return points != null && points >= 0 && points <= 500000;
	}

	//Método para obtener las iniciales del cliente
	private String getCustomerInitials(final Customer customer) {
		String name = customer.getUserAccount().getIdentity().getName();
		String surname = customer.getUserAccount().getIdentity().getSurname();

		String nameInitial = name.substring(0, 1);

		String[] surnames = surname.split(" ");
		String surname1Initial = surnames[0].substring(0, 1);
		String surname2Initial = surnames.length > 1 ? surnames[1].substring(0, 1) : "";

		return nameInitial + surname1Initial + surname2Initial;
	}
}
