/**
 * (C) Copyright 2023 Araf Karsh Hamid
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
package io.fusion.air.microservice.security.jwt.core;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.io.Serializable;

/**
 * KeyCloak Configuration Auth Microservice
 *
 * @author: Araf Karsh Hamid
 * @version:
 * @date:
 */
@Configuration
@PropertySource(
        name = "keyCloakConfig",
        // Expects file in the directory the jar is executed
        value = "file:./application.properties")
        // Expects the file in src/main/resources folder
        // value = "classpath:application.properties")
        // value = "classpath:application2.properties,file:./application.properties")
public class KeyCloakConfig implements Serializable {

    @Value("${server.keycloak.url}")
    private String keyCloakUrl;

    @Value("${server.keycloak.certs}")
    private String keyCloakCertsUrl;

    @Value("${server.keycloak.clientId}")
    private String keyCloakClientId;

    @Value("${server.keycloak.secret}")
    private String keyCloakSecret;

    @Value("${server.keycloak.grantType}")
    private String keyCloakGrantType;

    @Value("${server.keycloak.enabled}")
    private boolean keyCloakEnabled;

    @Value("${server.keycloak.publicKey}")
    private String keyCloakPublicKey;

    @Value("${server.keycloak.issuer}")
    private String tokenIssuer;

    /**
     * Return KeyCloak URL
     * @return
     */
    public String getKeyCloakUrl() {
        return keyCloakUrl;
    }

    /**
     * Return KeyCloak Client ID
     * @return
     */
    public String getKeyCloakClientId() {
        return keyCloakClientId;
    }

    /**
     * Return KeyCloak Secret
     * @return
     */
    public String getKeyCloakSecret() {
        return keyCloakSecret;
    }

    /**
     * Return KeyCloak Certs URL
     * @return
     */
    public String getKeyCloakCertsUrl() {
        return keyCloakCertsUrl;
    }

    /**
     * Return KeyCloak Grant Type
     * @return
     */
    public String getKeyCloakGrantType() {
        return keyCloakGrantType;
    }

    /**
     * Returns TRUE KeyCloak Enabled
     * @return
     */
    public boolean isKeyCloakEnabled() {
        return keyCloakEnabled;
    }

    /**
     * Returns the KeyCloak Public Key
     * @return
     */
    public String getKeyCloakPublicKey() {
        return keyCloakPublicKey;
    }

    /**
     * Returns the Token Issuer
     * @return
     */
    public String getTokenIssuer() {
        return tokenIssuer;
    }
}
