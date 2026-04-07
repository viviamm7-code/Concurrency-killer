package com.grape.ticketing.web.nav;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@Slf4j
public class TicketingController {
    @RequestMapping("/")
    public String Ticketing() {
        log.info("ticketing Controller");
        return "ticketing";
    }

}
