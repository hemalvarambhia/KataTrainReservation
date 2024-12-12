import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class WhenSeatsCannotBeReservedOnATrainTest {
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
    public void testNoSeatsAreReservedOnATrainWithOneCoachThatIsFull() {
        ReservationRequest request = new ReservationRequest("train-LDN-CAM", 1);
        context.checking(new Expectations(){{
            allowing(reservationPolicy).isSatisfiedBy(request); will(returnValue(false));
            allowing(trainDataService).availableSeatsOn("train-LDN-CAM"); will(returnValue(new ArrayList<Seat>()));
            never(referenceGenerator).generate();
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
    public void testNoSeatsAreReservedOnATrainsWithOneCoachWhereTheReservationWouldLeadToLimitBeingExceeded(){
        ReservationRequest reservationRequest = new ReservationRequest("train-LIV-NOR", 1);

        context.checking(
                new Expectations() {{
                    allowing(reservationPolicy).isSatisfiedBy(reservationRequest); will(returnValue(false));
                    allowing(trainDataService).availableSeatsOn(with(equal("train-LIV-NOR"))); will(returnValue(Seat.numbers("A1", "A2")));
                    never(referenceGenerator).generate();
                    never(trainDataService).reserve(with(equal("train-LIV-NOR")), with(any(String[].class)), with(any(String.class))); will(returnValue(true));
                }}
        );

        Reservation reservation = ticketOffice.makeReservation(reservationRequest);

        assertNoReservationWasMadeOn("train-LIV-NOR", reservation);
        context.assertIsSatisfied();
    }

    @Test
    public void testNoSeatsAreReservedOnATrainsWithOneCoachWhereTheReservationWouldHitTheLimit(){
        ReservationRequest reservationRequest = new ReservationRequest("train-MAN-CAR", 1);

        context.checking(new Expectations() {{
            allowing(reservationPolicy).isSatisfiedBy(reservationRequest); will(returnValue(false));
            allowing(trainDataService).availableSeatsOn(with(equal("train-MAN-CAR"))); will(returnValue(Seat.numbers("A1")));
            never(referenceGenerator).generate();
            never(trainDataService).reserve(with(equal("train-MAN-CAR")), with(any(String[].class)), with(any(String.class))); will(returnValue(true));
        }});

        Reservation reservation = ticketOffice.makeReservation(reservationRequest);

        assertNoReservationWasMadeOn("train-MAN-CAR", reservation);
        context.assertIsSatisfied();
    }

    private void assertNoReservationWasMadeOn(String expectedTrain, Reservation reservation) {
        Assert.assertEquals(expectedTrain, reservation.trainId);
        Assert.assertTrue("Expected no reservation, but got " + reservation, reservation.nothingBooked());
    }
}
