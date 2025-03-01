package com.sh.fbs.oms.orders.domain.service;

import com.sh.fbs.oms.orders.domain.model.FulfilOrderEntity;
import com.sh.fbs.oms.orders.domain.repo.FulfilOrderEntityRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class FulfilOrderDomainService {
    @Autowired
    private FulfilOrderEntityRepo fulfilOrderEntityRepo;

    public FulfilOrderEntity getById(String id,int shopId) {
       return fulfilOrderEntityRepo.findById(id,shopId);
    }




}
