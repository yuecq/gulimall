package com.atguigu.gulimall.pms.service.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.Query;
import com.atguigu.gulimall.commons.bean.QueryCondition;

import com.atguigu.gulimall.pms.dao.SpuInfoDao;
import com.atguigu.gulimall.pms.entity.SpuInfoEntity;
import com.atguigu.gulimall.pms.service.SpuInfoService;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public PageVo queryPageByCatId(QueryCondition queryCondition, Long catId) {

        //1、封装查询条件
       QueryWrapper<SpuInfoEntity>  wrapper = new QueryWrapper<>();
       if(catId !=0){  //==0 查全站的
           wrapper.eq("catalog_id",catId);
           if(!StringUtils.isEmpty(queryCondition.getKey())){

               wrapper.and(obj->{
                   obj.like("spu_name",queryCondition.getKey());
                   obj.or().like("id",queryCondition.getKey());
                   return obj;
               });

           }

       }

        //2、封装翻页条件
        IPage<SpuInfoEntity> page = new Query<SpuInfoEntity>().getPage(queryCondition);

        //3、去数据库查询

        IPage<SpuInfoEntity> iPage = this.page(page, wrapper);

        PageVo vo = new PageVo(iPage);
        return vo;
    }

}