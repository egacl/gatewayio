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
package cl.io.gateway.messaging;

/**
 * Enumeration that represents network connections sources that may have
 * configured a gateway.
 *
 * @author egacl
 *
 */
public enum NetworkServiceSource {
    /**
     * client connections network source
     */
    CLIENT,
    /**
     * bus connections network source
     */
    BUS,
    /**
     * admin connections network source
     */
    ADMIN;
}
