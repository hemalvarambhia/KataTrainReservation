import java.util.List;
import java.util.stream.Collectors;

public class TicketOffice {

    private final TrainDataService trainDataService;
    private final BookingReferenceGenerator bookingReferenceGenerator;
    private final ReservationPolicy reservationPolicy;

    public TicketOffice(
            TrainDataService trainDataService,
            BookingReferenceGenerator bookingReferenceGenerator, ReservationPolicy policy) {
        this.trainDataService = trainDataService;
        this.reservationPolicy = policy;
        this.bookingReferenceGenerator = bookingReferenceGenerator;
    }
    
    public Reservation makeReservation(ReservationRequest request) {
        String train = request.trainId;

        if(request.noSeatsRequested()) { return Reservation.none(train); }

        Coach coach = trainDataService.firstCoach(train);
        List<Seat> availableSeats = coach.getSeats();
        if(coach.fullyBooked()) { return Reservation.none(train); }

        if(!reservationPolicy.isSatisfiedBy(request)) { return Reservation.none(train); }
        List<Seat> seatsToBook = limit(availableSeats, request.numberOfSeatsToBook());
        String bookingReference = reserveSeatsOn(train, seatsToBook);
        return new Reservation(train, seatsToBook, bookingReference);
    }

    private List<Seat> limit(List<Seat> availableSeats, int numberOfSeats) {
        return availableSeats.stream().limit(numberOfSeats).collect(Collectors.toList());
    }

    private String reserveSeatsOn(String train, List<Seat> seatsToReserve) {
        String bookingReference = bookingReferenceGenerator.generate();
        String[] seatNumbers = seatsToReserve.stream().map(Seat::number).toArray(String[]::new);
        trainDataService.reserve(train, seatNumbers, bookingReference);
        return bookingReference;
    }
}