
package acme.entities.booking;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.validation.Mandatory;
import acme.entities.Passenger;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(indexes = {
	@Index(columnList = "booking_id"), @Index(columnList = "passenger_id"), @Index(columnList = "booking_id, passenger_id", unique = true)
})

public class BookingRecord extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Booking				booking;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Passenger			passenger;

}
