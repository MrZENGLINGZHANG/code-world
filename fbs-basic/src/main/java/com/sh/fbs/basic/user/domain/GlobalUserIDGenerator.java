package com.sh.fbs.basic.user.domain;

public interface GlobalUserIDGenerator {
    public long nextID() throws Exception;
    public long[] nextID(int nums) throws Exception;
}
