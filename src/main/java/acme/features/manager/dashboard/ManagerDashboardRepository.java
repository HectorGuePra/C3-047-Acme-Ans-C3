
package acme.features.manager.dashboard;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.airport.Airport;
import acme.entities.legs.LegStatus;
import acme.forms.manager.FlightStatistics;
import acme.forms.manager.LegsByStatus;

@Repository
public interface ManagerDashboardRepository extends AbstractRepository {

	@Query("select count(m) + 1 from Manager m where m.experience > (select am.experience from Manager am where am.id = :managerId)")
	Integer findRankingManagerByExperience(int managerId);

	@Query("select 65 - (year(:currentDate) - year(m.birthdate)) from Manager m where m.id = :managerId")
	Integer findYearsUntilRetirement(int managerId, Date currentDate);

	@Query("select 1.0 * count(l) / nullif((select count(lg) from Leg lg where lg.flight.manager.id = :managerId), 0) from Leg l where l.flight.manager.id = :managerId and l.status = :status")
	Double findRatioStatusLegs(int managerId, LegStatus status);

	@Query("select a from Airport a where a in (select l.departureAirport from Leg l where l.flight.manager.id = :managerId) or a in (select l.arrivalAirport from Leg l where l.flight.manager.id = :managerId) order by ((select count(l2) from Leg l2 where l2.flight.manager.id = :managerId and l2.departureAirport = a) + (select count(l3) from Leg l3 where l3.flight.manager.id = :managerId and l3.arrivalAirport = a)) desc")
	List<Airport> findMostPopularAirport(int managerId, PageRequest pageRequest);

	@Query("select a from Airport a where a in (select l.departureAirport from Leg l where l.flight.manager.id = :managerId) or a in (select l.arrivalAirport from Leg l where l.flight.manager.id = :managerId) order by ((select count(l2) from Leg l2 where l2.flight.manager.id = :managerId and l2.departureAirport = a) + (select count(l3) from Leg l3 where l3.flight.manager.id = :managerId and l3.arrivalAirport = a)) asc")
	List<Airport> findLessPopularAirport(int managerId, PageRequest pageRequest);

	@Query("select l.status as status, count(l) as legsNumber from Leg l where l.flight.manager.id = :managerId group by l.status")
	List<LegsByStatus> findNumberOfLegsByStatus(int managerId);

	@Query("select avg(f.cost.amount) as costAverage, min(f.cost.amount) as minimum, max(f.cost.amount) as maximum, stddev(f.cost.amount) as standardDeviation from Flight f where f.manager.id = :managerId")
	FlightStatistics findStatisticsFromMyFlights(int managerId);

}
