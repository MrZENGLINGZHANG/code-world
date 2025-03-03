package com.sh.fbs.gateway.session;

import lombok.Getter;
/**
 * @author sh
 *
 */
@Getter
public enum DeviceType {
    PC(1),
    MOBILE(2),
    PAD(3),
    WECHAT(4);
    private final int code;
    DeviceType(int code) {
        this.code = code;
    }

    public static DeviceType valueOf(int code) {
        for (DeviceType deviceType : DeviceType.values()) {
            if (deviceType.code == code) {
                return deviceType;
            }
        }
        return null;
    }

}
