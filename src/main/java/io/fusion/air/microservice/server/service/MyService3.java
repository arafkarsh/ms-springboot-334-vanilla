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
public class MyService3 {

    @Autowired
    private EchoService echoService;

    @Autowired
    private EchoSessionService echoSessionService;

    @Autowired
    private EchoAppService echoAppService;

    public void printData() {
        System.out.println("MyService3:Request-Scope: " + Utils.toString(echoService.getEchoData()));
        System.out.println("MyService3:Session-Scope: " + Utils.toString(echoSessionService.getEchoData()));
        System.out.println("MyService3:Apps----Scope: " + Utils.toString(echoAppService.getEchoData()));
    }
}
