package com.grape.ticketing.web.nav;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class QueuePageController {

    @GetMapping("/waitingQueue")
    public String waitingQueuePage() {
        return "waiting_queue/waitingQueue";
    }
}
