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

// FasterXML
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
// Custom
import io.fusion.air.microservice.domain.exceptions.AuthorizationException;
import io.fusion.air.microservice.domain.models.auth.Token;
import io.fusion.air.microservice.security.CryptoKeyGenerator;
import io.fusion.air.microservice.security.KeyCloakConfig;
// Spring
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
// Java
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

/**
 * @author: Araf Karsh Hamid
 * @version:
 * @date:
 */
@Service
public class AuthKeyCloakService {

    @Autowired
    private KeyCloakConfig keyCloakConfig;

    @Autowired
    private CryptoKeyGenerator cryptoKeyGenerator;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * Authenticate User with KeyCloak using User Credentials
     * Returns Token if the user is authenticated
     * @param username
     * @param password
     * @return
     */
    public Token authenticateUser(String username, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", keyCloakConfig.getKeyCloakGrantType());
        params.add("client_id", keyCloakConfig.getKeyCloakClientId());
        params.add("client_secret", keyCloakConfig.getKeyCloakSecret());
        params.add("username", username);
        params.add("password", password);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<Token> responseEntity = restTemplate.postForEntity(keyCloakConfig.getKeyCloakUrl(), request, Token.class);
            return responseEntity.getBody();
        } catch (Exception e) {
            // throw new SecurityException("Access Denied!", e);
            throw new AuthorizationException("Access Denied!", e);
        }
    }

    public Token refreshToken(String refreshToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "refresh_token");
        params.add("client_id", keyCloakConfig.getKeyCloakClientId());
        params.add("client_secret", keyCloakConfig.getKeyCloakSecret());
        params.add("refresh_token", refreshToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<Token> responseEntity = restTemplate.postForEntity(keyCloakConfig.getKeyCloakUrl(), request, Token.class);
            return responseEntity.getBody();
        } catch (Exception e) {
            // throw new SecurityException("Access Denied!", e);
            throw new AuthorizationException("Access Denied!", e);
        }
    }

    /**
     * Return the Public Key from KeyCloak
     * @return
     */
    private String getPublicKey() {
        return restTemplate.getForObject(keyCloakConfig.getKeyCloakCertsUrl(), String.class);
    }

    /**
     * Return the Public Key from KeyCloak
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public JsonNode getPublicKeyFromKeycloak() throws IOException {
        // Assuming the key info is in a "keys" array
        return new ObjectMapper().readTree(getPublicKey()).get("keys").get(1);
    }

    /**
     * Create the RSA Key from the KeyCloak Public Key
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public RSAPublicKey createRSAKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        JsonNode keyData = getPublicKeyFromKeycloak();
        String modulusBase64 = keyData.get("n").asText();
        String exponentBase64 = keyData.get("e").asText();

        BigInteger modulus = new BigInteger(1, Base64.getUrlDecoder().decode(modulusBase64));
        BigInteger publicExponent = new BigInteger(1, Base64.getUrlDecoder().decode(exponentBase64));

        return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new RSAPublicKeySpec(modulus, publicExponent));
    }


    /**
     * Return the Public Key in PEM Format
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public String getPublicKeyPEMFormat()  {
        Path filePath = Paths.get(keyCloakConfig.getKeyCloakPublicKey());
        RSAPublicKey key = null;
        String keyName = "RSA PUBLIC KEY";
        if (Files.exists(filePath)) {
            try {
                key = cryptoKeyGenerator.readPublicKey(new File(keyCloakConfig.getKeyCloakPublicKey()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            // File Doesnt Exist - Create the File
            try {
                key = createRSAKey();
            } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
                throw new RuntimeException(e);
            }
            cryptoKeyGenerator.setPublicKeyFromKeyCloak(key);
            cryptoKeyGenerator.writePEMFile(key, keyCloakConfig.getKeyCloakPublicKey(), keyName);
        }
        return cryptoKeyGenerator.convertKeyToText(key, keyName);
    }


}
