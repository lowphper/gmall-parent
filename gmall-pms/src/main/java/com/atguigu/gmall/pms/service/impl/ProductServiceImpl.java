package com.atguigu.gmall.pms.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.constant.RedisCacheConstant;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.mapper.*;
import com.atguigu.gmall.pms.service.ProductService;
import com.atguigu.gmall.pms.vo.PmsProductParam;
import com.atguigu.gmall.pms.vo.PmsProductQueryParam;
import com.atguigu.gmall.search.GmallSearchService;
import com.atguigu.gmall.to.es.EsProduct;
import com.atguigu.gmall.to.es.EsProductAttributeValue;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.sun.org.apache.bcel.internal.generic.NEW;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.SetParams;


import java.text.DecimalFormat;
import java.text.NumberFormat;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * <p>
 * 商品信息 服务实现类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
@Service
@Component
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {
    ThreadLocal<Product> productThreadLocal = new ThreadLocal<Product>();

    @Autowired
    ProductLadderMapper productLadderMapper;//阶梯价格

    @Autowired
    ProductFullReductionMapper productFullReductionMapper;//满减

    @Autowired
    MemberPriceMapper memberPriceMapper;//会员价格

    @Autowired
    ProductAttributeValueMapper productAttributeValueMapper;//商品属性

    @Autowired
    ProductCategoryMapper productCategoryMapper;//商品分类

    @Autowired
    SkuStockMapper skuStockMapper;//sku

    @Autowired
    ProductMapper productMapper;
    @Autowired
    JedisPool jedisPool;

    @Reference(version = "1.0")
    GmallSearchService gmallSearchService;

    /**
     * 分页查询商品列表
     *
     * @param productQueryParam
     * @param pageSize
     * @param pageNum
     * @return
     */
    @Override
    public Map<String, Object> selectPrducts(PmsProductQueryParam query, Integer pageSize, Integer pageNum) {
        QueryWrapper<Product> qw = new QueryWrapper<>();
        if (query.getPublishStatus() != null) {
            qw.eq("publish_status", query.getPublishStatus());
        }
        if (query.getKeyword() != null) {
            qw.like("keywords", query.getKeyword());
        }
        if (query.getBrandId() != null) {
            qw.eq("brand_id", query.getBrandId());
        }
        if (query.getProductCategoryId() != null) {
            qw.eq("product_category_id", query.getProductCategoryId());
        }
        if (query.getProductSn() != null) {
            qw.eq("product_sn", query.getProductSn());
        }
        if (query.getVerifyStatus() != null) {
            qw.eq("verify_status", query.getVerifyStatus());
        }
        qw.eq("delete_status", 0);
        Page<Product> page = new Page<>(pageNum, pageSize);
        baseMapper.selectPage(page, qw);
        HashMap<String, Object> map = new HashMap<>();
        map.put("total", page.getTotal());
        map.put("totalPage", page.getPages());
        map.put("pageSize", pageSize);
        map.put("list", page.getRecords());
        map.put("pageNum", pageNum);
        return map;
    }

    /**
     * 更新商品
     *
     * @param id
     * @param productParam
     * @return
     */
    @Override
    public boolean updateProductById(Long id, PmsProductParam productParam) {
        Product product = new Product();
        BeanUtils.copyProperties(productParam, product);
        product.setId(id);
        int i = baseMapper.updateById(product);
        return i > 0;
    }

    /**
     * 根据货号或名称查询商品
     *
     * @param keyword
     * @return
     */
    @Override
    public Map<String, Object> selectProductByNameOrProductSn(String keyword) {
        QueryWrapper<Product> queryWrapper = new QueryWrapper();
        queryWrapper.eq("name", keyword).or().eq("product_sn", keyword);
        Page<Product> page = new Page<>(1, 5);
        baseMapper.selectPage(page, queryWrapper);
        HashMap<String, Object> map = new HashMap<>();
        map.put("total", page.getTotal());
        map.put("totalPage", page.getPages());
        map.put("pageSize", 5);
        map.put("list", page.getRecords());
        map.put("pageNum", 1);
        return map;

    }

    /**
     * 批量删除商品
     *
     * @param ids
     * @param deleteStatus
     * @return
     */
    @Override
    public boolean deleteProductBatch(List<Long> ids, Integer deleteStatus) {
        boolean b = baseMapper.deleteProductBatch(ids);
        return b;
    }

    /**
     * 批量修改审核状态
     *
     * @param ids
     * @param verifyStatus
     * @param detail
     * @return
     */
    @Override
    public boolean updateVerifyStatusBatch(List<Long> ids, Integer verifyStatus, String detail) {
        boolean b = productMapper.updateVerifyStatusBatch(ids, verifyStatus);
        return b;
    }

    /**
     * 批量修改上下架,在这里将商品信息添加到ES中
     *
     * @param ids
     * @param publishStatus
     * @return
     */
    @Override
    public boolean updatePublishStatusBatch(List<Long> ids, Integer publishStatus) {
        boolean b = false;
        //1 判断上架还是下架
        if (publishStatus == 1) {
            b = publisProduct(ids);
        } else {
            b = LowerProduct(ids);
        }
        return b;
    }

    /**
     * 上架
     *
     * @param ids
     */
    public boolean publisProduct(List<Long> ids) {
        //第一步查询出这些商品spu
        //第二步查询出商品的sku
        //第三布查询出商品的参数
        //第四部，将其发布到ES中
        //--------------------------
        System.out.println("进入这里了");
        ids.forEach((id) -> {
            //查询出spu
            Product product = productMapper.selectById(id);//查询出商品
            //查询出sku
            List<SkuStock> skuList = skuStockMapper.selectList(new QueryWrapper<SkuStock>().eq("product_id", id));
            //查询出属性
            List<EsProductAttributeValue> attributeValueList = productAttributeValueMapper.selectProductAttrValues(id);
            //这不是为了看是否把所有信息到插入到ES中
            AtomicReference<Integer> count = new AtomicReference<Integer>(0);
            //将sku信息添加到ES中
            skuList.forEach((sku) -> {
                EsProduct esProduct = new EsProduct();
                //1.将基本属性添加到esProduct中
                BeanUtils.copyProperties(product, esProduct);
                //2.设置其他值
                esProduct.setName(product.getName() + " " + sku.getSp1() + " " + sku.getSp2() + " " + sku.getSp3());
                esProduct.setPrice(sku.getPrice());
                esProduct.setStock(sku.getStock());
                esProduct.setSale(sku.getSale());
                esProduct.setAttrValueList(attributeValueList);
                esProduct.setId(sku.getId());//直接改为sku的id
                //3保存到ES中
                boolean b = gmallSearchService.saveProductInfoToES(esProduct);
                System.out.println("---" + b);
                count.set(count.get() + 1);
                if (b) {
                    //保存当前的id，list.add(id);
                }
            });
            //判断是否完全上架成功，成功改数据库状态
            if (count.get() == skuList.size()) {
                //修改数据库状态;都是包装类型允许null值
                Product update = new Product();
                update.setId(product.getId());
                update.setPublishStatus(1);
                productMapper.updateById(update);
            } else {
                //成功的撤销操作；来保证业务数据的一致性；
                //es有失败  list.forEach(remove());
                System.out.println("失败");
            }
        });

        return true;
    }

    /**
     * 下架
     *
     * @param ids
     * @return
     */
    public boolean LowerProduct(List<Long> ids) {

        ids.forEach((id) -> {
            Product update = new Product();
            update.setId(id);
            update.setPublishStatus(0);
            productMapper.updateById(update);
        });
        return true;
    }


    /**
     * 批量设为推荐
     *
     * @param ids
     * @param recommendStatus
     * @return
     */
    @Override
    public boolean updateRecommendStatusBatch(List<Long> ids, Integer recommendStatus) {
        boolean b = productMapper.updateRecommendStatusBatch(ids, recommendStatus);
        return b;
    }

    /**
     * 批量设为新品
     *
     * @param ids
     * @param newStatus
     * @return
     */
    @Override
    public boolean updateNewStatusBatch(List<Long> ids, Integer newStatus) {
        boolean b = productMapper.updateNewStatusBatch(ids, newStatus);
        return b;
    }

    /**
     * 保存新商品
     * 1 保存商品基本信息以及商品库存信息pms_product pms_sku_stock
     * 2 保存商品阶梯价格 pms_product_ladder
     * 3 保存商品满减价格pms_product_full_reduction
     * 4 保存商品会员价格pms_member_price
     * 5 商品参数以及自定义规格
     * 6 商品分类数量
     *
     * @param productParam
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean saveProduct(PmsProductParam productParam) {
        ProductServiceImpl currentProxy = (ProductServiceImpl) AopContext.currentProxy();
        //分步调用方法
        //1,保存商品与库存信息
        currentProxy.saveBaseInfo(productParam);
        //2，保存商品阶梯价格
        currentProxy.saveLadderPrice(productParam.getProductLadderList());
        //3,保存商品满减价格
        currentProxy.saveFullReduction(productParam.getProductFullReductionList());
        //4,保存商品会员价格
        currentProxy.saveMemberPrice(productParam.getMemberPriceList());
        //5,保存商品参数和自定义规格
        currentProxy.saveProductAttributeValue(productParam.getProductAttributeValueList());
        //6,商品类别数量加一
        currentProxy.updateProductCategoryCount();
        return true;
    }

    /**
     * 根据商品Id获取商品信息及附加信息
     * * 保存新商品
     * 1 保存商品基本信息以及商品库存信息pms_product pms_sku_stock
     * 2 保存商品阶梯价格 pms_product_ladder
     * 3 保存商品满减价格pms_product_full_reduction
     * 4 保存商品会员价格pms_member_price
     * 5 商品参数以及自定义规格
     * 6 商品分类数量
     *
     * @param id
     * @return
     */

    @Override
    public PmsProductParam getProductInfoById(Long id) {
        //1 获取商品基本信息
        PmsProductParam pmsProductParam = new PmsProductParam();
        Product product = baseMapper.selectById(id);
        BeanUtils.copyProperties(product, pmsProductParam);
        //2 获取商品库存信息
        QueryWrapper<SkuStock> skuQueryWrapper = new QueryWrapper();
        skuQueryWrapper.eq("product_id", id);
        List<SkuStock> skuStocklist = skuStockMapper.selectList(skuQueryWrapper);
        pmsProductParam.setSkuStockList(skuStocklist);
        //3 获取商品阶梯价格
        QueryWrapper<ProductLadder> ladderQueryWrapper = new QueryWrapper();
        ladderQueryWrapper.eq("product_id", id);
        List<ProductLadder> ladderList = productLadderMapper.selectList(ladderQueryWrapper);
        pmsProductParam.setProductLadderList(ladderList);
        //4 获取商品满减价格
        QueryWrapper<ProductFullReduction> fullReductionQueryWrapper = new QueryWrapper();
        fullReductionQueryWrapper.eq("product_id", id);
        List<ProductFullReduction> fullReductionList = productFullReductionMapper.selectList(fullReductionQueryWrapper);
        pmsProductParam.setProductFullReductionList(fullReductionList);
        //5 获取商品会员价格
        QueryWrapper<MemberPrice> memberPriceQueryWrapper = new QueryWrapper();
        memberPriceQueryWrapper.eq("product_id", id);
        List<MemberPrice> memberPriceList = memberPriceMapper.selectList(memberPriceQueryWrapper);
        pmsProductParam.setMemberPriceList(memberPriceList);
        //6 获取商品参数及自定义规格
        QueryWrapper<ProductAttributeValue> productAttributeValueQueryWrapper = new QueryWrapper();
        productAttributeValueQueryWrapper.eq("product_id", id);
        List<ProductAttributeValue> productAttributeValuList = productAttributeValueMapper.selectList(productAttributeValueQueryWrapper);
        pmsProductParam.setProductAttributeValueList(productAttributeValuList);
        return pmsProductParam;
    }

    /**
     * 更新商品信息
     * * 1 保存商品基本信息以及商品库存信息pms_product pms_sku_stock
     * 2 保存商品阶梯价格 pms_product_ladder
     * 3 保存商品满减价格pms_product_full_reduction
     * 4 保存商品会员价格pms_member_price
     * 5 商品参数以及自定义规格
     * 6 商品分类数量
     * 办法:直接删原先的，添加新的
     *
     * @param id
     * @param productParam
     * @return
     */
    @Override
    public boolean updateProductAndOtherInfoById(Long id, PmsProductParam productParam) {
        //1 更新商品基本信息
        Product product = new Product();
        BeanUtils.copyProperties(productParam, product);
        product.setId(id);
        baseMapper.updateById(product);
        //2 更新库存
        QueryWrapper<SkuStock> skuQueryWrapper = new QueryWrapper();
        skuQueryWrapper.eq("product_id", id);
        List<SkuStock> skuStocklist = skuStockMapper.selectList(skuQueryWrapper);
        ArrayList<Long> skuList = new ArrayList<>();
        for (int i = 0; i < skuStocklist.size(); i++) {
            skuList.add(skuStocklist.get(i).getId());
        }
        //删除原先的
        skuStockMapper.deleteBatchIds(skuList);
        //添加新的
        saveSkuStockInfo(skuStocklist);
        //3 更新阶梯价格
        QueryWrapper<ProductLadder> ladderQueryWrapper = new QueryWrapper();
        ladderQueryWrapper.eq("product_id", id);
        List<ProductLadder> ladderLists = productLadderMapper.selectList(ladderQueryWrapper);
        ArrayList<Long> ladderList = new ArrayList<>();
        for (int i = 0; i < ladderLists.size(); i++) {
            ladderList.add(ladderLists.get(i).getId());
        }
        //删除原先的
        productLadderMapper.deleteBatchIds(ladderList);
        //添加新的
        saveLadderPrice(ladderLists);
        //4 更新满减价格
        QueryWrapper<ProductFullReduction> fullReductionQueryWrapper = new QueryWrapper();
        fullReductionQueryWrapper.eq("product_id", id);
        List<ProductFullReduction> fullReductionList = productFullReductionMapper.selectList(fullReductionQueryWrapper);
        ArrayList<Long> fullList = new ArrayList<>();
        for (int i = 0; i < fullReductionList.size(); i++) {
            fullList.add(fullReductionList.get(i).getId());
        }
        //删除原先的
        productFullReductionMapper.deleteBatchIds(fullList);
        //添加新的
        saveFullReduction(fullReductionList);
        //5 商品参数以及自定义规格
        QueryWrapper<ProductAttributeValue> productAttributeValueQueryWrapper = new QueryWrapper();
        productAttributeValueQueryWrapper.eq("product_id", id);
        List<ProductAttributeValue> productAttributeValuList = productAttributeValueMapper.selectList(productAttributeValueQueryWrapper);
        ArrayList<Long> attributeList = new ArrayList<>();
        for (int i = 0; i < productAttributeValuList.size(); i++) {
            attributeList.add(productAttributeValuList.get(i).getId());
        }
        //删除原先的
        productAttributeValueMapper.deleteBatchIds(attributeList);
        //添加新的
        saveProductAttributeValue(productAttributeValuList);

        return false;
    }

    /**
     * 查询商品的销售属性type=0
     *
     * @param productId
     * @return
     */
    @Override
    public List<EsProductAttributeValue> getProductSaleAttr(Long productId) {
        List<EsProductAttributeValue> list = baseMapper.getProductSaleAttr(productId);
        return list;
    }

    /**
     * 查询商品的基本属性type=1
     *
     * @param productId
     * @return
     */
    @Override
    public List<EsProductAttributeValue> getProductBaseAttr(Long productId) {
        List<EsProductAttributeValue> list = baseMapper.getProductBaseAttr(productId);
        return list;
    }

    /**
     * 从缓存中取商品
     * 问题，分布式锁：redis，锁要有时效性和原子性，
     *
     * @param productId
     * @return
     */
    @Override
    public Product selectProductFromCache(Long productId) {
        Product product = null;
        Jedis jedis = jedisPool.getResource();
        int randomSum = new Random().nextInt(20000);
        //缓存中查找
        String s = jedis.get(RedisCacheConstant.PRODUCT_INFO_CACHE_KEY + productId);
        if (StringUtils.isEmpty(s)) {//如果为空，去数据库查找
            UUID token = UUID.randomUUID();//这个是为后面防止删除锁的时候，别的线程删除而设置的
            String lock = jedis.set("lock", token.toString(), SetParams.setParams().ex(5).nx());//如果锁不存在，设置锁的过期时间5秒
            //缓存到数据库,这里要解决两个问题，缓存穿透和缓存雪崩
            if (!StringUtils.isEmpty(lock) && "ok".equalsIgnoreCase(lock)) {//如果锁不为看，且设置成功，说明占上坑了
                try {
                    product = baseMapper.selectById(productId);//从数据库查数据
                    String string = JSON.toJSONString(product);//转成字符串
                    if (string == null) {
                        //防止缓存穿透,一般把时间设置短一点即可
                        jedis.setex(RedisCacheConstant.PRODUCT_INFO_CACHE_KEY + productId, 60 + randomSum, string);
                    } else {
                        //防止缓存雪崩，设置一个随机时间
                        jedis.setex(RedisCacheConstant.PRODUCT_INFO_CACHE_KEY + productId, 60 * 60 * 24 * 3 + randomSum, string);
                    }
                } finally {//不管怎样，都要释放锁，锁的释放必须保证原子性，用脚本实现
                    String script =
                            "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                    jedis.eval(script, Collections.singletonList("lock"), Collections.singletonList(token.toString()));
                }
            } else {//没有获取到锁，等，继续调本身，自旋锁
                try {
                    Thread.sleep(1000);
                    selectProductFromCache(productId);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        else{//缓存中有数据
                product = JSON.parseObject(s, Product.class);
            }
            return product;
        }


//--------------------------------一些调用方法----------------------------------------

        //第一步，保存商品基本信息
        @Transactional(propagation = Propagation.REQUIRED)
        public Long saveProductInfo (PmsProductParam productParam){
            Product product = new Product();
            BeanUtils.copyProperties(productParam, product);
            int insert = productMapper.insert(product);
            //商品信息共享到ThreadLocal
            productThreadLocal.set(product);
            return product.getId();
        }

        //第一步，保存商品库存信息
        @Transactional(propagation = Propagation.REQUIRED)
        public void saveSkuStockInfo (List < SkuStock > skuStocks) {
            Product product = productThreadLocal.get();
            AtomicReference<Integer> i = new AtomicReference<>(0);
            NumberFormat numberFormat = DecimalFormat.getNumberInstance();
            numberFormat.setMinimumIntegerDigits(2);
            numberFormat.setMaximumIntegerDigits(2);
            skuStocks.forEach(skuStock -> {
                skuStock.setProductId(product.getId());
                //SKU编码 k_商品id_自增
                //skuStock.setSkuCode();  两位数字，不够补0
                String format = numberFormat.format(i.get());
                String code = "K_" + product.getId() + "_" + format;
                skuStock.setSkuCode(code);
                //自增
                i.set(i.get() + 1);
                skuStockMapper.insert(skuStock);
            });
        }

        //将第一步的两个方法放到一个事务中
        @Transactional(propagation = Propagation.REQUIRES_NEW)//两个方法同成功或失败
        public void saveBaseInfo (PmsProductParam productParam){
            ProductServiceImpl psProxy = (ProductServiceImpl) AopContext.currentProxy();
            //Required
            psProxy.saveProductInfo(productParam);
            //Required
            psProxy.saveSkuStockInfo(productParam.getSkuStockList());
        }

        //第二步，保存商品阶梯价格
        @Transactional(propagation = Propagation.REQUIRES_NEW)
        public void saveLadderPrice (List < ProductLadder > list) {
            Product product = productThreadLocal.get();
            //Product product1 = map.get(Thread.currentThread());
            //2、保存商品的阶梯价格 到 pms_product_ladder【REQUIRES_NEW】
            for (ProductLadder ladder : list) {
                ladder.setProductId(product.getId());
                productLadderMapper.insert(ladder);
            }
        }

        //第三步，保存会员价格
        @Transactional(propagation = Propagation.REQUIRES_NEW)
        public void saveMemberPrice (List < MemberPrice > memberPrices) {
            Product product = productThreadLocal.get();
            for (MemberPrice memberPrice : memberPrices) {
                memberPrice.setProductId(product.getId());
                memberPriceMapper.insert(memberPrice);
            }
        }

        //第四步，保存商品满减价格
        @Transactional(propagation = Propagation.REQUIRES_NEW)
        public void saveFullReduction (List < ProductFullReduction > list) {
            Product product = productThreadLocal.get();
            for (ProductFullReduction reduction : list) {
                reduction.setProductId(product.getId());
                productFullReductionMapper.insert(reduction);
            }
        }

        //第五步，保存参数及自定义规格
        @Transactional(propagation = Propagation.REQUIRES_NEW)
        public void saveProductAttributeValue (List < ProductAttributeValue > productAttributeValues) {
            Product product = productThreadLocal.get();
            productAttributeValues.forEach((pav) -> {
                pav.setProductId(product.getId());
                productAttributeValueMapper.insert(pav);
            });
        }

        //第六步，更新商品分类数量
        @Transactional(propagation = Propagation.REQUIRES_NEW)
        public void updateProductCategoryCount () {
            Product product = productThreadLocal.get();
            Long id = product.getProductCategoryId();
            productCategoryMapper.updateCountById(id);

        }


    }