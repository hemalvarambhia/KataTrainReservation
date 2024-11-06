import java.util.List;

public class TicketOffice {

    private final TrainDataService trainDataService;
    private final BookingReferenceGenerator bookingReferenceGenerator;

    public TicketOffice(TrainDataService trainDataService, BookingReferenceGenerator bookingReferenceGenerator) {
        this.trainDataService = trainDataService;
        this.bookingReferenceGenerator = bookingReferenceGenerator;
    }
    
    public Reservation makeReservation(ReservationRequest request) {
        if(request.seatCount >= 1) {
            List<Seat> availableSeats = trainDataService.availableSeatsOn(request.trainId);
            String bookingReference = availableSeats.isEmpty() ? "" : bookingReferenceGenerator.generate();

            trainDataService.reserve(request.trainId, availableSeats.stream().map(Seat::number).toArray(String[]::new), bookingReference);
            return new Reservation(request.trainId, availableSeats, bookingReference);
        } else {
            return Reservation.none(request.trainId);
        }
    }

}