package com.example.reservation.impl.entity;

import com.example.reservation.api.Reservation;
import com.lightbend.lagom.serialization.Jsonable;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;


@Value
public class ReservationState implements Jsonable {

    public static transient final ReservationState EMPTY = new ReservationState(new ArrayList<>());

    List<Reservation> reservations;

}
