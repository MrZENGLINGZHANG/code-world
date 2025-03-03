package com.sh.fbs.gateway.session;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

@Builder
@Data
public class USMgmtBuildParam {
    @NotNull
    private UserInfo userInfo;
    @Range(min = 1,max = 4)
    private int deviceType;
    @NotEmpty
    private String deviceInfo;
    @NotEmpty
    private String ip;
    @NotEmpty
    private String ipLocation;
}



