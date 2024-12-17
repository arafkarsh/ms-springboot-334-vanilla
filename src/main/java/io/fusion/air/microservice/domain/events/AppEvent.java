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
package io.fusion.air.microservice.domain.events;

import java.time.LocalDateTime;

/**
 * App Event Example
 *
 * @author arafkarsh
 */
public class AppEvent {

    private String id;
    private String name;
    private LocalDateTime eventTime;

    /**
     * App Event
     * @param id
     * @param name
     * @param eventTime
     */
    public AppEvent(String id, String name, LocalDateTime eventTime) {
        this.id = id;
        this.name = name;
        this.eventTime = eventTime;
    }

    /**
     * Returns Event ID
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * Returns Event Name
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Returns Event Time
     * @return
     */
    public LocalDateTime getEventTime() {
        return eventTime;
    }
}
