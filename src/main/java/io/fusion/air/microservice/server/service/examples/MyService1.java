package io.fusion.air.microservice.server.service.examples;

import io.fusion.air.microservice.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author: Araf Karsh Hamid
 * @version:
 * @date:
 */
@Component
public class MyService1 {

    @Autowired
    private EchoService echoService;

    @Autowired
    private EchoSessionService echoSessionService;

    @Autowired
    private EchoAppService echoAppService;

    public void printData() {
        System.out.println("MyService1:Request-Scope: " + Utils.toJsonString(echoService.getEchoData()));
        System.out.println("MyService1:Session-Scope: " + Utils.toJsonString(echoSessionService.getEchoData()));
        System.out.println("MyService1:Apps----Scope: " + Utils.toJsonString(echoAppService.getEchoData()));
    }
}
