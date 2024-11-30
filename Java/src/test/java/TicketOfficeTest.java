import org.jmock.Expectations;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.*;
import org.jmock.Mockery;

import java.util.*;

public class TicketOfficeTest {
    private TicketOffice ticketOffice;
    private final Mockery context = new JUnit5Mockery();
    private final TrainDataService trainDataService = context.mock(TrainDataService.class);
    private final BookingReferenceGenerator referenceGenerator = context.mock(BookingReferenceGenerator.class);
    private final ReservationPolicy reservationPolicy = context.mock(ReservationPolicy.class);

    @Before
    public void openTicketOffice() {
        ticketOffice = new TicketOffice(trainDataService, referenceGenerator, reservationPolicy);
    }

    @Test
    public void testReservingNoSeats() {
        context.checking(new Expectations(){{
            never(trainDataService);
            never(referenceGenerator);
        }});
        ReservationRequest request = new ReservationRequest("train-LDN-OXF", 0);

        Reservation actual = ticketOffice.makeReservation(request);

        assertNoReservationWasMadeOn("train-LDN-OXF", actual);
        context.assertIsSatisfied();
    }

    @Test
    public void testReservingNoSeatsOnAnyTrain() {
        context.checking(new Expectations(){{
            never(trainDataService);
            never(referenceGenerator);
        }});
        ReservationRequest singleSeat = new ReservationRequest("train-LDN-EDB", 0);

        Reservation actual = ticketOffice.makeReservation(singleSeat);

        assertNoReservationWasMadeOn("train-LDN-EDB", actual);
        context.assertIsSatisfied();
    }

    @Test
    public void testASeatCanBeReservedOnAnEmptyTrainWithOneCoach() {
        ReservationRequest singleSeat = new ReservationRequest("train-LDN-LIV", 1);
        context.checking(
                new Expectations() {{
                    List<Seat> freeSeats = seats("A1");
                    allowing(trainDataService).availableSeatsOn(with(equal("train-LDN-LIV"))); will(returnValue(freeSeats));
                    allowing(referenceGenerator).generate(); will(returnValue("a booking reference"));
                    allowing(reservationPolicy).isSatisfiedBy(singleSeat); will(returnValue(true));
                    oneOf(trainDataService).reserve(
                            with(equal("train-LDN-LIV")),
                            with(new String[]{"A1"}),
                            with("a booking reference")
                    ); will(returnValue(true));
        }});

        Reservation actual = ticketOffice.makeReservation(singleSeat);

        assertReservationMadeOn("train-LDN-LIV", new String[] {"A1"}, actual);
        context.assertIsSatisfied();
    }

    @Test
    public void testOnlyNumberOfSeatsRequestedAreBooked() {
        ReservationRequest twoSeats = new ReservationRequest("train-LDN-LIV", 2);
        context.checking( new Expectations(){{
            allowing(trainDataService).availableSeatsOn(with(equal("train-LDN-LIV"))); will(returnValue(seats("A1", "A2", "A3")));
            allowing(referenceGenerator).generate(); will(returnValue("a booking reference"));
            allowing(reservationPolicy).isSatisfiedBy(twoSeats); will(returnValue(true));
            oneOf(trainDataService).reserve(
                    with(equal("train-LDN-LIV")),
                    with(new String[]{"A1", "A2"}),
                    with("a booking reference")
            ); will(returnValue(true));
        }}
        );

        Reservation actual = ticketOffice.makeReservation(twoSeats);

        assertReservationMadeOn("train-LDN-LIV", new String[] {"A1", "A2"}, actual);
        context.assertIsSatisfied();
    }

