
package acme.features.customer.passenger;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.Passenger;
import acme.entities.booking.Booking;
import acme.realms.Customer;

@Repository
public interface CustomerPassengerRepository extends AbstractRepository {

	@Query("select distinct br.passenger from BookingRecord br where br.booking.id = :bookingId")
	Collection<Passenger> findPassengersByBookingId(int bookingId);

	@Query("select p from Passenger p where p.id = :passengerId")
	Passenger findPassengerById(int passengerId);

	@Query("select c from Customer c where c.id = :customerId")
	Customer findCustomerById(int customerId);

	@Query("select p from Passenger p where p.customer.id = :customerId")
	Collection<Passenger> findPassengersByCustomerId(int customerId);

	@Query("select count(p) > 0 from Passenger p where p.passport = :passport and p.id != :passengerId")
	boolean existsPassengerWithDuplicatedPassport(String passport, int passengerId);

	@Query("select b from Booking b where b.id = :bookingId")
	Booking findBookingById(int bookingId);

}
