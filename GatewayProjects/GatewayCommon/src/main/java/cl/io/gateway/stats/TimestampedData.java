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

/**
 * Class that allows to store an object of any type and associate a timestamp.
 *
 * I thank my co-workers who were the creators of this class.
 *
 * @author egacl
 * @param <T>
 *            Type of data to be stored
 */
public class TimestampedData<T extends Object> {

    private final T data;

    private final long timestamp;

    /**
     * Creates a new instance containing the given data object and associates the
     * timestamp corresponding to the system time.
     *
     * @param data
     *            data to store
     */
    public TimestampedData(T data) {
        this(data, System.currentTimeMillis());
    }

    /**
     * Creates a new instance containing the provided data object, associated to
     * timestamp delivered.
     *
     * @param data
     *            data to store
     * @param timestamp
     *            associated timestamp
     */
    public TimestampedData(T data, long timestamp) {
        this.data = data;
        this.timestamp = timestamp;
    }

    public T getData() {
        return data;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return String.format("Data:<%s> TimeStamp:<%d>", data, timestamp);
    }
}
