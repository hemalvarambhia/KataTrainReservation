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
    @Before
    public void openTicketOffice() {
        ticketOffice = new TicketOffice(trainDataService);
    }


    @Test
    public void testReservingNoSeats() {
        ReservationRequest request = new ReservationRequest("train-LDN-OXF", 0);

        Reservation actual = ticketOffice.makeReservation(request);

        Assert.assertEquals("train-LDN-OXF", actual.trainId);
        assertNoReservationMade(actual);
    }

    @Test
    public void testReservingNoSeatsOnAnyTrain() {
        ReservationRequest singleSeat = new ReservationRequest("train-LDN-EDB", 0);

        Reservation actual = ticketOffice.makeReservation(singleSeat);

        Assert.assertEquals("train-LDN-EDB", actual.trainId);
        assertNoReservationMade(actual);
    }

    @Test
    public void testReservingASeatOnTrainWithOneCoachThatIsEmpty() {
        context.checking(
                new Expectations() {{
                    List<Seat> freeSeats = Arrays.stream(new Seat[] { new Seat("A", 1) }).collect(Collectors.toList());
                    allowing(trainDataService).availableSeatsOn(with(equal("train-LDN-LIV"))); will(returnValue(freeSeats));

                    oneOf(trainDataService).reserve(
                            with(equal("train-LDN-LIV")),
                            with(new String[]{"A1"}),
                            with(Matchers.any(String.class))
                    ); will(returnValue(true));
        }});

        ReservationRequest singleSeat = new ReservationRequest("train-LDN-LIV", 1);

        Reservation actual = ticketOffice.makeReservation(singleSeat);

        Assert.assertEquals("train-LDN-LIV", actual.trainId);
        Assert.assertArrayEquals(new String[] {"A1"}, actual.seatsReserved());
        Assert.assertFalse("Booking Reference assigned", actual.bookingReference().isEmpty());
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
        }});

        Reservation reservation = ticketOffice.makeReservation(request);

        assertNoReservationMade(reservation);
        Assert.assertEquals(0, reservation.seatsReserved().length);
        context.assertIsSatisfied();
    }

    @Test
    @Ignore("TODO - booking more than one seat in a empty train")
    public void testReservingMoreThanOneSeatOnAnEmptyTrainWithOneCoach() {}

    private void assertNoReservationMade(Reservation reservation) {
        Assert.assertTrue("Expected no reservation, but got " + reservation.toString(), reservation.nothingBooked());

    }
}
