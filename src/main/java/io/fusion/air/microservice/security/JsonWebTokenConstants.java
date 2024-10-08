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
package io.fusion.air.microservice.security;

/**
 * @author: Araf Karsh Hamid
 * @version:
 * @date:
 */
public class JsonWebTokenConstants {

    public static String TOKEN = "<([1234567890SecretKey!!??To??Encrypt##Data@12345%6790])>";

    public static final long EXPIRE_IN_ONE_MINS 		= 1000 * 60;
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

    public static final int SECRET_KEY 				        = 1;
    public static final int PUBLIC_KEY				        = 2;
    public static final int KEYCLOAK_PUBLIC_KEY		    = 3;

    public static final int LOCAL_KEY 				        = 1;
    public static final int KEYCLOAK_KEY 			        = 2;
}
