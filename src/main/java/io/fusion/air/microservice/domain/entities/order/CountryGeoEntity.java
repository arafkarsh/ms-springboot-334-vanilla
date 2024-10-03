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
package io.fusion.air.microservice.domain.entities.order;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

/**
 * @author: Araf Karsh Hamid
 * @version:
 * @date:
 */
@Entity
@Table(name = "country_geolite_m")
public class CountryGeoEntity implements Serializable {

    @Id
    @Column(name = "geoname_id", unique = true)
    private int geoNameId;

    @NotNull
    @Column(name = "is_in_european_union")
    private int isInEuropeanUnion;

    @NotNull
    @Column(name = "locale_code")
    private String localeCode;

    @NotNull
    @Column(name = "continent_code")
    private String continentCode;

    @Column(name = "continent_name")
    private String continentName;

    @Column(name = "country_iso_code")
    private String countryIsoCode;

    @Column(name = "country_name")
    private String countryName;

    protected CountryGeoEntity() {
    }

    public int getGeoNameId() {
        return geoNameId;
    }

    public int getIsInEuropeanUnion() {
        return isInEuropeanUnion;
    }

    public String getLocaleCode() {
        return localeCode;
    }

    public String getContinentCode() {
        return continentCode;
    }

    public String getContinentName() {
        return continentName;
    }

    public String getCountryIsoCode() {
        return countryIsoCode;
    }

    public String getCountryName() {
        return countryName;
    }

    /**
     * Returns Country ISO ID + Country Name
     * @return
     */
    public String toString() {
        return getCountryIsoCode() + "|" + getCountryName();
    }

    /**
     * Returns the Hashcode
     * @return
     */
    public int hashCode() {
        return Objects.hash(getCountryIsoCode(), getCountryName());
    }

    /**
     * Checks Equality returns True if Objects are equal
     * Check Country ID and Country Name
     * @param o
     * @return
     */
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        CountryGeoEntity countryEntity = (CountryGeoEntity) o;
        return getCountryIsoCode().equalsIgnoreCase(countryEntity.getCountryIsoCode())
                && getCountryName().equals(countryEntity.getCountryName());
    }
}
