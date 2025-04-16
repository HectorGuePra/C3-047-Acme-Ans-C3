package acme.features.technician.maintenanceRecord;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.aircraft.Aircraft;
import acme.entities.maintenancerecord.MaintenanceRecord;
import acme.entities.tasks.Task;

@Repository
public interface TechnicianMaintenanceRecordRepository extends AbstractRepository {

	@Query("SELECT m FROM MaintenanceRecord m WHERE m.technician.id = :technicianId ")
	Collection<MaintenanceRecord> findAllByTechnicianId(final int technicianId);

	@Query("SELECT m FROM MaintenanceRecord m WHERE m.id = :id")
	MaintenanceRecord findById(int id);
	
	@Query("SELECT a FROM Aircraft a")
	Collection<Aircraft> findAllAircrafts();
	
	@Query("SELECT t FROM Task t JOIN MaintenanceRecordTask rt ON t.id = rt.task.id WHERE rt.maintenanceRecord.id = :id")
	Collection<Task> findTasksByMaintenanceRecordId(int id);
}
