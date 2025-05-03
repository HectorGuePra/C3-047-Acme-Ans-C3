package acme.features.technician.task;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.tasks.MaintenanceRecordTask;
import acme.entities.tasks.Task;

@Repository
public interface TechnicianTaskRepository extends AbstractRepository {

	@Query("SELECT t FROM Task t WHERE t.technician.id = :technicianId ")
	Collection<Task> findAllByTechnicianId(final int technicianId);

	@Query("SELECT t FROM Task t WHERE t.id = :id")
	Task findById(int id);
	
	@Query("SELECT mrt FROM MaintenanceRecordTask mrt WHERE mrt.task.id = :taskId")
	Collection<MaintenanceRecordTask> findMaintenanceRecordTasksByTaskId(int taskId);

}
