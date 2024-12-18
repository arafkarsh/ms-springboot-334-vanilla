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
package io.fusion.air.microservice.adapters.security;

// Custom
import io.fusion.air.microservice.domain.exceptions.SecurityException;
import io.fusion.air.microservice.domain.models.auth.Token;
import io.fusion.air.microservice.domain.ports.services.UserService;
import io.fusion.air.microservice.security.CryptoKeyGenerator;
import io.fusion.air.microservice.security.TokenManager;
import io.fusion.air.microservice.server.config.ServiceConfiguration;
// Spring
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
// Java
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author: Araf Karsh Hamid
 * @version:
 * @date:
 */
@Service
public class AuthLocalService {

    // Set Logger -> Lookup will automatically determine the class name.
    private static final Logger log = getLogger(lookup().lookupClass());

    // Autowired using Constructor
    private final ServiceConfiguration serviceConfig;

    // Autowired using Constructor
    private final CryptoKeyGenerator cryptoKeys;

    // Autowired using Constructor
    private final TokenManager tokenManager;

    // Autowired using Constructor
    private final UserService userService;

    @Value("${server.token.auth.expiry:300000}")
    private long tokenAuthExpiry;

    @Value("${server.token.refresh.expiry:1800000}")
    private long tokenRefreshExpiry;

    /**
     * Autowired using Constructor
     *
     * @param serviceConfig
     * @param cryptoKeys
     * @param tokenManager
     * @param userService
     */
    public  AuthLocalService(ServiceConfiguration serviceConfig, CryptoKeyGenerator cryptoKeys,
                             TokenManager tokenManager, UserService userService ) {
        this.serviceConfig = serviceConfig;
        this.cryptoKeys = cryptoKeys;
        this.tokenManager = tokenManager;
        this.userService = userService;
    }

    public Map<String,String> getPublicKey() throws SecurityException {
        cryptoKeys.setKeyFiles(getCryptoPublicKeyFile(), getCryptoPrivateKeyFile())
                .readRSAKeyFiles();
        // Send the Token in the Body (This is NOT Required and ONLY for Testing Purpose)
        HashMap<String,String> data = new HashMap<>();
        data.put("type", "Public-Key");
        data.put("format", cryptoKeys.getPublicKey().getFormat());
        data.put("key", cryptoKeys.getPublicKeyPEMFormat());
        return data;
    }

    /**
     * Returns Crypto Public Key File
     * @return
     */
    private String getCryptoPublicKeyFile() {
        return (serviceConfig != null) ? serviceConfig.getCryptoPublicKeyFile() : "publicKey.pem";
    }

    /**
     * Returns Crypto Private Key File
     * @return
     */
    private String getCryptoPrivateKeyFile() {
        return (serviceConfig != null) ? serviceConfig.getCryptoPrivateKeyFile() : "privateKey.pem";
    }

    /**
     * Authenticate User with Local Authentication using User Credentials
     * Returns Token if the user is authenticated
     * @param username
     * @param password
     * @return
     */
    public Map<String, String> authenticateUser(String username, String password) {
        // Validate the User ID & Password with DB
        log.debug("Authenticate user with credentials {} ", username);
        // .....
        // Authenticate the User using username & password.
        // The following is pseudo service for demo purpose
        userService.authenticateUser(username, password);
        // If Validation is Successful then Create the Tokens (Auth & Refresh Tokens)
        // Once Authenticated Generate the Token as follows
        HttpHeaders headers = new HttpHeaders();
        Map<String, String> tokens = tokenManager.createAuthorizationToken(username, headers);
        // TX - Token
        String txToken = tokenManager.createTXToken(username, TokenManager.TX_USERS,  headers);
        tokens.put("TX-Token", txToken);
        return tokens;
    }

    public Token refreshToken(String refreshToken) throws NoSuchMethodException {
        throw new NoSuchMethodException("Refresh Token "+refreshToken);
    }
}
