package io.fusion.air.microservice.server.service;

import com.jayway.jsonpath.internal.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author: Araf Karsh Hamid
 * @version:
 * @date:
 */
@Component
public class MyService2 {

    @Autowired
    private EchoService echoService;

    @Autowired
    private EchoSessionService echoSessionService;

    @Autowired
    private EchoAppService echoAppService;

    public void printData() {
        System.out.println("MyService2:Request-Scope: " + Utils.toString(echoService.getEchoData()));
        System.out.println("MyService2:Session-Scope: " + Utils.toString(echoSessionService.getEchoData()));
        System.out.println("MyService2:Apps----Scope: " + Utils.toString(echoAppService.getEchoData()));
    }
}
