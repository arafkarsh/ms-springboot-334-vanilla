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
package io.fusion.air.microservice.server.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * Config Map
 *
 * @author arafkarsh
 *
 */
public class StandardConfig implements Serializable {

    private String serviceOrg;
    private String serviceName;
    private String serviceApiPrefix;
    private String serviceApiVersion;
    private String serviceApiName;
    private String serviceApiPath;
    private String serviceApiErrorPrefix;
    private String serviceContainer;
    private String serviceUrl;
    private String apiDocPath;
    private int buildNumber;
    private String buildDate;
    private String serverVersion;
    private String serverHost;
    private ArrayList<String> appPropertyList;
    private HashMap<String, String> appPropertyMap;

    public StandardConfig() {
    }

    public ArrayList<String> getAppPropertyList() {
        return appPropertyList;
    }

    public HashMap<String, String> getAppPropertyMap() {
        return appPropertyMap;
    }

    @Override
    public String toString() {
        return "StandardConfig{" +
                "serviceName='" + getServiceName() + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StandardConfig configMap = (StandardConfig) o;
        return getServiceName().equals(configMap.getServiceName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServiceName());
    }

    public String getServiceOrg() {
        return serviceOrg;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getServiceApiPrefix() {
        return serviceApiPrefix;
    }

    public String getServiceApiVersion() {
        return serviceApiVersion;
    }

    public String getServiceApiName() {
        return serviceApiName;
    }

    public String getServiceApiPath() {
        return serviceApiPath;
    }

    public String getServiceApiErrorPrefix() {
        return serviceApiErrorPrefix;
    }

    public String getServiceContainer() {
        return serviceContainer;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public String getApiDocPath() {
        return apiDocPath;
    }

    public int getBuildNumber() {
        return buildNumber;
    }

    public String getBuildDate() {
        return buildDate;
    }

    public String getServerVersion() {
        return serverVersion;
    }

    public String getServerHost() {
        return serverHost;
    }

    // =======================================================================================================
    // Package Local Methods
    // =======================================================================================================
    /**
     *
     * @param serviceOrg
     */
    void setServiceOrg(String serviceOrg) {
        this.serviceOrg = serviceOrg;
    }

    void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    void setServiceApiPrefix(String serviceApiPrefix) {
        this.serviceApiPrefix = serviceApiPrefix;
    }

    void setServiceApiVersion(String serviceApiVersion) {
        this.serviceApiVersion = serviceApiVersion;
    }

    void setServiceApiName(String serviceApiName) {
        this.serviceApiName = serviceApiName;
    }

    void setServiceApiPath(String serviceApiPath) {
        this.serviceApiPath = serviceApiPath;
    }

    void setServiceApiErrorPrefix(String serviceApiErrorPrefix) {
        this.serviceApiErrorPrefix = serviceApiErrorPrefix;
    }

    void setServiceContainer(String serviceContainer) {
        this.serviceContainer = serviceContainer;
    }

    void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    void setApiDocPath(String apiDocPath) {
        this.apiDocPath = apiDocPath;
    }

    void setBuildNumber(int buildNumber) {
        this.buildNumber = buildNumber;
    }

    void setBuildDate(String buildDate) {
        this.buildDate = buildDate;
    }

    void setServerVersion(String serverVersion) {
        this.serverVersion = serverVersion;
    }

    void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    void setAppPropertyList(ArrayList<String> appPropertyList) {
        this.appPropertyList = appPropertyList;
    }

    void setAppPropertyMap(HashMap<String, String> appPropertyMap) {
        this.appPropertyMap = appPropertyMap;
    }
}
