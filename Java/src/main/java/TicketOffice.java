import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TicketOffice {

    private final TrainDataService trainDataService;
    private final BookingReferenceGenerator bookingReferenceGenerator;

    public TicketOffice(TrainDataService trainDataService, BookingReferenceGenerator bookingReferenceGenerator) {
        this.trainDataService = trainDataService;
        this.bookingReferenceGenerator = bookingReferenceGenerator;
    }
    
    public Reservation makeReservation(ReservationRequest request) {
        String train = request.trainId;

        if(request.seatCount == 0) {
            return Reservation.none(request.trainId);
        }

        List<Seat> availableSeats = trainDataService.availableSeatsOn(train);
        if(availableSeats.isEmpty()) {
            return Reservation.none(train);
        }
        List<Seat> seatsToBook = availableSeats.stream().limit(request.seatCount).collect(Collectors.toList());
        String bookingReference = reserveSeatsOn(train, seatsToBook);
        return new Reservation(request.trainId, seatsToBook, bookingReference);
    }

    private String reserveSeatsOn(String train, List<Seat> seatsToReserve) {
        String bookingReference = bookingReferenceGenerator.generate();
        String[] seatNumbers = seatsToReserve.stream().map(Seat::number).toArray(String[]::new);
        trainDataService.reserve(train, seatNumbers, bookingReference);
        return bookingReference;
    }
}