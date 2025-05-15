package acme.features.technician.maintenanceRecordTask;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.maintenancerecord.MaintenanceRecord;
import acme.entities.tasks.Task;
import acme.entities.tasks.MaintenanceRecordTask;

@Repository
public interface TechnicianMaintenanceRecordTaskRepository extends AbstractRepository {

	@Query("SELECT mrt FROM MaintenanceRecordTask mrt WHERE mrt.maintenanceRecord.id = :maintenanceRecordId ")
	Collection<MaintenanceRecordTask> findAllByMaintenanceRecordId(final int maintenanceRecordId);

	@Query("SELECT mrt FROM MaintenanceRecordTask mrt WHERE mrt.id = :id")
	MaintenanceRecordTask findById(int id);
	
	@Query("SELECT t FROM Task t WHERE t.technician.id = :technicianId")
	Collection<Task> findTasksByTechnicianId(int technicianId);	
	
	@Query("SELECT t FROM Task t WHERE t.technician.id = :technicianId AND t.draftMode = false")
	Collection<Task> findPublishedTasksByTechnicianId(int technicianId);
	
	@Query("SELECT t FROM Task t JOIN MaintenanceRecordTask rt ON t.id = rt.task.id WHERE rt.maintenanceRecord.id = :id")
	Collection<Task> findTasksByMaintenanceRecordId(int id);
	
	@Query("SELECT mr FROM MaintenanceRecord mr WHERE mr.id = :maintenanceRecordId")
	MaintenanceRecord findMaintenanceRecordById(int maintenanceRecordId);
	
	@Query("SELECT mr FROM MaintenanceRecord mr WHERE mr.technician.id = :technicianId")
	Collection<MaintenanceRecord> findMaintenanceRecordsByTechnicianId(int technicianId);
	
	@Query("SELECT t FROM Task t WHERE t.id = :taskId")
	Task findTaskById(int taskId);
	
	@Query("SELECT mrt FROM MaintenanceRecordTask mrt WHERE mrt.maintenanceRecord.id = :maintenanceRecordId AND mrt.task.id = :taskId")
	MaintenanceRecordTask findMaintenanceRecordTaskByMaintenanceRecordIdAndTaskId(int maintenanceRecordId, int taskId);
	
}
