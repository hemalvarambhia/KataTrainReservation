import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.*;
import org.jmock.Mockery;

import java.util.*;
import java.util.stream.Collectors;

public class TicketOfficeTest {
    private TicketOffice ticketOffice;
    private final Mockery context = new JUnit5Mockery();
    private final TrainDataService trainDataService = context.mock(TrainDataService.class);
    private final BookingReferenceGenerator referenceGenerator = context.mock(BookingReferenceGenerator.class);
    @Before
    public void openTicketOffice() {
        ticketOffice = new TicketOffice(trainDataService, referenceGenerator);
    }


    @Test
    public void testReservingNoSeats() {
        context.checking(new Expectations(){{
            never(trainDataService);
            never(referenceGenerator);
        }});
        ReservationRequest request = new ReservationRequest("train-LDN-OXF", 0);

        Reservation actual = ticketOffice.makeReservation(request);

        Assert.assertEquals("train-LDN-OXF", actual.trainId);
        assertNoReservationMade(actual);
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

        Assert.assertEquals("train-LDN-EDB", actual.trainId);
        assertNoReservationMade(actual);
        context.assertIsSatisfied();
    }

    @Test
    public void testReservingASeatOnTrainWithOneCoachThatIsEmpty() {
        context.checking(
                new Expectations() {{
                    List<Seat> freeSeats = Arrays.stream(new Seat[] { new Seat("A", 1) }).collect(Collectors.toList());
                    allowing(trainDataService).availableSeatsOn(with(equal("train-LDN-LIV"))); will(returnValue(freeSeats));
                    allowing(referenceGenerator).generate(); will(returnValue("a booking reference"));

                    oneOf(trainDataService).reserve(
                            with(equal("train-LDN-LIV")),
                            with(new String[]{"A1"}),
                            with(Matchers.any(String.class))
                    ); will(returnValue(true));
        }});

        ReservationRequest singleSeat = new ReservationRequest("train-LDN-LIV", 1);

        Reservation actual = ticketOffice.makeReservation(singleSeat);

        assertReservationMadeOn("train-LDN-LIV", new String[] {"A1"}, actual);
        context.assertIsSatisfied();
    }

    @Test
    public void testReservingASeatOnATrainWithOneCoachThatIsFull() {
        ReservationRequest request = new ReservationRequest("train-LDN-CAM", 1);
        context.checking(new Expectations(){{
            List<Seat> noSeatsAvailable = new ArrayList<>();
            allowing(trainDataService).availableSeatsOn("train-LDN-CAM"); will(returnValue(noSeatsAvailable));

            oneOf(trainDataService).reserve(
                    with(equal("train-LDN-CAM")),
                    with(new String[]{}),
                    with(Matchers.any(String.class))
            ); will(returnValue(true));
            never(referenceGenerator).generate();
        }});

        Reservation reservation = ticketOffice.makeReservation(request);

        assertNoReservationMade(reservation);
        Assert.assertEquals(0, reservation.seatsReserved().length);
        context.assertIsSatisfied();
    }

    @Test
    public void testReservingMoreThanOneSeatOnAnEmptyTrainWithOneCoach() {
        context.checking(
                new Expectations() {{
                    List<Seat> freeSeats = Arrays.stream(
                            new Seat[] { new Seat("A", 1), new Seat("A", 2) }
                    ).collect(Collectors.toList());
                    allowing(trainDataService).availableSeatsOn(with(equal("train-LDN-CAR"))); will(returnValue(freeSeats));
                    allowing(referenceGenerator).generate(); will(returnValue("a booking reference"));

                    oneOf(trainDataService).reserve(
                            with(equal("train-LDN-CAR")),
                            with(new String[]{"A1", "A2"}),
                            with(Matchers.any(String.class))
                    ); will(returnValue(true));
                }});

        ReservationRequest multipleSeats = new ReservationRequest("train-LDN-CAR", 2);

        Reservation actual = ticketOffice.makeReservation(multipleSeats);

        assertReservationMadeOn("train-LDN-CAR", new String[] {"A1", "A2"}, actual);
        context.assertIsSatisfied();
    }

    @Test
    @Ignore("Test list: Booking Seats In Trains With One Coach Where The Reservation Is At Limit")
    public void testBookingSeatsInTrainsWithOneCoachWhereTheReservationIsAtLimit(){

    }

    @Test
    @Ignore("Test list: Booking Seats In Trains With One Coach Where The Reservation Would Lead To Limit Being Exceeded")
    public void testBookingSeatsInTrainsWithOneCoachWhereTheReservationWouldLeadToLimitBeingExceeded(){}

    @Test
    @Ignore("Test list: Booking Seats In Trains With One Coach Where The Reservation Would Hit The Limit")
    public void testBookingSeatsInTrainsWithOneCoachWhereTheReservationWouldHitTheLimit(){}

    private void assertNoReservationMade(Reservation reservation) {
        Assert.assertTrue("Expected no reservation, but got " + reservation.toString(), reservation.nothingBooked());

    }

    private void assertReservationMadeOn(String train, String[] seats, Reservation actual) {
        Assert.assertEquals(train, actual.trainId);
        Assert.assertArrayEquals(seats, actual.seatsReserved());
        Assert.assertFalse("Booking Reference assigned", actual.bookingReference().isEmpty());
    }
}
