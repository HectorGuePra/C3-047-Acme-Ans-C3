
package acme.features.customer.bookingRecord;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.Passenger;
import acme.entities.booking.Booking;
import acme.entities.booking.BookingRecord;

@Repository
public interface CustomerBookingRecordRepository extends AbstractRepository {

	@Query("select br from BookingRecord br where br.booking.customer.id = :id")
	Collection<BookingRecord> findBookingRecordByCustomerId(int id);

	@Query("select br from BookingRecord br where br.id = :id")
	BookingRecord findBookingRecordById(int id);

	@Query("select b from Booking b where b.customer.id = :id")
	Collection<Booking> findBookingsByCustomerId(int id);

	@Query("select p from Passenger p where p.customer.id = :id")
	Collection<Passenger> findPassengersByCustomerId(int id);

	@Query("select b from Booking b where b.id = :id")
	Booking findBookingById(int id);

	@Query("select p from Passenger p where p.id= :id")
	Passenger findPassengerById(int id);

}
