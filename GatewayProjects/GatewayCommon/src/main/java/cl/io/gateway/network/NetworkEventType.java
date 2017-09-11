/*
 * Copyright 2017 GetSoftware (http://www.getsoftware.cl)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cl.io.gateway.network;

/**
 *
 * @author egacl
 */
public enum NetworkEventType {
    
    /**
     * Invoked when a Channel is registered to its EventLoop and is able to handle I/O.
     */
    REGISTERED,
    
    /**
     * Invoked when a Channel is deregistered from its EventLoop and can’t handle any I/O.
     */
    UNREGISTERED,
    
    /**
     * Invoked when a Channel is active; the Channel is connected/bound and ready.
     */
    ACTIVE,
    
    /**
     * Invoked when a Channel leaves active state and is no longer connected to its remote peer.
     */
    INACTIVE,

    /**
     * Invoked when a connected Channel has iddle network activity.
     */
    TIMEOUT_ALERT_ON,

    /**
     * Invoked when a connected Channel respond a ping request.
     */
    TIMEOUT_ALERT_OFF;
    
}
