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
package cl.io.gateway.network.driver;

/**
 * Interface representing network connection server connections.
 *
 * @author egacl
 */
public interface INetworkDriverServer {

    /**
     * Allows you to boot the network server to listen for connections. Before the
     * {@link #initialice()} method must be executed.
     */
    void start();

    /**
     * Allows you to stop the network server.
     */
    void stop();

    /**
     * Allows you to initialize the network server.
     */
    void initialice();
}
