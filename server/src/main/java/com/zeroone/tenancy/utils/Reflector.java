package com.zeroone.tenancy.utils;

public class Reflector {


    @SuppressWarnings("unchecked")
     public static <T extends Throwable> void sneakyThrow(final Throwable t) throws T {
        throw (T) t;
    }
}
