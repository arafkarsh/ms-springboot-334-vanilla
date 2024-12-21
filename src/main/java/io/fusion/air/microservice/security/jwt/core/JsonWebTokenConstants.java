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
package io.fusion.air.microservice.security.jwt.core;

/**
 * @author: Araf Karsh Hamid
 * @version:
 * @date:
 */
public class JsonWebTokenConstants {

    public static final String TOKEN = "<([1234567890SecretKey!!??To??Encrypt##Data@12345%6790])>";

    // Token Expiry Time
    public static final long EXPIRE_IN_ONE_MINS 		= 1000L * 60;
    public static final long EXPIRE_IN_FIVE_MINS 	    = EXPIRE_IN_ONE_MINS * 5;
    public static final long EXPIRE_IN_TEN_MINS 		= EXPIRE_IN_ONE_MINS * 10;
    public static final long EXPIRE_IN_TWENTY_MINS 	= EXPIRE_IN_ONE_MINS * 20;
    public static final long EXPIRE_IN_THIRTY_MINS 	= EXPIRE_IN_ONE_MINS * 30;
    public static final long EXPIRE_IN_ONE_HOUR 		= EXPIRE_IN_ONE_MINS * 60;

    public static final long EXPIRE_IN_TWO_HOUR 		= EXPIRE_IN_ONE_HOUR * 2;
    public static final long EXPIRE_IN_THREE_HOUR 	= EXPIRE_IN_ONE_HOUR * 3;
    public static final long EXPIRE_IN_FIVE_HOUR 	    = EXPIRE_IN_ONE_HOUR * 5;
    public static final long EXPIRE_IN_EIGHT_HOUR 	= EXPIRE_IN_ONE_HOUR * 8;
    public static final long EXPIRE_IN_ONE_DAY 		= EXPIRE_IN_ONE_HOUR * 24;

    public static final long EXPIRE_IN_TWO_DAYS 		= EXPIRE_IN_ONE_DAY * 2;
    public static final long EXPIRE_IN_ONE_WEEK 		= EXPIRE_IN_ONE_DAY * 7;
    public static final long EXPIRE_IN_TWO_WEEKS 	= EXPIRE_IN_ONE_DAY * 14;
    public static final long EXPIRE_IN_ONE_MONTH 	= EXPIRE_IN_ONE_DAY * 30;
    public static final long EXPIRE_IN_THREE_MONTHS	= EXPIRE_IN_ONE_DAY * 90;
    public static final long EXPIRE_IN_SIX_MONTHS 	= EXPIRE_IN_ONE_DAY * 180;
    public static final long EXPIRE_IN_ONE_YEAR 		= EXPIRE_IN_ONE_DAY * 365;

    public static final long EXPIRE_IN_TWO_YEARS 	    = EXPIRE_IN_ONE_YEAR * 2;
    public static final long EXPIRE_IN_FIVE_YEARS 	    = EXPIRE_IN_ONE_YEAR * 5;
    public static final long EXPIRE_IN_TEN_YEARS 	    = EXPIRE_IN_ONE_YEAR * 10;

    // Key Types
    public static final int SECRET_KEY 				        = 1;
    public static final int PUBLIC_KEY				        = 2;
    public static final int KEYCLOAK_PUBLIC_KEY		    = 3;

    // Key Origin
    public static final int LOCAL_KEY 				        = 1;
    public static final int KEYCLOAK_KEY 			        = 2;

    // Token Types
    public static final String AUTH                           = "auth";
    public static final String AUTH_REFRESH               = "refresh";
    public static final String TX_USERS                     = "tx-users";
    public static final String TX_SERVICE                   = "tx-internal";
    public static final String TX_EXTERNAL                 = "tx-external";

    // Tokens
    public static final String AUTH_TOKEN                  = "Authorization";
    public static final String REFRESH_TOKEN              = "Refresh-Token";
    public static final String TX_TOKEN                     = "TX-TOKEN";

    // Token Mode
    public static final String SINGLE_TOKEN_MODE      = "Single-Token-Mode";
    public static final String MULTI_TOKEN_MODE       = "Multi-Token-Mode";
    public static final String SECURE_PKG_MODE         = "Secure-Pkg-Mode";
    public static final String REFRESH_TOKEN_MODE     = "Refresh-Token-Mode";


    // User Type / Token Categories
    public static final int CONSUMERS                       = 1;
    public static final int INTERNAL_SERVICES            = 2;
    public static final int EXTERNAL_SERVICES            = 3;

    // User Roles
    public static final String ROLE_USER                    = "USER";
    public static final String ROLE_ADMIN                  = "ADMIN";
    public static final String ROLE_PUBLIC                  = "PUBLIC";

    // Other
    public static final String BEARER                         = "Bearer ";
    public static final String EXPIRES_IN                   = "expires_in";
    public static final String REFRESH_EXPIRES_IN       = "refresh_expires_in";
    public static final String SUCCESS                       = "SUCCESS";
    public static final String ERROR                          = "ERROR";

    private JsonWebTokenConstants() {}
}
