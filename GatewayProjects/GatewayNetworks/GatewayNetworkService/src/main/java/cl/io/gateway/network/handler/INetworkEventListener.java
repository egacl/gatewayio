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
package cl.io.gateway.network.handler;

import cl.io.gateway.network.NetworkEvent;

/**
 * Interface used to obtain connection status changes for network channels.
 * 
 * @author egacl
 */
public interface INetworkEventListener {

    /**
     * This method is called when a channel network event is received.
     *
     * @param event
     *            Network event.
     */
    void onEvent(NetworkEvent event);
}