    @Test
    public void testMoreThanOneSeatCanBeReservedOnAnEmptyTrainWithOneCoach() {
        ReservationRequest multipleSeats = new ReservationRequest("train-LDN-CAR", 2);
        context.checking(
                new Expectations() {{
                    List<Seat> freeSeats = seats("A1", "A2");
                    allowing(trainDataService).availableSeatsOn(with(equal("train-LDN-CAR"))); will(returnValue(freeSeats));
                    allowing(referenceGenerator).generate(); will(returnValue("a booking reference"));
                    allowing(reservationPolicy).isSatisfiedBy(multipleSeats); will(returnValue(true));
                    oneOf(trainDataService).reserve(
                            with(equal("train-LDN-CAR")),
                            with(new String[]{"A1", "A2"}),
                            with(equal("a booking reference"))
                    ); will(returnValue(true));
                }});

        Reservation actual = ticketOffice.makeReservation(multipleSeats);

        assertReservationMadeOn("train-LDN-CAR", new String[] {"A1", "A2"}, actual);
        context.assertIsSatisfied();
    }

    @Test
    public void testNoSeatsCanBeReservedOnATrainWithOneCoachThatIsFull() {
        ReservationRequest request = new ReservationRequest("train-LDN-CAM", 1);
        context.checking(new Expectations(){{
            allowing(trainDataService).availableSeatsOn("train-LDN-CAM"); will(returnValue(new ArrayList<Seat>()));
            never(referenceGenerator);
            never(trainDataService).reserve(
                    with(equal("train-LDN-CAM")),
                    with(equal(new String[]{})),
                    with(equal(""))
            ); will(returnValue(true));
        }});

        Reservation reservation = ticketOffice.makeReservation(request);

        assertNoReservationWasMadeOn("train-LDN-CAM", reservation);
        context.assertIsSatisfied();
    }

    @Test
    public void testBookingSeatsInTrainsWithOneCoachWhereTheReservationWouldLeadToLimitBeingExceeded(){
        ReservationRequest reservationRequest = new ReservationRequest("train-LIV-NOR", 1);

        context.checking(
                new Expectations() {{
                    List<Seat> freeSeats = seats("A1", "A2");
                    allowing(trainDataService).availableSeatsOn(with(equal("train-LIV-NOR"))); will(returnValue(freeSeats));
                    allowing(referenceGenerator).generate(); will(returnValue("a booking reference"));
                    allowing(reservationPolicy).isSatisfiedBy(reservationRequest); will(returnValue(false));
                    never(trainDataService).reserve(with(equal("train-LIV-NOR")), with(any(String[].class)), with(any(String.class))); will(returnValue(true));
        }}
        );

        Reservation reservation = ticketOffice.makeReservation(reservationRequest);

        assertNoReservationWasMadeOn("train-LIV-NOR", reservation);
        context.assertIsSatisfied();
    }

    @Test
    public void testBookingSeatsInTrainsWithOneCoachWhereTheReservationWouldHitTheLimit(){
        ReservationRequest reservationRequest = new ReservationRequest("train-MAN-CAR", 1);

        context.checking(new Expectations() {{
            List<Seat> freeSeat = seats("A1");
            allowing(trainDataService).availableSeatsOn(with(equal("train-MAN-CAR"))); will(returnValue(freeSeat));
            never(referenceGenerator).generate();
            allowing(reservationPolicy).isSatisfiedBy(reservationRequest); will(returnValue(false));
            never(trainDataService).reserve(with(equal("train-MAN-CAR")), with(any(String[].class)), with(any(String.class))); will(returnValue(true));
        }});

        Reservation reservation = ticketOffice.makeReservation(reservationRequest);

        assertNoReservationWasMadeOn("train-MAN-CAR", reservation);
        context.assertIsSatisfied();
    }

    private List<Seat> seats(String... seatNumbers) {
        return Seat.with(seatNumbers);
    }

    private void assertNoReservationWasMadeOn(String expectedTrain, Reservation reservation) {
        Assert.assertEquals(expectedTrain, reservation.trainId);
        Assert.assertTrue("Expected no reservation, but got " + reservation, reservation.nothingBooked());
    }

    private void assertReservationMadeOn(String train, String[] seats, Reservation actual) {
        Assert.assertEquals(train, actual.trainId);
        Assert.assertArrayEquals(seats, actual.seatsReserved());
        Assert.assertFalse("Booking Reference assigned", actual.bookingReference().isEmpty());
    }
}
