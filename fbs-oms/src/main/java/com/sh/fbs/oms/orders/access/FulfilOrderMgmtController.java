package com.sh.fbs.oms.orders.access;

import com.sh.fbs.commom.result.Result;
import com.sh.fbs.commom.result.ResultUtils;
import jakarta.websocket.server.PathParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("/api/fbs/oms/fulfil_order")
@RestController
public class FulfilOrderMgmtController {

    @PostMapping("/create")
    @ResponseBody
    public Result CreateOrder(@RequestBody OrderRequestParam param) {
        return ResultUtils.buildSuccessResult();
    }

    @GetMapping("/get")
    @ResponseBody
    public Result getOrder(@PathParam("id") String id) {
        log.info("get order id {}", id);
        return ResultUtils.buildSuccessResult();
    }

}
