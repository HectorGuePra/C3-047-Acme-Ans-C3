
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.entities.Review;

@Validator
public class ReviewValidator extends AbstractValidator<ValidReview, Review> {

	@Override
	protected void initialise(final ValidReview annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Review review, final ConstraintValidatorContext context) {
		if (review == null) {
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
			return false;
		}

		boolean result = true;

		boolean hasValidRelation = review.getServiceReviewed() != null || review.getAirlineReviewed() != null || review.getAirportReviewed() != null || review.getFlightReviewed() != null;

		if (!hasValidRelation) {
			super.state(context, false, "*", "acme.validation.review.missing-relation.message");
			result = false;
		}

		return result;
	}
}
