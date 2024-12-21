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
package io.fusion.air.microservice.server.config;

import java.io.Serializable;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.fusion.air.microservice.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * Service Configuration
 *
 * @author arafkarsh
 *
 */
@Component
@Configuration
@PropertySource(
		name = "serviceConfig",
		// Expects file in the directory the jar is executed
		value = "file:./application.properties")
		// Expects the file in src/main/resources folder
		// value = "classpath:application.properties")
		// value = "classpath:application2.properties,file:./application.properties")
public class ServiceConfig implements Serializable {

	// Config Path
	public static final String CONFIG_PATH = "/config";

	// Health Path
	public static final String HEALTH_PATH = "/service";

	/**
	 * To be used outside SpringBoot Context
	 * For WireMock Testing the External Services
	 */
	@Autowired
	public ServiceConfig() {
		this("localhost", 8080);
	}

	/**
	 * To be used outside SpringBoot Context
	 * For WireMock Testing the External Services
	 *
	 * @param rHost
	 * @param rPort
	 */
	public ServiceConfig(String rHost, int rPort) {
		this.remoteHost = rHost;
		this.remotePort = rPort;
	}

	/**
	 * Return the JSON String
	 * @return
	 */
	public String toJSONString()  {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"springdoc.swagger-ui.path\": \"").append(apiDocPath).append("\",");
		sb.append("\"service.org\": \"").append(serviceOrg).append("\",");
		sb.append("\"service.name\": \"").append(serviceName).append("\",");
		sb.append("\"service.api.prefix\": \"").append(serviceApiPrefix).append("\",");
		sb.append("\"service.api.version\": \"").append(serviceApiVersion).append("\",");
		sb.append("\"service.api.name\": \"").append(serviceApiName).append("\",");
		sb.append("\"service.api.path\": \"").append(serviceApiPath).append("\",");
		sb.append("\"service.url\": \"").append(serviceUrl).append("\",");
		sb.append("\"build.number\": ").append(buildNumber).append(",");
		sb.append("\"build.date\": \"").append(buildDate).append("\",");
		sb.append("\"serverVersion\": \"").append(serverVersion).append("\",");
		sb.append("\"server.port\": ").append(serverPort).append(",");
		sb.append("\"server.restart\": ").append(serverRestart).append(",");
		sb.append("\"app.property.list\": ").append(Utils.toJsonString(appPropertyList)).append(",");
		sb.append("\"app.property.map\": ").append(Utils.toJsonString(appPropertyMap));
		sb.append("}");
		return sb.toString();
	}

	/**
	 * Returns Basic Info about the Server
	 * @return
	 */
	public Map<String, String> getConfigMap() {
		HashMap<String,String> map = new LinkedHashMap<>();
		map.put("swagger.api.path", apiDocPath);
		map.put("service.org", serviceOrg);
		map.put("service.name",  serviceName);
		map.put("service.url", serviceUrl);
		map.put("service.api.version", serviceApiVersion);
		map.put("build.number", ""+buildNumber);
		map.put("build.date", buildDate);
		map.put("serverVersion", serverVersion);
		return map;
	}

	@Value("${service.org:OrgNotDefined}")
	private String serviceOrg;

	@Value("${service.name:NameNotDefined}")
	private String serviceName;

	@Value("${service.api.prefix:API}")
	private String serviceApiPrefix;

	@Value("${service.api.version:V1}")
	private String serviceApiVersion;

	@Value("${service.api.name:NAME}")
	private String serviceApiName;

	@Value("${service.api.path:PATH}")
	private String serviceApiPath;

	@Value("${service.api.error.prefix:99}")
	private String serviceApiErrorPrefix;

	@Value("${service.container:ContainerName}")
	private String serviceContainer;

	@Value("${service.api.repository:GitRepo}")
	private String serviceApiRepository;

	@Value("${service.url:URL}")
	private String serviceUrl;

	@Value("${service.license:MIT License}")
	private String serviceLicense;

	@Value("${service.license.url}")
	private String serviceLicenseURL;

	@Value("${springdoc.swagger-ui.path}")
	private String apiDocPath;

	@Value("${build.number:13}")
	private int buildNumber;
	
	@Value("${build.date}")
	private String buildDate;
	
	@Value("${server.version:0.5.0}")
	private String serverVersion;

	@Value("${server.host:localhost}")
	private String serverHost;

	@Value("${server.resources.url}")
	private String serverResourceUrl;

	@Value("${server.host.dev:localhost:9090}")
	private String serverHostDev;
	@Value("${server.host.dev.desc:localhost:9090}")
	private String serverHostDevDesc;

	@Value("${server.host.uat:localhost:9090}")
	private String serverHostUat;
	@Value("${server.host.uat.desc:localhost:9090}")
	private String serverHostUatDesc;

	@Value("${server.host.prod:localhost:9090}")
	private String serverHostProd;
	@Value("${server.host.prod.desc:localhost:9090}")
	private String serverHostProdDesc;

	@Value("${server.port:9080}")
	private int serverPort;
	
	@Value("${payment.gateway.host:localhost}")
	private String paymentGWHost;
	@Value("${payment.gateway.port:9091}")
	private int paymentGWPort;
	
	@Value("${remote.host:localhost}")
	private String remoteHost;
	@Value("${remote.port:9091}")
	private int remotePort;
	
	@Value("${server.restart}")
	private boolean serverRestart;

	@Value("${server.api.url.print}")
	private boolean serverPrintAPIUrl;

	@Value("${spring.profiles.default:dev}")
	private String activeProfile;

	@Value("${spring.codec.max-in-memory-size:3MB}")
	private String springCodecMaxMemory;

	// Get All the System Properties
	@JsonIgnore
	@Value("#{systemProperties}")
	private HashMap<String, String> systemProperties;

	// Property Type - Product
	@Value("${app.property.product:fusion.air.product}")
	private String appPropertyProduct;

	// Deployed App Property Product List
	@JsonIgnore
	@Value("${app.property.product.list}")
	private ArrayList<String> appPropertyProductList;

	// Deployed App Property List
	@JsonIgnore
	@Value("${app.property.list}")
	private ArrayList<String> appPropertyList;

	// Deployed App Properties Map
	@JsonIgnore
	@Value("#{${app.property.map}}")
	private HashMap<String, String> appPropertyMap;

	/**
	 * Returns Service Details as HTML
	 * @return
	 */
	public String getServiceDetails() {
		StringBuilder sb = new StringBuilder();
		sb.append("<b>").append(serviceName).append(" Service </b><br>");
		sb.append("Build No:<b> ").append(buildNumber).append("</b>, ");
		sb.append("Build Date:<b> ").append(buildDate).append("</b> ");
		return sb.toString();
	}

	/**
	 * Show the API URL
	 * @return
	 */
	public String apiURL() {
		return "http://localhost:"+serverPort+""+ apiDocPath;
	}

	/**
	 * @return the serverPort
	 */
	public int getServerPort() {
		return serverPort;
	}

	/**
	 * @return the paymentGWHost
	 */
	public String getPaymentGWHost() {
		return paymentGWHost;
	}

	/**
	 * @return the paymentGWPort
	 */
	public int getPaymentGWPort() {
		return paymentGWPort;
	}

	/**
	 * @return the remoteHost
	 */
	public String getRemoteHost() {
		return remoteHost;
	}

	/**
	 * @return the remotePort
	 */
	public int getRemotePort() {
		return remotePort;
	}

	/**
	 * @return the springCodecMaxMemory
	 */
	public String getSpringCodecMaxMemory() {
		return springCodecMaxMemory;
	}
	
	/**
	 * @return the serverVersion
	 */
	public String getServerVersion() {
		return serverVersion;
	}

	/**
	 * @return the serverRestart
	 */
	public boolean isServerRestart() {
		return serverRestart;
	}

	/**
	 * @return the buildNumber
	 */
	public int getBuildNumber() {
		return buildNumber;
	}

	/**
	 * @return the buildDate
	 */
	public String getBuildDate() {
		return buildDate;
	}
	
	/**
	 * @return the serviceName
	 */
	public String getServiceName() {
		return serviceName;
	}

	/**
	 * Get Property Product Name
	 * @return
	 */
	public String getAppPropertyProduct() {
		return appPropertyProduct;
	}

	/**
	 * @return the appPropertyProductList
	 */
	public List<String> getAppPropertyProductList() {
		return appPropertyProductList;
	}

	/**
	 * Get Property List
	 * @return
	 */
	public List<String> getAppPropertyList() {
		return appPropertyList;
	}

	/**
	 * @return the appPropertyMap
	 */
	public Map<String, String> getAppPropertyMap() {
		return appPropertyMap;
	}
	
	/**
	 * @return the systemProperties
	 */
	public Map<String, String> systemProperties() {
		return getSystemProperties();
	}

	/**
	 * Returns API Doc Path
	 * @return
	 */
	public String getApiDocPath() {
		return apiDocPath;
	}

	/**
	 * Returns API Prefix
	 * @return
	 */
	public String getServiceApiPrefix() {
		return serviceApiPrefix;
	}

	/**
	 * Returns Service API Version
	 * @return
	 */
	public String getServiceApiVersion() {
		return serviceApiVersion;
	}

	/**
	 * Returns Service API Name
	 * @return
	 */
	public String getServiceApiName() {
		return serviceApiName;
	}

	/**
	 * Returns API Service Path
	 * @return
	 */
	public String getServiceApiPath() {
		return serviceApiPath;
	}

	/**
	 * Returns Service API Error Prefix
	 * @return
	 */
	public String getServiceApiErrorPrefix() {
		return (serviceApiErrorPrefix != null) ? serviceApiErrorPrefix : "99";
	}

	/**
	 * Returns Service Repository
	 * @return
	 */
	public String getServiceApiRepository() {
		return serviceApiRepository;
	}

	/**
	 * Returns Service URL
	 * @return
	 */
	public String getServiceUrl() {
		return serviceUrl;
	}

	/**
	 * Returns Organization Name
	 * @return
	 */
	public String getServiceOrg() {
		return serviceOrg;
	}

	/**
	 * Returns the Container Name
	 * @return
	 */
	public String getServiceContainer() {
		return serviceContainer;
	}

	/**
	 * Returns Service License Type
	 * @return
	 */
	public String getServiceLicense() {
		return serviceLicense;
	}

	/**
	 * Returns Server Host name
	 * @return
	 */
	public String getServerHost() {
		return serverHost;
	}

	/**
	 * Returns Dev Host Name
	 * @return
	 */
	public String getServerHostDev() {
		return serverHostDev;
	}

	public String getServerHostDevDesc() {
		return serverHostDevDesc;
	}

	public String getServerHostUat() {
		return serverHostUat;
	}

	public String getServerHostUatDesc() {
		return serverHostUatDesc;
	}

	public String getServerHostProd() {
		return serverHostProd;
	}

	public String getServerHostProdDesc() {
		return serverHostProdDesc;
	}

	/**
	 * Returns System Properties
	 * @return
	 */
	public Map<String, String> getSystemProperties() {
		return systemProperties;
	}

	/**
	 * Returns Server Resource URL
	 * @return
	 */
	public String getServerResourceUrl() {
		return serverResourceUrl;
	}

	/**
	 * Returns if the API URL can be printed
	 * @return
	 */
	public boolean isServerPrintAPIUrl() {
		return serverPrintAPIUrl;
	}

	/**
	 * Returns the Active Profile Name
	 * @return
	 */
	public String getActiveProfile() {
		return activeProfile;
	}

	/**
	 * Return Service License URL
	 * @return
	 */
	public String getServiceLicenseURL() {
		return serviceLicenseURL;
	}
}
