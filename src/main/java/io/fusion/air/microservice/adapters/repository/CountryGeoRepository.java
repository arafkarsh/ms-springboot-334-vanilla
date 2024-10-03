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
package io.fusion.air.microservice.adapters.repository;

import io.fusion.air.microservice.domain.entities.order.CountryGeoEntity;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author: Araf Karsh Hamid
 * @version:
 * @date:
 */
@Repository
public interface CountryGeoRepository extends PagingAndSortingRepository<CountryGeoEntity, Integer> {

    /**
     * Find By Country Geo Name ID
     * @param cid
     * @return
     */
    public Optional<CountryGeoEntity> findByGeoNameId(int cid);

    /**
     * List All Countries by Continent
     * @param continent
     * @return
     */
    public List<CountryGeoEntity> findByContinentName(String continent);
}
