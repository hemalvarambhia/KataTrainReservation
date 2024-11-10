import java.util.List;

public class TicketOffice {

    private final TrainDataService trainDataService;
    private final BookingReferenceGenerator bookingReferenceGenerator;

    public TicketOffice(TrainDataService trainDataService, BookingReferenceGenerator bookingReferenceGenerator) {
        this.trainDataService = trainDataService;
        this.bookingReferenceGenerator = bookingReferenceGenerator;
    }
    
    public Reservation makeReservation(ReservationRequest request) {
        String train = request.trainId;
        if(request.seatCount >= 1) {
            List<Seat> availableSeats = trainDataService.availableSeatsOn(train);
            if(availableSeats.isEmpty()) {
                return Reservation.none(train);
            }

            String bookingReference = reserveSeatsOn(train, availableSeats);
            return new Reservation(request.trainId, availableSeats, bookingReference);
        } else {
            return Reservation.none(request.trainId);
        }
    }

    private String reserveSeatsOn(String train, List<Seat> seatsToReserve) {
        String bookingReference = bookingReferenceGenerator.generate();
        String[] seatNumbers = seatsToReserve.stream().map(Seat::number).toArray(String[]::new);
        trainDataService.reserve(train, seatNumbers, bookingReference);
        return bookingReference;
    }
}