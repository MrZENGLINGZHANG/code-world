package com.sh.fbs.oms.orders.domain.repo;

import com.sh.fbs.oms.orders.domain.model.FulfilOrderEntity;

import java.util.List;

public interface FulfilOrderEntityRepo {

    FulfilOrderEntity findById(String orderId,int shopId);

    void save(FulfilOrderEntity fulfilOrderEntity);

    void batchSave(List<FulfilOrderEntity> list);



}
