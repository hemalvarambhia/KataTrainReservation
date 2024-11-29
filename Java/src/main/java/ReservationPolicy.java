public interface ReservationPolicy {
    boolean isSatisfiedBy(ReservationRequest reservationRequest);
}
