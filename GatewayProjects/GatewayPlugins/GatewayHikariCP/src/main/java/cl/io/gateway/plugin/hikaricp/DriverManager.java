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
package cl.io.gateway.plugin.hikaricp;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DriverManager {

    private Map<String, Driver> driversMap;

    public DriverManager() {
        this.driversMap = new ConcurrentHashMap<String, Driver>();
    }

    void addDriver(String id, Driver driverData) {
        this.driversMap.put(id, driverData);
    }

    Driver getDriver(String id) {
        return this.driversMap.get(id);
    }

    public static class Driver {

        private final String id;

        private final String name;

        private final String libPath;

        public Driver(final String id, String name, String libPath) {
            this.id = id;
            this.name = name;
            this.libPath = libPath;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getLibPath() {
            return libPath;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("Driver [id=");
            builder.append(id);
            builder.append(", name=");
            builder.append(name);
            builder.append(", libPath=");
            builder.append(libPath);
            builder.append("]");
            return builder.toString();
        }
    }
}
