
package acme.entities.tasks;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.Valid;

import acme.entities.maintenancerecord.MaintenanceRecord;
import acme.client.components.basis.AbstractEntity;
import acme.client.components.validation.Mandatory;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class MaintenanceRecordTask extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;
	
	@Mandatory
	@Valid
	@ManyToOne
	private MaintenanceRecord maintenanceRecord;
	
	@Mandatory
	@Valid
	@ManyToOne
	private Task task;
	
}
