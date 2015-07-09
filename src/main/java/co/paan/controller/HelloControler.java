package co.paan.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@RestController
public class HelloControler {

    @RequestMapping("/health")
    public String health(@RequestParam(value="name", defaultValue="World") String name) {
        return "OK";
    }
}
