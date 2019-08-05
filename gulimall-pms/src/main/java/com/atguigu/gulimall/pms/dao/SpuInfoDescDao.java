package com.atguigu.gulimall.pms.dao;

import com.atguigu.gulimall.pms.entity.SpuInfoDescEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * spu信息介绍
 * 
 * @author yuechuan
 * @email 1012864906@qq.com
 * @date 2019-08-01 20:26:50
 */
@Mapper
public interface SpuInfoDescDao extends BaseMapper<SpuInfoDescEntity> {

    /**
     * 强制要求
     * @param entity
     */
    void insertInfo(@Param("entity" )SpuInfoDescEntity entity);
}
