
package acme.entities.maintenancerecord;

import java.util.Date;

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
import acme.client.components.validation.ValidMoney;
import acme.client.components.validation.ValidString;
import acme.entities.aircraft.Aircraft;
import acme.realms.technician.Technician;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class MaintenanceRecord extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ValidMoment
	@Temporal(TemporalType.TIMESTAMP)
	private Date maintenanceMoment;

	@Mandatory
	@Valid
	@Automapped
	private MaintenanceRecordStatus status;

	@Mandatory
	@ValidMoment
	@Temporal(TemporalType.TIMESTAMP)
	private Date nextInspectionDate;

	@Mandatory
	@ValidMoney(min = 0, max=1000000)
	@Automapped
	private Money estimatedCost;


	@Optional
	@ValidString(min=1, max = 255)
	@Automapped
	private String notes;

	@ManyToOne
	@Mandatory
	@Automapped
	private Aircraft aircraft;
	
	@ManyToOne
	@Mandatory
	@Automapped
	private Technician technician;
	
	@Mandatory
	@Valid
	@Automapped
	private Boolean draftMode;

}
