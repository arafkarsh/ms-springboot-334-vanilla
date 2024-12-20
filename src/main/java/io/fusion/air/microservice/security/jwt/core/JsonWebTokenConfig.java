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
// Spring
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
// Java
import java.io.Serializable;

import static io.fusion.air.microservice.security.jwt.core.JsonWebTokenConstants.EXPIRE_IN_TEN_MINS;
import static io.fusion.air.microservice.security.jwt.core.JsonWebTokenConstants.EXPIRE_IN_THIRTY_MINS;

/**
 * JWT Configuration
 *
 * @author: Araf Karsh Hamid
 * @version:
 * @date:
 */
@Configuration
@PropertySource(
        name = "JsonWebTokenConfig",
        // Expects file in the directory the jar is executed
        value = "file:./application.properties")
        // Expects the file in src/main/resources folder
        // value = "classpath:application.properties")
        // value = "classpath:application2.properties,file:./application.properties")
public class JsonWebTokenConfig implements Serializable {

    @Value("${service.org:OrgNotDefined}")
    private String serviceOrg;

    @Value("${service.name:NameNotDefined}")
    private String serviceName;

    // server.crypto.public.key=publicKey.pem
    @Value("${server.crypto.public.key:publicKey.pem}")
    private String cryptoPublicKeyFile;

    // server.crypto.private.key=privateKey.pem
    @Value("${server.crypto.private.key:privateKey.pem}")
    private String cryptoPrivateKeyFile;

    @Value("${server.token.issuer}")
    private String tokenIssuer;

    // server.token.type=1
    // (Type 1 = secret key, 2 = public / private key)
    @Value("${server.token.type:1}")
    private int tokenType;

    @Value("${server.token.test}")
    private boolean serverTokenTest;

    // server.token.auth.expiry=300000
    @Value("${server.token.auth.expiry:300000}")
    private long tokenAuthExpiry;

    // server.token.refresh.expiry=1800000
    @Value("${server.token.refresh.expiry:1800000}")
    private long tokenRefreshExpiry;

    @Value("${server.token.key:sigmaEpsilon6109871597}")
    private String tokenKey;

    // server.secure.data.key
    @Value("${server.secure.data.key:alphaHawk6109871597}")
    private String secureDataKey;

    /**
     * Return Crypto Public Key File
     * @return
     */
    public String getCryptoPublicKeyFile() {
        return cryptoPublicKeyFile;
    }

    /**
     * Return Crypto Private Key File
     * @return
     */
    public String getCryptoPrivateKeyFile() {
        return cryptoPrivateKeyFile;
    }

    /**
     * Returns the Token Issuer
     * @return
     */
    public String getTokenIssuer() {
        return tokenIssuer;
    }

    /**
     * Returns the Token Type
     * @return
     */
    public int getTokenType() {
        return tokenType;
    }

    /**
     * Returns TRUE if the Server Token Test is enabled
     * @return
     */
    public boolean isServerTokenTest() {
        return serverTokenTest;
    }

    /**
     * Returns Auth Token Expiry Time
     * @return
     */
    public long getAuthTokenExpiry() {
        return tokenExpiryValidator( tokenAuthExpiry,  true);
    }

    /**
     * Returns Refresh Token Expiry Time
     * @return
     */
    public long getRefreshTokenExpiry() {
        return tokenExpiryValidator( tokenRefreshExpiry,  false);
    }

    /**
     * Returns the Token Key
     * @return
     */
    public String getTokenKey() {
        return tokenKey;
    }

    /**
     * Returns the Secure Data Key
     * @return
     */
    public String getSecureDataKey() {
        return secureDataKey;
    }

    /**
     * Returns the Service Org
     * @return
     */
    public String getServiceOrg() {
        return serviceOrg;
    }

    /**
     * @return the serviceName
     */
    public String getServiceName() {
        return serviceName;
    }


    /**
     * Validate the Token Expiry Time
     * @param tokenExpiry
     * @param auth
     * @return
     */
    public static final long tokenExpiryValidator(long tokenExpiry, boolean auth) {
        if (auth) {
            // For Auth Token
            return (tokenExpiry > EXPIRE_IN_TEN_MINS) ?  EXPIRE_IN_TEN_MINS : tokenExpiry;
        }
        // For Other Token like Refresh or Tx Token
        return (tokenExpiry > EXPIRE_IN_THIRTY_MINS) ? EXPIRE_IN_THIRTY_MINS  : tokenExpiry;
    }
}
