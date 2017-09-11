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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Logging of Processing Times.
 * <p>
 * This class is responsible for recording process times identified by a String,
 * with the aim of measuring the processing times of some procedure or message.
 * <p>
 * The time register is grouped into a table. This table defines a series of
 * fixed interval ranges from a point of start, and save the number of
 * occurrences whose processing time is within each range.
 * <p>
 * This array will be of final size, NumFields + 2, where array [0] represents
 * occurrences from 0 to startPoint, and fix [numFields] occurrences from the
 * end point (calculated to infinity. For the rest of the array fields, array
 * [i] represents field i data as shown shows in the example. Example:
 * <p>
 * <blockquote>
 *
 * <pre>
 * Start Point = 400
 * Num Fields = 5
 * Field Range = 200
 *
 * Arreglo Resultante:
 *  [0]  [1]  [2]  [3]  [4]  [5]
 * {18 , 25 , 31 , 12 , 83 , 105 }
 * 0  400  600  800  1000 1200   +
 * </pre>
 *
 * </blockquote>
 *
 * The above arrangement indicates that 18 entrances took between 0 and 400, 25
 * entries took between 400 and 600, 31 took between 600 and 800, 12 between 800
 * and 1000, 83 between 1000 and 1200, and 105 took 1200 or more.
 * <p>
 * The information is stored in a hashmap, which is responsible for associating
 * the data to a String identifier. Data is stored in a pair of two arrays: the
 * first (int []) stores the number of messages in each range, and the according
 * to the total sum of times for that field.
 * <p>
 * While the <code> registerTime </ code> function receives time in nanoseconds,
 * The accuracy of the timer is microseconds.
 * <p>
 * All TimerRegistry methods are Thread-safe.
 *
 * I thank my co-workers who were the creators of this class.
 * 
 * @author Eugenio Contreras
 */
public class TimerRegistry {

    /** Inicio de los intervalos (microsegundos). */
    private final long offsetSize;

    /** Numero de intervalos de detalle */
    private final int intervalCount;

    /** Tamaño de cada intervalo (microsegundos). */
    private final long intervalSize;

    /** Fin de los intervalos (microsegundos). */
    private final long endSize;

    /** Mapping the nombre a pares de datos. */
    private final HashMap<String, DataPoint> dataPointMap;

    /**
     * Crea una nueva instancia del registro de tiempos.
     *
     * @param offsetSize
     *            Inicio de los intervalos (microsegundos).
     * @param intervalCount
     *            Numero de intervalos de detalle.
     * @param intervalSize
     *            Tamaño de cada intervalo (microsegundos).
     * @throws IllegalArgumentException
     *             Si el valor de algun parametro is invalido.
     */
    public TimerRegistry(long offsetSize, int intervalCount, long intervalSize) {
        if (offsetSize < 0)
            throw new IllegalArgumentException("offsetSize is negative");
        if (intervalCount < 0)
            throw new IllegalArgumentException("intervalCount is negative");
        if (intervalSize <= 0)
            throw new IllegalArgumentException("intervalSize is not positive");
        this.offsetSize = offsetSize;
        this.intervalCount = intervalCount;
        this.intervalSize = intervalSize;
        this.endSize = offsetSize + (intervalCount * intervalSize);
        dataPointMap = new HashMap<String, DataPoint>(5, 0.5f);
    }

    /**
     * @return the offset size (microsegundos).
     */
    public long getOffsetSize() {
        return offsetSize;
    }

    /**
     * @return the number of intervals.
     */
    public int getIntervalCount() {
        return intervalCount;
    }

    /**
     * @return the interval size (microsegundos).
     */
    public long getIntervalSize() {
        return intervalSize;
    }

    /**
     * Valida si los parametros de configuracion son iguales a los entregados.
     */
    public boolean validate(long offsetSize, int intervalCount, long intervalSize) {
        return (this.intervalCount == intervalCount && this.intervalSize == intervalSize
                && this.offsetSize == offsetSize);
    }

    /**
     * Calcula el indice del arreglo para el tiempo entregado.
     *
     * @param deltaMicro
     *            Tiempo de proceso (microsegundos).
     */
    private int computeIndex(long deltaMicro) {
        if (deltaMicro < offsetSize)
            return 0;
        if (deltaMicro >= endSize)
            return intervalCount + 1;
        return (int) ((deltaMicro - offsetSize) / intervalSize) + 1;
    }

    /**
     * Registra un tiempo de proceso para el string indicado.
     *
     * @param name
     *            String identificador.
     * @param deltaNano
     *            Tiempo de proceso (nanosegundos).
     */
    public synchronized void registerTime(String name, long deltaNano) {
        if (deltaNano < 0)
            deltaNano = 0;
        final long deltaMicro = deltaNano / 1000;
        DataPoint dataPoint = getDataPoint(name);
        final int index = computeIndex(deltaMicro);
        dataPoint.increment(index, deltaMicro);
    }

    public synchronized void registerTimeMillis(String name, long deltaMillisecons) {
        this.registerTime(name, TimeUnit.NANOSECONDS.convert(deltaMillisecons, TimeUnit.MILLISECONDS));
    }

    /**
     * Obtiene los datos de un timer especifico.
     */
    public synchronized DataPoint getDataPoint(String name) {
        DataPoint dataPoint = dataPointMap.get(name);
        if (dataPoint == null) {
            dataPoint = new DataPoint(name, intervalCount);
            dataPointMap.put(name, dataPoint);
        }
        return dataPoint;
    }

    /**
     * Obtiene un arreglo con todos los datos contenidos en el registro.
     */
    public synchronized List<DataPoint> getDataPoints() {
        List<DataPoint> result = new ArrayList<DataPoint>(dataPointMap.size());
        for (DataPoint point : dataPointMap.values())
            result.add(point.clone());
        return result;
    }

    /**
     * Reinicia las estructuras de datos del registro.
     */
    public synchronized void reset() {
        dataPointMap.clear();
    }

    /**
     * Obtiene un arreglo con todos los datos contenidos en el registro y luego
     * resetea las estructuras de datos atomicamente.
     * <p>
     * Este metodo es equivalente a llamar a getDataArray() seguido de reset(), con
     * la diferencia que en este metodo se ejecutan en forma atomica.
     */
    public synchronized List<DataPoint> getDataPointsAndReset() {
        List<DataPoint> result = getDataPoints();
        reset();
        return result;
    }

    // -----------------------------------------------------------------------
    // -- DataPoint Class ----------------------------------------------------
    // -----------------------------------------------------------------------
    /**
     * Class that contains the information of a single timer data point.
     */
    public static final class DataPoint implements Serializable, Cloneable {

        private static final long serialVersionUID = 1L;

        private final String name;

        private Interval[] intervals;

        private long lastTime;

        /**
         * Constructs a new instance.
         */
        public DataPoint(String name, int intervalCount) {
            if (name == null)
                throw new NullPointerException("name is null");
            if (intervalCount < 0)
                throw new IllegalArgumentException("intervalCount is negative");
            this.name = name;
            intervals = new Interval[intervalCount + 2];
            for (int i = intervals.length - 1; i >= 0; i--)
                intervals[i] = new Interval();
        }

        /**
         * Returns the value of the "Name" property.
         */
        public String getName() {
            return name;
        }

        /**
         * Returns the value of the "LastTime" property.
         */
        public synchronized long getLastTime() {
            return lastTime;
        }

        /**
         * Returns the total number of intervals.
         */
        public long getCount() {
            return intervals.length;
        }

        /**
         * Returns the interval indexed by supplied index.
         */
        public Interval getInterval(int index) {
            return intervals[index];
        }

        /**
         * Returns a list with all defined intervals.
         */
        public synchronized List<Interval> getIntervals() {
            List<Interval> result = new ArrayList<Interval>(intervals.length);
            for (Interval interval : intervals)
                result.add(interval.clone());
            return result;
        }

        /**
         * Increments the interval indexed by specified index.
         */
        public void increment(int index, long deltaMicro) {
            lastTime = System.currentTimeMillis();
            Interval interval = getInterval(index);
            interval.increment(deltaMicro);
        }

        /**
         * Creates and returns a copy of this object.
         */
        @Override
        public DataPoint clone() {
            try {
                DataPoint copy = (DataPoint) super.clone();
                copy.intervals = new Interval[intervals.length];
                for (int i = intervals.length - 1; i >= 0; i--)
                    copy.intervals[i] = intervals[i].clone();
                return copy;
            } catch (CloneNotSupportedException e) {
                throw new InternalError();
            }
        }

        /**
         * Returns a string representation of this object.
         */
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder(super.toString());
            sb.append(", Name=[").append(name).append(']');
            sb.append(", LastTime=[").append(lastTime).append(']');
            sb.append(", Intervals=[").append(Arrays.toString(intervals)).append(']');
            return sb.toString();
        }
    }

    // -----------------------------------------------------------------------
    // -- Interval Class -----------------------------------------------------
    // -----------------------------------------------------------------------
    /**
     * Class that contains the information of a single interval.
     */
    public static final class Interval implements Serializable, Cloneable {

        private static final long serialVersionUID = 1L;

        private long sumMicro;

        private int count;

        /**
         * Constructs a new instance.
         */
        public Interval() {
        }

        /**
         * Returns the value of the "Sum" property.
         */
        public synchronized long getSum() {
            return sumMicro;
        }

        /**
         * Returns the value of the "Count" property.
         */
        public synchronized int getCount() {
            return count;
        }

        /**
         * Increments the total sum by delta microseconds.
         */
        public synchronized void increment(long deltaMicro) {
            sumMicro += deltaMicro;
            count++;
        }

        /**
         * Creates and returns a copy of this object.
         */
        @Override
        public synchronized Interval clone() {
            try {
                return (Interval) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new InternalError();
            }
        }

        /**
         * Returns a string representation of this object.
         */
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder(super.toString());
            sb.append(", Count=[").append(count).append(']');
            sb.append(", Sum=[").append(sumMicro).append(']');
            return sb.toString();
        }
    }
}
