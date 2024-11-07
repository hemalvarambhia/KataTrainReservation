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
            if(availableSeats.isEmpty()) {
                return Reservation.none(request.trainId);
            }

            String[] seatNumbers = availableSeats.stream().map(Seat::number).toArray(String[]::new);
            trainDataService.reserve(request.trainId, seatNumbers, bookingReference);
            return new Reservation(request.trainId, availableSeats, bookingReference);
        } else {
            return Reservation.none(request.trainId);
        }
    }

}