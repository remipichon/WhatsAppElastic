package co.paan.service.impl;

import co.paan.domain.Greeting;
import co.paan.service.GreetingService;
import org.springframework.stereotype.Service;

/**
 * Created by remi on 04/07/15.
 */
@Service
public class GreetingServiceImpl implements GreetingService {
    @Override
    public Greeting getGreeting() {
        return new Greeting(12,"pouet");
    }
}
