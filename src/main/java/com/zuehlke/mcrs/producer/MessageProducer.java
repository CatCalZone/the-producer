package com.zuehlke.mcrs.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import reactor.bus.Event;
import reactor.bus.EventBus;

import javax.inject.Named;

import java.util.concurrent.CountDownLatch;

import lombok.extern.java.Log;

/**
 * Created by dea@zuehlke.com on 08.04.15.
 */
@Service
@Log
public class MessageProducer {

    @Autowired
    EventBus eventBus;

    @Autowired
    @Named("internalAppointmentQueue")
    private String appointmentQueue;


    @Autowired
    private AppointmentCreator messageCreator;

    @Autowired
    CountDownLatch latch;

    @Async
    public void createMessages()  {
        int index = 0;
        while(index++ < TheProducer.RUNS) {
        	final AppointmentRequest request = messageCreator.newMessage();
            eventBus.notify(appointmentQueue, Event.wrap(request));
            log.info(String.format("Notified message from %s", request.getRequestUser()));
            
            try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
