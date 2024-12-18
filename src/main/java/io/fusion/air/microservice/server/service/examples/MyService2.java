/**
 * (C) Copyright 2021 Araf Karsh Hamid
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.fusion.air.microservice.server.service.examples;
// Custom
import io.fusion.air.microservice.utils.Std;
import io.fusion.air.microservice.utils.Utils;
import org.springframework.stereotype.Component;

/**
 * @author: Araf Karsh Hamid
 * @version:
 * @date:
 */
@Component
public class MyService2 {

    // Autowired using the constructor
    private EchoService echoService;

    // Autowired using the constructor
    private EchoSessionService echoSessionService;

    // Autowired using the constructor
    private EchoAppService echoAppService;

    /**
     * Autowired using the constructor
     * @param echoService
     * @param echoSessionService
     * @param echoAppService
     */
    public MyService2(EchoService echoService, EchoSessionService echoSessionService,
                      EchoAppService echoAppService ) {
        this.echoService = echoService;
        this.echoSessionService = echoSessionService;
        this.echoAppService = echoAppService;
    }

    public void printData() {
        Std.println("MyService2:Request-Scope: " + Utils.toJsonString(echoService.getEchoData()));
        Std.println("MyService2:Session-Scope: " + Utils.toJsonString(echoSessionService.getEchoData()));
        Std.println("MyService2:Apps----Scope: " + Utils.toJsonString(echoAppService.getEchoData()));
    }
}
