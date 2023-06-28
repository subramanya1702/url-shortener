package com.design.urlshortener.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {

    public static final String BASE_62_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    public static final int BASE_62_CHARACTERS_SIZE = 62;
    public static final long BASE_EPOCH_TIME = 946684800L;
    public static final long INSTANCE_ID_BITS = 5L;
    public static final long DATA_CENTER_ID_BITS = 5L;
    public static final long SEQUENCE_BITS = 10L;
}
