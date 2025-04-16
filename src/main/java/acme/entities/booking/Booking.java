
package acme.entities.booking;

import java.beans.Transient;
import java.util.Collection;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidString;
import acme.client.helpers.SpringHelper;
import acme.entities.Passenger;
import acme.entities.flight.Flight;
import acme.entities.flight.FlightRepository;
import acme.realms.Customer;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Booking extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ValidString(min = 6, max = 8, pattern = "^[A-Z0-9]{6,8}$")
	@Column(unique = true)
	private String				locatorCode;

	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date				purchaseMoment;

	@Mandatory
	@Valid
	@Automapped
	private TravelClass			travelClass;

	@Optional
	@ValidString(min = 4, max = 4, pattern = "^\\d{4}$")
	@Automapped
	private String				lastCardNibble;

	@Mandatory
	@Automapped
	private boolean				draftMode;


	@Transient
	public Money getPrice() {
		Money res;
		FlightRepository flightRepository = SpringHelper.getBean(FlightRepository.class);
		BookingRepository bookingRepository = SpringHelper.getBean(BookingRepository.class);

		if (this.getFlight() == null) {
			Money withoutResult = new Money();
			withoutResult.setAmount(0.0);
			withoutResult.setCurrency("EUR");
			return withoutResult;
		} else {
			res = flightRepository.findCostByFlight(this.flight.getId());
			Collection<Passenger> pb = bookingRepository.findPassengersByBooking(this.getId());
			Double amount = res.getAmount() * pb.size();
			res.setAmount(amount);
			return res;
		}

	}


	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Flight		flight;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Customer	customer;

}
