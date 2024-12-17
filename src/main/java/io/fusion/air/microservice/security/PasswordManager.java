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
package io.fusion.air.microservice.security;

import io.fusion.air.microservice.server.config.ServiceConfiguration;

/**
 * Password Manager Decrypts the Encrypted DB Credentials in the property file
 * In Production Environment Use Secure Vault to Store Sensitive Information like Credentials etc
 *
 * @author: Araf Karsh Hamid
 * @version:
 * @date:
 */
public class PasswordManager {

    // Autowired using the Constructor
    private ServiceConfiguration serviceConfig;

    /**
     * Autowired using the Constructor
     * @param serviceCfg
     */
    public PasswordManager(ServiceConfiguration serviceCfg) {
        serviceConfig = serviceCfg;
    }
    /**
     * Return the Decrypted User Name
     * @return
     */
    public String getDatabaseUserName() {
        return SecureData.decrypt(serviceConfig.getDataSourceUserName(),  getSeed());
    }

    /**
     * Return the Decrypted Password
     * @return
     */
    public String getDatabasePassword() {
        return SecureData.decrypt(serviceConfig.getDataSourcePassword(), getSeed());
    }

    /**
     * Returns the Secret Key
     * @return
     */
    private String getSeed() {
        return new StringBuilder()
                .append(serviceConfig.getServiceOrg())
                .append("|")
                .append(serviceConfig.getServiceName())
                .append("|")
                .append(serviceConfig.getSecureDataKey())
                .toString();
    }

    /**
     * For Testing ONLY
     * In Production Environment Use Secure Vault to Store Sensitive Information like Credentials etc.
     * @param args
     */
    public static void main (String[] args) {

        String seed  = "lincolnHawk"+"|"+"ms-vanilla"+"|"+"<([1234567890SecretKEY!!TO??Encrypt##DATA@12345%6790])>";
        String un = "sa";
        String psd = "SigmaHawk.1901";
        String uEnc = SecureData.encrypt(un, seed);
        String pEnc = SecureData.encrypt(psd, seed);
        System.out.println("U="+un+" | Encrypted="+uEnc);
        System.out.println("U="+un+" | Decrypted="+SecureData.decrypt(uEnc, seed));
        System.out.println("P="+psd+" | Encrypted="+pEnc);
        System.out.println("U="+psd+" | Decrypted="+SecureData.decrypt(pEnc, seed));
    }
}
