
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
		@Index(columnList = "task_id"), @Index(columnList = "maintenance_record_id"), @Index(columnList = "task_id, maintenance_record_id")
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
