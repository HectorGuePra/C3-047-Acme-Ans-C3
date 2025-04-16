
package acme.features.manager.flights;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.airline.Airline;
import acme.entities.booking.Booking;
import acme.entities.booking.BookingRecord;
import acme.entities.flight.Flight;
import acme.entities.legs.Leg;
import acme.realms.manager.Manager;

@Repository
public interface ManagerFlightRepository extends AbstractRepository {

	@Query("select f from Flight f")
	public List<Flight> findAllFlights();

	@Query("select f from Flight f where f.manager.id = :id")
	public List<Flight> findManagerFlightsByManagerId(int id);

	@Query("select f from Flight f where f.id = :id")
	public Flight findFlightById(int id);

	@Query("select f from Flight f where f.manager.id = :id")
	public List<Flight> findFlightsByManagerId(int id);

	@Query("select distinct f.manager.airline from Flight f where f.manager.id = :id")
	public Airline findAirlineByManager(int id);

	@Query("select f.manager from Flight f where f.manager.id = :id")
	public Manager findManagerByFlightManagerId(int id);

	@Query("select b from Booking b where b.flight.id = :id")
	public List<Booking> findBookingsByFlightId(int id);

	@Query("select l from Leg l where l.flight.id = :id")
	public List<Leg> findLegsByFlightId(int id);

	@Query("select i from BookingRecord i where i.booking.id = :id")
	public List<BookingRecord> findBookingRecordByBookingId(int id);

}
