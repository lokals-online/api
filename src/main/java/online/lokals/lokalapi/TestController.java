package online.lokals.lokalapi;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/lokal-test")
    public String test() {
        return "test";
    }

}
