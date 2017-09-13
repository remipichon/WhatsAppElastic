package co.paan.controller;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class HelloControler {

    @RequestMapping("/health")
    public String health(@RequestParam(value="name", defaultValue="World") String name) {
        return "OKok";
    }

}
