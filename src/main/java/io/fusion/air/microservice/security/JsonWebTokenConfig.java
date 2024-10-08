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
package io.fusion.air.microservice.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.io.Serializable;

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

    // server.crypto.public.key=publicKey.pem
    @Value("${server.crypto.public.key:publicKey.pem}")
    private String cryptoPublicKeyFile;

    // server.crypto.private.key=privateKey.pem
    @Value("${server.crypto.private.key:privateKey.pem}")
    private String cryptoPrivateKeyFile;

    // server.token.issuer=${service.org}
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

    public JsonWebTokenConfig() {
    }

    public String getCryptoPublicKeyFile() {
        return cryptoPublicKeyFile;
    }

    public String getCryptoPrivateKeyFile() {
        return cryptoPrivateKeyFile;
    }

    public String getTokenIssuer() {
        return tokenIssuer;
    }

    public int getTokenType() {
        return tokenType;
    }

    public boolean isServerTokenTest() {
        return serverTokenTest;
    }

    public long getTokenAuthExpiry() {
        return tokenAuthExpiry;
    }

    public long getTokenRefreshExpiry() {
        return tokenRefreshExpiry;
    }

    public String getTokenKey() {
        return tokenKey;
    }

    public String getSecureDataKey() {
        return secureDataKey;
    }

    public String getServiceOrg() {
        return serviceOrg;
    }
}
