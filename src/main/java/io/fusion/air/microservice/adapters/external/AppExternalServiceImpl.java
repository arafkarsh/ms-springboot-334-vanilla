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
package io.fusion.air.microservice.adapters.external;
// Java
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
// Custom
import io.fusion.air.microservice.domain.models.order.PaymentDetails;
import io.fusion.air.microservice.domain.models.order.PaymentStatus;
import io.fusion.air.microservice.server.config.ServiceConfiguration;
import io.fusion.air.microservice.server.models.EchoData;
import io.fusion.air.microservice.server.models.EchoResponseData;
import io.fusion.air.microservice.utils.Utils;
// Spring
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

/***
 *
 * @author arafkarsh
 *
 */
/**/
@Service
public class AppExternalServiceImpl {

    // Set Logger -> Lookup will automatically determine the class name.
    private static final Logger log = getLogger(lookup().lookupClass());

    // Autowired using the Constructor
    private final ServiceConfiguration serviceConfig;

    private String payments 	= "/payments";
    private String remoteEcho 	= "/service/echo";

    private static final String REQUEST = "REQUEST";
    private static final String RESPONSE = "RESPONSE";
    private static final String SESSIONID = "sessionId";

    private String paymentURL;
    private String echoURL;

    private boolean urlsSet = false;

    // @Autowired
    private RestClientService restClient;

    /**
     * Autowired using the Constructor
     * @param serviceCfg
     */
    @Autowired
    public AppExternalServiceImpl(ServiceConfiguration serviceCfg) {
        serviceConfig = serviceCfg;
    }

    /**
     * Only for Testing outside SpringBoot Context
     * Set the Payment GateWay
     */
    public AppExternalServiceImpl(String host, int port) {
        log.info("{} |PaymentGW Constructor(host,port) ...", LocalDateTime.now());
        serviceConfig = new ServiceConfiguration(host, port);
        restClient = new RestClientService();
        setURLs();
    }

    /**
     * Set the Base URLs
     */
    private void setURLs() {
        String gwBaseURL = "";
        if(!urlsSet) {
            if(serviceConfig != null) {
                String apiPath = (serviceConfig.getServiceApiPath() != null) ? serviceConfig.getServiceApiPath() : "/ms-cache/api/v1";
                gwBaseURL = "http://" + serviceConfig.getRemoteHost()
                        + ":" + serviceConfig.getRemotePort()
                        + apiPath;
            } else {
                log.info("INIT ERR|> Service Configuration NOT Available!!");
                gwBaseURL = "http://localhost:8080";

            }
            paymentURL = gwBaseURL + payments;
            echoURL = gwBaseURL + remoteEcho;
            urlsSet = true;
        }
    }

    /**
     * Do a Remote Echo - For Testing Purpose ONLY
     * @param word
     * @return
     */
    public EchoResponseData remoteEcho(String word) {
        setURLs();
        String w = Utils.toJsonString(word);
        log.info("{}|> {}", REQUEST, w);
        EchoResponseData erd = restClient.getForObject(echoURL +"/"+ word,  EchoResponseData.class);
        String e = Utils.toJsonString(erd);
        log.info("{}|> {}", RESPONSE, e);
        return erd;
    }

    /**
     * Remote Echo - For Testing Purpose ONLY
     * @param word
     * @return
     */
    public EchoResponseData remoteEcho(EchoData word) {
        setURLs();
        return remoteEcho(echoURL, word);
    }

    /**
     * Do a Remote Echo - For Testing Purpose ONLY
     * @param word
     * @return
     */
    public EchoResponseData remoteEcho(String url, EchoData word) {
        // Set Headers
        HttpHeaders headers = getHeadersWithCookies();
        HttpEntity<EchoData> request = new HttpEntity<>(word, headers);
        String s =  Utils.toJsonString(request);
        log.info("{} 2|> {}", REQUEST, s);
        String c = Utils.createCurlCommand("POST", url, headers, word);
        log.info(c);
        // Call Remote Service > POST
        EchoResponseData erd = restClient.postForObject(url, request, EchoResponseData.class);
        String e = Utils.toJsonString(erd);
        log.info("{} 3|> {}", RESPONSE, e);
        return erd;
    }

    public EchoResponseData remoteEchoGET(String url, EchoData word) {
        // Set Headers
        HttpHeaders headers = getHeadersWithCookies();
        HttpEntity<EchoData> request = new HttpEntity<>(word, headers);
        String s =  Utils.toJsonString(request);
        log.info("{} 2|> {}", REQUEST, s);
        String c = Utils.createCurlCommand("POST", url, headers, word);
        log.info(c);
        // Call Remote Service > POST
        EchoResponseData erd = restClient.getForObject(url, EchoResponseData.class);
        String e = Utils.toJsonString(erd);
        log.info("{} 3|> {}", RESPONSE, e);
        return erd;
    }

    /**
     * Returns headers
     * @return
     */
    public static HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("accept", "application/json");
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(SESSIONID, UUID.randomUUID().toString());
        headers.add("app", "MS-Cache");
        return headers;
    }

    /**
     * Returns headers with Cookies
     * @return
     */
    public static  HttpHeaders getHeadersWithCookies() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("accept", "application/json");
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(SESSIONID, UUID.randomUUID().toString());
        headers.add("app", "MS-Cache");
        headers.put(HttpHeaders.COOKIE, getCookies() );
        return headers;
    }

    /**
     * Returns Cookies
     */
    public static  List<String> getCookies() {
        List<String> cookies = new ArrayList<>();
        cookies.add("token="+UUID.randomUUID().toString());
        cookies.add("domain=arafkarsh.com");
        return cookies;
    }

    /**
     * Process Payments
     *
     * @param paymentDetails
     * @return
     */
    public PaymentStatus processPayments(PaymentDetails paymentDetails) {
        setURLs();
        String p = Utils.toJsonString(paymentDetails);
        log.info("{}} 1|> {}",REQUEST, p);
        // Set Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(SESSIONID, UUID.randomUUID().toString());
        headers.add("app", "bigBasket");

        HttpEntity<PaymentDetails> request = new HttpEntity<>(paymentDetails, headers);
        String r = Utils.toJsonString(request);
        log.info("{}} 2|> {}",REQUEST, r);
        // Call Remote Service > POST
        PaymentStatus ps = restClient.postForObject(paymentURL, request, PaymentStatus.class);
        String p2 = Utils.toJsonString(ps);
        log.info("{}} 3|> {}",RESPONSE, p2);
        return ps;
    }

    /**
     * Test the External Client
     * @param args
     */
    public static void main(String[] args) {
        AppExternalServiceImpl extService = new AppExternalServiceImpl("localhost", 9090);
        String urlEcho = "http://localhost:9090/ms-cache/api/v1/service/echo/";
        extService.remoteEcho(urlEcho, new EchoData("John Doe"));
    }
}

