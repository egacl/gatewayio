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
package cl.io.gateway.stats;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A class that allows you to define keys for storing counters.
 *
 * I thank my co-workers who were the creators of this class.
 *
 * @author Eugenio
 */
public class Counter {

    private final ConcurrentHashMap<String, DataPoint> dataPointMap;

    public Counter() {
        dataPointMap = new ConcurrentHashMap<String, DataPoint>(20, 0.5F);
    }

    /**
     * Increment the counter identified by 'key' in 1. If no counter exists for the
     * specified identifier, one will be created automatically.
     *
     * @param key
     *            String counter identifier
     * @return counter value
     */
    public long increment(String key) {
        DataPoint dataPoint = dataPointMap.get(key);
        if (dataPoint == null) {
            dataPoint = new DataPoint(key);
            DataPoint oldDataPoint = putIfAbsent(dataPointMap, key, dataPoint);
            if (oldDataPoint != null) {
                dataPoint = oldDataPoint;
            }
        }
        return dataPoint.increment();
    }

    /**
     * Increment the counter identified by 'key' in <code> value </ code>. If no
     * counter exists for the specified identifier, one is created automatically.
     *
     * @param key
     *            String counter identifier
     * @return counter value
     */
    public long increment(String key, long value) {
        DataPoint dataPoint = dataPointMap.get(key);
        if (dataPoint == null) {
            dataPoint = new DataPoint(key);
            DataPoint oldDataPoint = putIfAbsent(dataPointMap, key, dataPoint);
            if (oldDataPoint != null) {
                dataPoint = oldDataPoint;
            }
        }
        return dataPoint.increment(value);
    }

    /**
     * Set the counter value identified by 'key' to <code> value </ code>. If no
     * counter exists for the specified identifier, one is created automatically.
     *
     * @param key
     *            String counter identifier
     * @param value
     *            New value for the counter
     * @return counter value
     */
    public long setValue(String key, long value) {
        DataPoint dataPoint = dataPointMap.get(key);
        if (dataPoint == null) {
            dataPoint = new DataPoint(key);
            DataPoint oldDataPoint = putIfAbsent(dataPointMap, key, dataPoint);
            if (oldDataPoint != null) {
                dataPoint = oldDataPoint;
            }
        }
        return dataPoint.set(value);
    }

    /**
     * Obtiene valor actual del contador de mensajes para el mensaje identificado
     * por "key".
     *
     * @param key
     *            Nombre asociado al mensaje.
     * @return Valor del contador.
     */
    public long getCounter(String key) {
        DataPoint dataPoint = dataPointMap.get(key);
        return (dataPoint != null) ? dataPoint.getCount() : 0;
    }

    /**
     * Returns a list with all named data points of this counter.
     *
     * @return returns a list with all named data points of this counter.
     */
    public List<DataPoint> getDataPoints() {
        List<DataPoint> result = new ArrayList<DataPoint>(dataPointMap.size());
        for (DataPoint counterData : dataPointMap.values()) {
            result.add(counterData.clone());
        }
        return result;
    }

    /**
     * Returns a map with all named data points of this counter.
     *
     * @return returns a map with all named data points of this counter.
     */
    public Map<String, DataPoint> getDataPointsMap() {
        Map<String, DataPoint> map = new HashMap<String, DataPoint>();
        for (Map.Entry<String, DataPoint> d : dataPointMap.entrySet()) {
            map.put(d.getKey(), d.getValue().clone());
        }
        return map;
    }

    public void clear() {
        this.dataPointMap.clear();
    }

    // -----------------------------------------------------------------------
    // -- DataPoint Class ----------------------------------------------------
    // -----------------------------------------------------------------------
    /**
     * Class that contains the information of a single counter data point.
     */
    public static final class DataPoint implements Serializable, Cloneable {

        private static final long serialVersionUID = 1L;

        private final String name;

        private long counter;

        private long lastTime;

        private long min = Long.MAX_VALUE;

        private long max = Long.MIN_VALUE;

        public DataPoint(String name) {
            this.name = name;
        }

        /**
         * Returns the value of the "Name" property.
         *
         * @return
         */
        public String getName() {
            return name;
        }

        /**
         * Returns the value of the "LastTime" property.
         *
         * @return
         */
        public synchronized long getLastTime() {
            return lastTime;
        }

        /**
         * Returns the value of the "Count" property.
         *
         * @return
         */
        public synchronized long getCount() {
            return counter;
        }

        /**
         * Increments by one the count and returns the resulting value.
         *
         * @return
         */
        public synchronized long increment() {
            lastTime = System.currentTimeMillis();
            return ++counter;
        }

        /**
         * Increments by increment the count and returns the resulting value.
         *
         * @param value
         */
        public synchronized long increment(long value) {
            lastTime = System.currentTimeMillis();
            counter = (counter + value);
            evaluateMinMax(value);
            return counter;
        }

        /**
         * Set the count value and returns the result.
         *
         * @param value
         *            The new value
         * @return The new value
         */
        public synchronized long set(long value) {
            lastTime = System.currentTimeMillis();
            counter = value;
            evaluateMinMax(value);
            return counter;
        }

        private void evaluateMinMax(long value) {
            this.min = Math.min(this.min, value);
            this.max = Math.max(this.max, value);
        }

        /**
         * Creates and returns a copy of this object.
         */
        @Override
        public synchronized DataPoint clone() {
            try {
                return (DataPoint) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new InternalError();
            }
        }

        /**
         * @return the min
         */
        public long getMin() {
            return min;
        }

        /**
         * @return the max
         */
        public long getMax() {
            return max;
        }

        /**
         * Returns a string representation of this object.
         */
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder(super.toString());
            sb.append(", Name=[").append(name).append(']');
            sb.append(", LastTime=[").append(lastTime).append(']');
            sb.append(", Counter=[").append(counter).append(']');
            sb.append(", Min=[").append(min).append(']');
            sb.append(", Max=[").append(max).append(']');
            return sb.toString();
        }
    }

    /**
     * Old java version compatibility.
     *
     * @param map
     * @param key
     * @param value
     * @return
     */
    public static DataPoint putIfAbsent(ConcurrentHashMap<String, DataPoint> map, String key, DataPoint value) {
        if (!map.containsKey(key))
            return map.put(key, value);
        else
            return map.get(key);
    }
}
