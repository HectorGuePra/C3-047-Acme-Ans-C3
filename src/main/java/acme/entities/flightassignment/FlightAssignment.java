
package acme.entities.flightassignment;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidString;
import acme.entities.legs.Leg;
import acme.entities.legs.LegStatus;
import acme.realms.flightcrewmember.FlightCrewMember;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(indexes = {
	@Index(columnList = "allocated_flight_crew_member_id"), @Index(columnList = "leg_id"), @Index(columnList = "leg_id, duty, draftMode"), @Index(columnList = "allocated_flight_crew_member_id, draftMode")
})
public class FlightAssignment extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Leg					leg;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private FlightCrewMember	allocatedFlightCrewMember;

	@Mandatory
	@Valid
	@Automapped
	private CrewsDuty			duty;

	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date				momentLastUpdate;

	@Mandatory
	@Valid
	@Automapped
	private AssigmentStatus		currentStatus;

	@Optional
	@ValidString(min = 0, max = 255)
	@Automapped
	private String				remarks;

	@Mandatory
	@Valid
	@Automapped
	private Boolean				draftMode;


	@Transient
	public Boolean getIsLegLanded() {
		return this.leg != null && LegStatus.LANDED.equals(this.leg.getStatus());
	}
}
