package com.sh.fbs.oms.orders.access;

import com.sh.fbs.commom.result.Result;
import com.sh.fbs.commom.result.ResultUtils;
import org.springframework.web.bind.annotation.*;

@RestController("/api/fbs/oms/fulfil_order")
public class FulfilOrderMgmtController {

    @PostMapping("/create")
    @ResponseBody
    public Result CreateOrder(@RequestBody OrderRequestParam param) {
        return ResultUtils.buildSuccessResult();
    }

}
