package com.sh.fbs.oms.orders.infra.impl.db;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.sh.fbs.oms.orders.domain.model.FulfilOrderEntity;
import com.sh.fbs.oms.orders.domain.repo.FulfilOrderEntityRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;


@Repository
public class FulfilOrderEntityRepoImplDB implements FulfilOrderEntityRepo {

    @Autowired
    private FulfilOrderDOMapper fulfilOrderDOMapper;

    private static final Logger logger = LoggerFactory.getLogger(FulfilOrderEntityRepoImplDB.class);

    @Override
    public FulfilOrderEntity findById(String orderId,int shopId){
        Wrapper<FulfilOrderDO> wrapper = Wrappers.<FulfilOrderDO>lambdaQuery()
                .eq(FulfilOrderDO::getOrderId, orderId)
                .eq(FulfilOrderDO::getShopId, shopId);
        FulfilOrderDO orderDO =fulfilOrderDOMapper.selectOne(wrapper);
        FulfilOrderEntity fulfilOrderEntity = new FulfilOrderEntity();
        BeanUtils.copyProperties(orderDO,fulfilOrderEntity);
        return fulfilOrderEntity;
    }

    @Override
    public void save(FulfilOrderEntity fulfilOrderEntity) {
        logger.info("Save FulfilOrderEntity: {}", fulfilOrderEntity);
        FulfilOrderDO fulfilOrderDO = new FulfilOrderDO();
        BeanUtils.copyProperties(fulfilOrderEntity,fulfilOrderDO);
        int result = fulfilOrderDOMapper.insert(fulfilOrderDO);
        if(result <= 0){
            throw new RuntimeException("Insert FulfilOrderEntity failed");
        }
    }

    @Override
    public void batchSave(List<FulfilOrderEntity> list) {
        List<FulfilOrderDO> fulfilOrderDOS = new ArrayList<FulfilOrderDO>();
        BeanUtils.copyProperties(list,fulfilOrderDOS);

    }

}
