package com.atguigu.gulimall.pms.service.impl;

import com.atguigu.gulimall.commons.to.SkuSaleInfoTo;
import com.atguigu.gulimall.commons.utils.AppUtils;
import com.atguigu.gulimall.pms.dao.*;
import com.atguigu.gulimall.pms.entity.*;
import com.atguigu.gulimall.pms.feign.SmsSkuSaleInfoFeignService;
import com.atguigu.gulimall.pms.vo.BaseAttrVo;
import com.atguigu.gulimall.pms.vo.SaleAttrVo;
import com.atguigu.gulimall.pms.vo.SkuVo;
import com.atguigu.gulimall.pms.vo.SpuAllSaveVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.Query;
import com.atguigu.gulimall.commons.bean.QueryCondition;

import com.atguigu.gulimall.pms.service.SpuInfoService;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    SmsSkuSaleInfoFeignService smsSkuSaleInfoFeignService;

    @Autowired
    SpuInfoDao spuInfoDao;

    @Autowired
    SpuInfoDescDao spuInfoDescDao;

    //pms_product_attr_value
    @Autowired
    ProductAttrValueDao spuAttrValueDao;

    @Autowired
    SkuInfoDao skuInfoDao;

    @Autowired
    SkuImagesDao imagesDao;

    @Autowired
    AttrDao attrDao;

    @Autowired
    SkuSaleAttrValueDao skuSaleAttrValueDao;

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

    @Override
    public void spuBigSaveAll(SpuAllSaveVo spuInfo) {
        //1、存spu的基本信息
        //1.1）、存spu的基本信息
        Long supId = this.saveSpuBaseInfo(spuInfo);
        //1.2）、保存spu的所有图片信息
        this.saveSpuInfoImages(supId,spuInfo.getSpuImages());
        //2、保存spu的基本属性信息
        List<BaseAttrVo> baseAttrs = spuInfo.getBaseAttrs();
        this.saveSpuBaseAttrs(supId, baseAttrs);

        //3、保存sku以及sku的营销相关信息
        this.saveSkuInfos(supId, spuInfo.getSkus());

    }

    //负责解析出数据做出相应的业务
    @Override
    public Long saveSpuBaseInfo(SpuAllSaveVo spuInfo) {
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(spuInfo,spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUodateTime(new Date());

        spuInfoDao.insert(spuInfoEntity);
        return spuInfoEntity.getId();
    }

    @Override
    public void saveSpuInfoImages(Long supId, String[] spuImages) {
        SpuInfoDescEntity entity = new SpuInfoDescEntity();
        entity.setSpuId(supId);
        entity.setDecript(AppUtils.arrayToStringWithSeperator(spuImages, ","));
        spuInfoDescDao.insertInfo(entity);
    }

    @Override
    public void saveSpuBaseAttrs(Long supId, List<BaseAttrVo> baseAttrs) {
        List<ProductAttrValueEntity> allSave = new ArrayList<>();
        for (BaseAttrVo baseAttr : baseAttrs) {
            ProductAttrValueEntity entity = new ProductAttrValueEntity();
            entity.setAttrId(baseAttr.getAttrId());
            entity.setAttrName(baseAttr.getAttrName());
            String[] selected = baseAttr.getValueSelected();
            entity.setAttrValue(AppUtils.arrayToStringWithSeperator(selected, ","));
            entity.setAttrSort(0);
            entity.setQuickShow(1);
            entity.setSpuId(supId);
            allSave.add(entity);

        }
        spuAttrValueDao.insertBatch(allSave);

    }

    //保存sku的所有详情
    @Override
    public void saveSkuInfos(Long supId, List<SkuVo> skus) {

        //0、查出这个spu的信息
        SpuInfoEntity spuInfo = this.getById(supId);
        List<SkuSaleInfoTo> tos = new ArrayList<>();

        //1、保存sku的info信息
        for (SkuVo skuVo : skus) {
            String [] images = skuVo.getImages();
            SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
            skuInfoEntity.setBrandId(spuInfo.getBrandId());
            skuInfoEntity.setCatalogId(spuInfo.getCatalogId());
            skuInfoEntity.setPrice(skuVo.getPrice());
            skuInfoEntity.setSkuCode(UUID.randomUUID().toString().substring(0, 5).toUpperCase());
            if(images!=null && images.length>0){
                skuInfoEntity.setSkuDefaultImg(skuVo.getImages()[0]);
            }
            skuInfoEntity.setSkuDesc(skuVo.getSkuDesc());
            skuInfoEntity.setSkuName(skuVo.getSkuName());
            skuInfoEntity.setSkuSubtitle(skuVo.getSkuSubtitle());
            skuInfoEntity.setSpuId(supId);
            skuInfoEntity.setSkuTitle(skuVo.getSkuTitle());
            skuInfoEntity.setWeight(skuVo.getWeight());

            //保存sku的基本信息
            skuInfoDao.insert(skuInfoEntity);
            Long skuId = skuInfoEntity.getSkuId();

            //2、保存sku的所有对应图片
            for (int i = 0; i <images.length ; i++) {
                SkuImagesEntity imagesEntity = new SkuImagesEntity();
                imagesEntity.setSkuId(skuId);
                imagesEntity.setDefaultImg(i==0? 1:0);
                imagesEntity.setImgUrl(images[i]);
                imagesEntity.setImgSort(0);
                imagesDao.insert(imagesEntity);
            }

            //3、当前sku的所有销售属性组合保存起来
            List<SaleAttrVo> saleAttrs = skuVo.getSaleAttrs();
            for (SaleAttrVo attrVo : saleAttrs) {
                //查询当前属性的信息
                SkuSaleAttrValueEntity entity = new SkuSaleAttrValueEntity();
                entity.setAttrId(attrVo.getAttrId());

                //查出这个属性的真正信息
                AttrEntity attrEntity = attrDao.selectById(attrVo.getAttrId());
                entity.setAttrName(attrEntity.getAttrName());
                entity.setAttrSort(0);
                entity.setAttrValue(attrVo.getAttrValue());
                entity.setSkuId(skuId);
                //sku与销售属性的关联关系
                skuSaleAttrValueDao.insert(entity);
            }

            //以上都是pms系统完成的工作

            //以下需要由sms完成，保存每一个sku的相关优惠数据
            SkuSaleInfoTo info = new SkuSaleInfoTo();
            BeanUtils.copyProperties(skuVo,info);
            info.setSkuId(skuId);
            tos.add(info);

        }

        //2、发给sms，让他去处理。我们不管
        smsSkuSaleInfoFeignService.saveSkuSaleInfos(tos);

    }


}