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
package io.fusion.air.microservice.domain.models.auth;

import jakarta.validation.constraints.NotBlank;

/**
 * User Credentials
 *
 * @author: Araf Karsh Hamid
 * @version:
 * @date:
 */
public class UserCredentials {

    @NotBlank(message = "The User ID is required.")
    private String userId;
    @NotBlank(message = "The Password is required.")
    private String password;

    /**
     * Default Constructor
     */
    public UserCredentials() {
    }

    /**
     * Constructor
     * @param userId
     * @param password
     */
    public UserCredentials(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }

    /**
     * Returns the User ID
     * @return
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Returns the Password
     * @return
     */
    public String getPassword() {
        return password;
    }
}
