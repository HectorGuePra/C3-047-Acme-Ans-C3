
package acme.entities.tasks;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.Valid;

import acme.entities.maintenancerecord.MaintenanceRecord;
import acme.client.components.basis.AbstractEntity;
import acme.client.components.validation.Mandatory;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(indexes = {
		@Index(columnList = "task"), @Index(columnList = "maintenanceRecord"), @Index(columnList = "task, maintenanceRecord")
	})
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
