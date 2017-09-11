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
package cl.io.gateway.exception;

/**
 * This exception is reported when anomalies occur during processing of messages
 * or system processes.
 * 
 * @author egacl
 *
 */
public class GatewayProcessException extends Exception {

    private static final long serialVersionUID = 1L;

    public GatewayProcessException(String message, Throwable cause) {
        super(message, cause);
    }

    public GatewayProcessException(String message) {
        super(message);
    }

    public GatewayProcessException(Throwable cause) {
        super(cause);
    }
}
