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
 * Error during message serialization process.
 * 
 * @author egacl
 */
public class NetworkMessageSerializationException extends Exception {

    private static final long serialVersionUID = 1L;

    public NetworkMessageSerializationException(String message) {
        super(message);
    }

    public NetworkMessageSerializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public NetworkMessageSerializationException(Throwable cause) {
        super(cause);
    }
}