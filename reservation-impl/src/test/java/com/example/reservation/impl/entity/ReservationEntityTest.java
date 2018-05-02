package com.example.reservation.impl.entity;

import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;
import com.example.reservation.api.Reservation;
import com.lightbend.lagom.javadsl.testkit.PersistentEntityTestDriver;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 *
 */
public class ReservationEntityTest {
    static ActorSystem system;

    @BeforeClass
    public static void setup() {
        system = ActorSystem.create();
    }

    @AfterClass
    public static void teardown() {
        TestKit.shutdownActorSystem(system);
        system = null;
    }

    @Test
    public void testMakeReservation() {
        String listingId = UUID.randomUUID().toString();

        PersistentEntityTestDriver<ReservationCommand, ReservationEvent, ReservationState> driver = new
            PersistentEntityTestDriver<>(system, new ReservationEntity(), listingId);

        Reservation r = new Reservation(LocalDate.now().plusDays(1L), LocalDate.now().plusDays(5L));

        PersistentEntityTestDriver.Outcome<ReservationEvent, ReservationState> outcome = driver.run(new ReservationCommand.AddReservation(r));

        Assert.assertEquals(outcome.events().size(), 1) ;
        ReservationEvent.ReservationAdded reservationAdded = (ReservationEvent.ReservationAdded) outcome.events().get(0);
        Assert.assertEquals(reservationAdded.getListingId().toString(), listingId) ;
        Assert.assertEquals(reservationAdded.getReservation(), r) ;

        Assert.assertEquals(outcome.getReplies().size(), 1);

        List<PersistentEntityTestDriver.Issue> issues = driver.getAllIssues();
        if (issues.size() > 0) {
            issues.stream().forEach(System.out::println);
        }
        Assert.assertEquals(0, issues.size());


    }

}
