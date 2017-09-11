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
package cl.io.gateway.network.driver.exception;

/**
 * General error occurred in the context of the network driver.
 * 
 * @author egacl
 */
public class NetworkDriverException extends Exception {

    private static final long serialVersionUID = 1L;

    public NetworkDriverException(String message) {
        super(message);
    }

    public NetworkDriverException(String message, Throwable cause) {
        super(message, cause);
    }

    public NetworkDriverException(Throwable cause) {
        super(cause);
    }
}