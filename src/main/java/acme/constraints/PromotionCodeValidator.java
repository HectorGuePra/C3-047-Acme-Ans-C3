
package acme.constraints;

import java.text.SimpleDateFormat;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.helpers.MomentHelper;
import acme.client.helpers.StringHelper;

public class PromotionCodeValidator extends AbstractValidator<ValidPromotionCode, String> {

	private static final String FLIGHT_NUMBER_PATTERN = "^[A-Z]{4}-[0-9]{2}$";


	@Override
	public void initialise(final ValidPromotionCode annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final String promotionCode, final ConstraintValidatorContext context) {
		assert context != null;

		if (!StringHelper.isBlank(promotionCode)) {

			boolean matchesPattern = StringHelper.matches(promotionCode, PromotionCodeValidator.FLIGHT_NUMBER_PATTERN);
			super.state(context, matchesPattern, "promotionCode", "acme.validation.service.promotion.code");

			SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
			String yearDigits = yearFormat.format(MomentHelper.getCurrentMoment()).substring(2);
			boolean containsCurrentYear = StringHelper.endsWith(promotionCode, yearDigits, false);
			super.state(context, containsCurrentYear, "promotionCode", "acme.validation.service.promotion.code");
		}

		return !super.hasErrors(context);
	}
}
