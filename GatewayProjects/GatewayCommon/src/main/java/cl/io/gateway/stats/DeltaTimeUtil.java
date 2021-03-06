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
 * Utility to calculate time ranges.
 *
 * @author egacl
 */
public class DeltaTimeUtil {

    private Long initTime = null;

    private Long endTime = null;

    public DeltaTimeUtil() {
        this.initTime = System.currentTimeMillis();
    }

    public DeltaTimeUtil(final long initTime) {
        this.initTime = initTime;
    }

    public DeltaTimeUtil init() {
        this.initTime = System.currentTimeMillis();
        return this;
    }

    public DeltaTimeUtil init(final long initTime) {
        this.initTime = initTime;
        return this;
    }

    public DeltaTimeUtil end() {
        this.endTime = System.currentTimeMillis();
        return this;
    }

    public DeltaTimeUtil end(final long endTime) {
        this.endTime = endTime;
        return this;
    }

    public long delta() {
        if (this.endTime == null) {
            this.end();
        }
        return this.endTime - this.initTime;
    }

    public long delta(final long endTime) {
        this.endTime = endTime;
        return this.endTime - this.initTime;
    }

    public long getDeltaAndThenReset() {
        final long delta = this.delta();
        this.reset();
        return delta;
    }

    public long getDeltaAndThenReset(final long endTime) {
        final long delta = this.delta(endTime);
        this.reset();
        return delta;
    }

    public DeltaTimeUtil reset() {
        this.initTime = System.currentTimeMillis();
        this.endTime = null;
        return this;
    }
}
