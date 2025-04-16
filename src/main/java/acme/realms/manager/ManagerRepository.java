
package acme.realms.manager;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface ManagerRepository extends AbstractRepository {

	@Query("SELECT a FROM Manager a")
	List<Manager> findAllManagers();
}
