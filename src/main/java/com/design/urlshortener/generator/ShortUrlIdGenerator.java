package com.design.urlshortener.generator;

import java.time.Instant;

import static com.design.urlshortener.constant.Constants.BASE_EPOCH_TIME;
import static com.design.urlshortener.constant.Constants.DATA_CENTER_ID_BITS;
import static com.design.urlshortener.constant.Constants.INSTANCE_ID_BITS;
import static com.design.urlshortener.constant.Constants.SEQUENCE_BITS;

public class ShortUrlIdGenerator {
    private static final long instanceIdShift = SEQUENCE_BITS;
    private static final long datacenterIdShift = SEQUENCE_BITS + INSTANCE_ID_BITS;
    private static final long timestampLeftShift = SEQUENCE_BITS + INSTANCE_ID_BITS + DATA_CENTER_ID_BITS;
    private static final long sequenceMask = ~(-1L << SEQUENCE_BITS);
    private long lastTimestamp = -1L;
    private long sequence = 0L;

    private final long instanceId;
    private final long dataCenterId;

    public ShortUrlIdGenerator(final long instanceId,
                               final long dataCenterId) {
        this.instanceId = instanceId;
        this.dataCenterId = dataCenterId;
    }

    public long getId() {
        synchronized (this) {
            long timestamp = getCurrentTime();

            if (timestamp < lastTimestamp) {
                return -1L;
            }

            if (lastTimestamp == timestamp) {
                sequence = (sequence + 1) & sequenceMask;

                if (sequence == 0) {
                    timestamp = tillNextSecond(lastTimestamp);
                }
            } else {
                sequence = 0;
            }

            lastTimestamp = timestamp;
            return ((timestamp - BASE_EPOCH_TIME) << timestampLeftShift) |
                    (dataCenterId << datacenterIdShift) |
                    (instanceId << instanceIdShift) |
                    sequence;
        }
    }

    private long tillNextSecond(final long lastTimestamp) {
        long timestamp = getCurrentTime();

        while (timestamp <= lastTimestamp) {
            timestamp = getCurrentTime();
        }
        return timestamp;
    }

    private long getCurrentTime() {
        return Instant.now().getEpochSecond();
    }
}
