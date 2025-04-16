
package acme.entities.booking;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.Passenger;

@Repository
public interface BookingRepository extends AbstractRepository {

	@Query("select br.passenger from BookingRecord br where br.booking.id = :bookingId")
	Collection<Passenger> findPassengersByBooking(int bookingId);

	@Query("select b from Booking b where b.locatorCode = :code")
	public Booking findBookingByLocatorCode(String code);

	@Query("select i from BookingRecord i where i.passenger.id = :passengerId and i.booking.id = :bookingId")
	BookingRecord findBookingRecordByIds(int passengerId, int bookingId);
}
