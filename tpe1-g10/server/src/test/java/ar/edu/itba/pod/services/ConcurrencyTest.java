package server.src.test.java.ar.edu.itba.pod.services;

import ar.edu.itba.pod.server.services.FlightsAdminService;
import ar.edu.itba.pod.server.services.SeatsAssignmentService;
import ar.edu.itba.pod.services.utils.TestUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ConcurrencyTest {
    private final FlightsAdminService flightsAdminService = FlightsAdminService.getInstance();
    private final SeatsAssignmentService seatsAssignmentService = SeatsAssignmentService.getInstance();

    @BeforeEach
    public void restartService() {
        flightsAdminService.restart();
    }

    @Test
    public void test() throws RemoteException {
        TestUtils.fillAdminService(flightsAdminService);
        TestUtils.assignAlotOfSeats(seatsAssignmentService);

        for (int i = 0; i < 25; i++) {
            flightsAdminService.cancelPendingFlight("AA"+i);
        }



        List<Thread> threadList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {

            Thread newThread = new Thread(() -> {
                try {
                    ar.edu.itba.pod.models.ChangedTicketsDto changedTicketsDto = flightsAdminService.findNewSeatsForCancelledFlights();
                    System.out.println(changedTicketsDto.getTicketsChangedAmount());
                } catch (Exception ex) {
                    System.out.println("fail");
                }
            });
            newThread.start();
            threadList.add(newThread);
        }

        AtomicBoolean breakCond = new AtomicBoolean(true);
        while (breakCond.get()) {
            System.out.println("test passed");
            threadList.forEach(thread -> {
                try {
                     thread.join();
                     breakCond.set(false);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }


    }


}
