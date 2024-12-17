/**
 * (C) Copyright 2022 Araf Karsh Hamid
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
package io.fusion.air.microservice.domain.models.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.fusion.air.microservice.utils.DateJsonSerializer;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: Araf Karsh Hamid
 * @version:
 * @date:
 */
@JsonPropertyOrder({  "time", "success", "code" , "msg", "payload"})
public abstract class AbstractResponse implements Serializable {

    @JsonProperty("time")
    @JsonSerialize(using = DateJsonSerializer.class)
    private LocalDateTime requestTime = LocalDateTime.now();

    @JsonProperty("success")
    private boolean success = false;
    @JsonProperty("code")
    private String code = "ERROR";
    @JsonProperty("msg")
    private String description = "Default Error Message!";

   @JsonProperty("payload")
    private Object payload = null;

    /**
     * Initialize for Failure Response
     * @param code
     * @param desc
     */
    public AbstractResponse initFailure( String code, String desc) {
        return this.init(false, code, desc);
    }

    /**
     * Initialize for Success Response
     * @param code
     * @param desc
     */
    public AbstractResponse initSuccess(String code, String desc) {
        return this.init(true, code, desc);
    }

    /**
     * Set the Response Status, Code and Description
     * @param status
     * @param code
     * @param desc
     */
    public AbstractResponse init(boolean status, String code, String desc) {
        success     = status;
        this.code = code;
        description = desc;
        payload     = new ArrayList<Object>();
        return this;
    }

    /**
     * Set the Payload
     * @param payload
     */
    public AbstractResponse setPayload(Object payload) {
        ArrayList<Object> data = new ArrayList<>();
        if(payload != null) {
            if(payload instanceof List) {
                this.payload = payload;
                return this;
            } else {
                data.add(payload);
            }
        }
        this.payload = data;
        return this;
    }

    /**
     * Request Time
     * @return
     */
    public LocalDateTime getRequestTime() {
        return requestTime;
    }

    /**
     * Returns True if the Response is Success else False
     */
    public boolean getSuccess() {
        return success;
    }

    /**
     * Returns the Error Code
     * @return
     */
    public String getCode() {
        return code;
    }

    /**
     * Returns the Description
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     * Retuurns the Payload
     * @return
     */
    public Object getPayload() {
        return payload;
    }
}