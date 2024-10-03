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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


import io.fusion.air.microservice.domain.models.order.PaymentDetails;
import io.fusion.air.microservice.domain.models.order.PaymentStatus;
import io.fusion.air.microservice.server.config.ServiceConfiguration;
import io.fusion.air.microservice.server.models.EchoData;
import io.fusion.air.microservice.server.models.EchoResponseData;
import io.fusion.air.microservice.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

/***
 *
 * @author arafkarsh
 *
 */
/**/
@Service
public class AppExternalServiceImpl {

    @Autowired
    private ServiceConfiguration serviceConfig;

    private String payments 	= "/payments";
    private String remoteEcho 	= "/service/echo";

    private String gwBaseURL;
    private String paymentURL;
    private String echoURL;

    private boolean urlsSet = false;

    // @Autowired
    private RestClientService restClient;

    /**
     * Only for Testing outside SpringBoot Context
     */
    public AppExternalServiceImpl() {
    }

    /**
     * Only for Testing outside SpringBoot Context
     * Set the Payment GateWay
     */
    public AppExternalServiceImpl(String _host, int _port) {
        System.out.println(LocalDateTime.now()+"|PaymentGW Constructor(host,port) ...");
        serviceConfig = new ServiceConfiguration(_host, _port);
        restClient = new RestClientService();
        setURLs();
    }

    /**
     * Set the Base URLs
     */
    private void setURLs() {
        if(!urlsSet) {
            if(serviceConfig != null) {
                String apiPath = (serviceConfig.getServiceApiPath() != null) ? serviceConfig.getServiceApiPath() : "/ms-cache/api/v1";
                gwBaseURL = "http://" + serviceConfig.getRemoteHost()
                        + ":" + serviceConfig.getRemotePort()
                        + apiPath;
            } else {
                System.out.println("INIT ERR|> Service Configuration NOT Available!!");
                gwBaseURL = "http://localhost:8080";

            }
            paymentURL = gwBaseURL + payments;
            echoURL = gwBaseURL + remoteEcho;
            urlsSet = true;
            // System.out.println("INIT    |> PaymentGateway Service Initialize.");
            // System.out.println("REMOTE  |> "+paymentURL+"/");
            // System.out.println("REMOTE  |> "+echoURL+"/");
        }
    }

    /**
     * Do a Remote Echo - For Testing Purpose ONLY
     * @param _word
     * @return
     */
    public EchoResponseData remoteEcho(String _word) {
        setURLs();
        System.out.println("REQUEST |> "+Utils.toJsonString(_word));
        EchoResponseData erd = restClient.getForObject(echoURL +"/"+ _word,  EchoResponseData.class);
        System.out.println("RESPONSE|> "+Utils.toJsonString(erd));
        return erd;
    }

    /**
     * Remote Echo - For Testing Purpose ONLY
     * @param _word
     * @return
     */
    public EchoResponseData remoteEcho(EchoData _word) {
        setURLs();
        return remoteEcho(echoURL, _word);
    }

    /**
     * Do a Remote Echo - For Testing Purpose ONLY
     * @param _word
     * @return
     */
    public EchoResponseData remoteEcho(String url, EchoData _word) {
        // Set Headers
        HttpHeaders headers = getHeadersWithCookies();
        HttpEntity<EchoData> request = new HttpEntity<EchoData>(_word, headers);
        System.out.println("REQUEST  2|> "+Utils.toJsonString(request));
        System.out.println(Utils.createCurlCommand("POST", url, headers, _word));
        // Call Remote Service > POST
        EchoResponseData erd = restClient.postForObject(url, request, EchoResponseData.class);
        System.out.println("RESPONSE 3|> "+Utils.toJsonString(erd));
        return erd;
    }

    public EchoResponseData remoteEchoGET(String url, EchoData _word) {
        // Set Headers
        HttpHeaders headers = getHeadersWithCookies();
        HttpEntity<EchoData> request = new HttpEntity<EchoData>(_word, headers);
        System.out.println("REQUEST  2|> "+Utils.toJsonString(request));
        System.out.println(Utils.createCurlCommand("POST", url, headers, _word));
        // Call Remote Service > POST
        EchoResponseData erd = restClient.getForObject(url, EchoResponseData.class);
        System.out.println("RESPONSE 3|> "+Utils.toJsonString(erd));
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
        headers.add("sessionId", UUID.randomUUID().toString());
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
        headers.add("sessionId", UUID.randomUUID().toString());
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
     * @param _paymentDetails
     * @return
     */
    public PaymentStatus processPayments(PaymentDetails _paymentDetails) {
        setURLs();
        System.out.println("REQUEST |> "+Utils.toJsonString(_paymentDetails));
        // Set Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("sessionId", UUID.randomUUID().toString());
        headers.add("app", "bigBasket");

        // List<String> cookies = new ArrayList<>();
        // cookies.add("token="+UUID.randomUUID().toString());
        // cookies.add("domain=arafkarsh.com");
        // headers.put(HttpHeaders.COOKIE, cookies);
        // HttpEntity<PaymentDetails> request = new HttpEntity<PaymentDetails>
        // (_paymentDetails, null);

        HttpEntity<PaymentDetails> request = new HttpEntity<PaymentDetails>(_paymentDetails, headers);
        System.out.println("REQUEST |> "+Utils.toJsonString(request));
        // Call Remote Service > POST
        PaymentStatus ps = restClient.postForObject(paymentURL, request, PaymentStatus.class);
        System.out.println("RESPONSE|> "+Utils.toJsonString(ps));
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

