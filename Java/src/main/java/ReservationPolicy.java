public interface ReservationPolicy {
    boolean policyMet(ReservationRequest reservationRequest);
}
