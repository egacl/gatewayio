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

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;

/**
 * Interface that allows the network driver to create a bootstrap to initialize
 * a connection to another server in the network.
 *
 * @author egacl
 * @param <C>
 *            Client channel type.
 */
public interface IClientChannelInitializer<C extends Channel> {

    /**
     * Method that allows the network driver to create a bootstrap to initialize a
     * connection to another server in the network
     *
     * @param networkConnection
     *            data to connect another network server
     * @return boostrap instance
     * @throws Exception
     *             if an error ocurrus
     */
    Bootstrap createBootstrapClientConnection(DriverClientNetworkConnection networkConnection) throws Exception;
}