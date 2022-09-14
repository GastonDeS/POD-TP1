package server.src.test.java.ar.edu.itba.pod.services;

import ar.edu.itba.pod.server.services.FlightsAdminService;
import ar.edu.itba.pod.server.services.SeatsAssignmentService;
import ar.edu.itba.pod.services.utils.TestUtils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ConcurrencyTest {
    private final FlightsAdminService flightsAdminService = FlightsAdminService.getInstance();
    private final SeatsAssignmentService seatsAssignmentService = SeatsAssignmentService.getInstance();
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

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
                    flightsAdminService.findNewSeatsForCancelledFlights();
                } catch (Exception ex) {
                    Assertions.fail("fail");
                }
            });
            newThread.start();
            threadList.add(newThread);
        }

        threadList.forEach(thread -> {
            try {
                 thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }


}
