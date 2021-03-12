package io.turntabl.clientconnectivity.resources;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/client")
public class ClientConnectivity {
    @RequestMapping("/all")
    public String getMessage(){
        return "hello world";
    }
}
