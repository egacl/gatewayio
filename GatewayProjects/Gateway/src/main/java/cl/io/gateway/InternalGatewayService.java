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
package cl.io.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cl.io.gateway.service.IGatewayService;

public class InternalGatewayService extends AbstractGateway<IGatewayService, InternalService> {

    private static final Logger logger = LoggerFactory.getLogger(InternalGatewayService.class);

    public InternalGatewayService(Gateway gateway, InternalService service) throws Exception {
        super(gateway, service);
    }

    @Override
    public void init() throws Exception {
        logger.info("Initialiazing gateway element '" + this.getGatewayId() + "' from '" + this.getContextId()
                + "' context");
        super.init();
        this.getElementInstance().initialize(this);
    }
}
