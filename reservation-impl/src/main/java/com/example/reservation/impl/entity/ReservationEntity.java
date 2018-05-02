package com.example.reservation.impl.entity;

import com.example.reservation.api.Reservation;
import com.example.reservation.impl.entity.ReservationCommand.*;
import com.example.reservation.impl.entity.ReservationEvent.*;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ReservationEntity extends PersistentEntity<ReservationCommand, ReservationEvent, ReservationState> {
    @Override
    public Behavior initialBehavior(Optional<ReservationState> snapshotState) {

        BehaviorBuilder behaviorBuilder = newBehaviorBuilder(snapshotState.orElse(ReservationState.EMPTY));

        behaviorBuilder
            .setReadOnlyCommandHandler(
                GetCurrentReservations.class,
                this::getCurrentReservations
            );

        behaviorBuilder
            .setCommandHandler(
                AddReservation.class,
                this::addReservation
            );

        behaviorBuilder
            .setEventHandler(
                ReservationAdded.class,
                this::reservationAdded
            );

        return behaviorBuilder.build();
    }


    private void getCurrentReservations(GetCurrentReservations cmd, ReadOnlyCommandContext ctx) {
        ctx.reply(state().getReservations());
    }

    private Persist addReservation(AddReservation cmd, CommandContext ctx) {
        ReservationAdded added = new ReservationAdded(
            UUID.randomUUID(),
            UUID.fromString(entityId()),
            cmd.getReservation()
        );
        return ctx.thenPersist(added, (evt) -> ctx.reply(evt));
    }

    private ReservationState reservationAdded(ReservationAdded event) {
        List<Reservation> reservations = state().getReservations();
        reservations.add(event.getReservation());
        return new ReservationState(reservations);
    }


}
