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
package io.fusion.air.microservice.server.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.springframework.context.annotation.Configuration;
import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Log Aspect for the Servers
 * Log Messages
 * Keep Track of Time
 * Exception Handling
 *
 * @author arafkarsh
 */
@Aspect
@Configuration
public class TimeTrackerServerAspect {

    // Set Logger -> Lookup will automatically determine the class name.
    private static final Logger log = getLogger(lookup().lookupClass());

    /**
     * Log Message before the Method Call
     * @param joinPoint
     */
    @Before(value = "execution(* io.fusion.air.microservice.server.controllers.*.*(..))")
    public void logStatementBefore(JoinPoint joinPoint) {
        log.debug("1|TA|TIME=|STATUS=START|CLASS={}",joinPoint);
    }

    /**
     * Log Message after the method call
     * @param joinPoint
     */
    @After(value = "execution(* io.fusion.air.microservice.server.controllers.*.*(..))")
    public void logStatementAfter(JoinPoint joinPoint) {
        log.debug("9|TA|TIME=|STATUS=END|CLASS={}",joinPoint);
    }

    /**
     * Capture Method Execution Time
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around(value = "execution(* io.fusion.air.microservice.server.controllers.*.*(..))")
    public Object timeTracker(ProceedingJoinPoint joinPoint) throws Throwable {
        return trackTime("WS", joinPoint);
    }

    /**
     * Track Time
     * @param _method
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    private Object trackTime(String _method, ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String status = "STATUS=SUCCESS";
        try {
            return joinPoint.proceed();
        }catch(Throwable e) {
            status = "STATUS=ERROR:"+e.getMessage();
            throw e;
        } finally {
            logTime(_method, startTime, status, joinPoint);
        }
    }

    /**
     * Log Time Taken to Execute the Function
     * @param _startTime
     * @param _status
     * @param joinPoint
     */
    private void logTime(String _method, long _startTime, String _status, ProceedingJoinPoint joinPoint) {
        long timeTaken=System.currentTimeMillis() - _startTime;
        log.info("3|{}|TIME={} ms|{}|CLASS={}|",_method, timeTaken, _status,joinPoint);
    }
}
