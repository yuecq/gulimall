package com.atguigu.gulimall.sms.dao;

import com.atguigu.gulimall.sms.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author yuechuan
 * @email 1012864906@qq.com
 * @date 2019-08-01 20:40:34
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
