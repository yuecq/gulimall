package com.atguigu.gulimall.pms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gulimall.pms.entity.AttrEntity;
import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.QueryCondition;


/**
 * 商品属性
 *
 * @author yuechuan
 * @email 1012864906@qq.com
 * @date 2019-08-01 20:26:51
 */
public interface AttrService extends IService<AttrEntity> {

    PageVo queryPage(QueryCondition params);


    PageVo queryPageCatelogBaseAttrs(QueryCondition queryCondition, Long catId, Integer attrType);
}

