
package acme.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import acme.constraints.ValidReview;
import acme.entities.airline.Airline;
import acme.entities.airport.Airport;
import acme.entities.flight.Flight;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@ValidReview
public class Review extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ValidString(min = 1, max = 50)
	@Automapped
	private String				alias;

	@Mandatory
	@ValidMoment(min = "2000/01/01  00:00:00", past = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date				postedMoment;

	@Mandatory
	@ValidString(min = 1, max = 50)
	@Automapped
	private String				subject;

	@Mandatory
	@ValidString(min = 1, max = 255)
	@Automapped
	private String				text;

	@Optional
	@ValidNumber(min = 0, max = 10, integer = 2, fraction = 2)
	@Automapped
	private Double				score;

	@Optional
	@Valid
	@Automapped
	private Boolean				isRecommended;

	@Optional
	@Valid
	@ManyToOne(optional = true)
	private Service				serviceReviewed;

	@Optional
	@Valid
	@ManyToOne(optional = true)
	private Airline				airlineReviewed;

	@Optional
	@Valid
	@ManyToOne(optional = true)
	private Airport				airportReviewed;

	@Optional
	@Valid
	@ManyToOne(optional = true)
	private Flight				flightReviewed;
}
