package com.sh.fbs.gateway.session;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class USMgmtRefreshParam {
    @NotNull
    private Long userId;
    @NotNull
    private int deviceType;
    @NotEmpty
    private String sessionId;

}
