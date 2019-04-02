package com.atguigu.gmall.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.cart.bean.CartItem;
import com.atguigu.gmall.cart.bean.SkuResponse;
import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.pms.entity.Product;
import com.atguigu.gmall.pms.entity.SkuStock;
import com.atguigu.gmall.pms.service.ProductService;
import com.atguigu.gmall.pms.service.SkuStockService;
import com.atguigu.gmall.sms.service.CouponService;
import com.atguigu.gmall.ums.entity.Member;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
@Component
public class CartServiceImpl implements CartService {

    @Reference
    SkuStockService skuStockService;
    @Reference
    ProductService productService;
    @Reference
    CouponService couponService;
    @Autowired
    RedissonClient redissonClient;

    /**
     * 添加到购物车
     *
     * @param token
     * @param cartKey
     * @param skuId
     * @param num
     * @return
     */
    @Override
    public SkuResponse addToCart(String token, String cartKey, String skuId, Integer num) {
        SkuResponse skuResponse = new SkuResponse();
        //0首先先组装基本数据CartItem
        CartItem cartItem;
        //0.1查询sku信息
        SkuStock skuStock = skuStockService.getById(skuId);
        //0.2查询出spu信息
        Product product = productService.getById(skuStock.getProductId());
        Member member = new Member();
        //注意这里member信息应该根据token查询出来member信息，这里默认没有
        if ("root".equals(token)) {
            member.setId(5L);
            member.setNickname("lisi");
        } else {
            member.setId(0L);
            member.setNickname("");
        }
        //0.3查询优惠卷信息
        //0.4封装成数据
        //4、封装成一个cartItem；
        cartItem = new CartItem(product.getId(),
                skuStock.getId(),
                member.getId(),
                num,
                skuStock.getPrice(),
                skuStock.getPrice(),
                num,
                skuStock.getSp1(), skuStock.getSp2(), skuStock.getSp3(),
                product.getPic(),
                product.getName(),
                member.getNickname(),
                product.getProductCategoryId(),
                product.getBrandName(),
                false,
                "满199减90"
        );

        //1判断是否登录,从redsi中查询token，如果有就是登录了，如果没有就是没登录
        //这列只是演示，token为root就是登录了
        if ("root".equals(token)) {//2登录了
            String redisKey = "gmall:cart:user"+member.getId();
            //2.1合并旧购物车
            mergeOldCart(redisKey,"gmall:cart:temp" + cartKey);
            //2.2添加登录加入购物车的数据
            addItemToCart(cartItem,num,redisKey);
        } else {//3没登录
            //3.1查看用户是否有cartId
            if (StringUtils.isEmpty(cartKey)) {//3.1.1如果用户的cartId为空，要先给用户分配一个Id，后面还有还给前端
                cartKey = UUID.randomUUID().toString().replace("-", "");
                String newCartKey = "gmall:cart:temp" + cartKey;
                skuResponse.setCartKey(newCartKey);
                addItemToCart(cartItem, num, newCartKey);//添加到购物车
            } else {//3.3.2用户已经有cartKey了
                skuResponse.setCartKey(cartKey);
                cartKey = "gmall:cart:temp"+cartKey;
                addItemToCart(cartItem,num,cartKey);
            }
        }
        skuResponse.setItem(cartItem);
        return skuResponse;
    }

    /**
     * 合并旧购物车数据
     * @param redisKey
     * @param s
     */
    private void mergeOldCart(String newKey, String oldKey) {
        RMap<String, String> oldMap = redissonClient.getMap(oldKey);
        if(oldMap!=null&&oldMap.entrySet()!=null){
            oldMap.entrySet().forEach((entry)->{
                String value = entry.getValue();
                CartItem cartItem = JSON.parseObject(value, CartItem.class);
                addItemToCart(cartItem,cartItem.getNum(),newKey);//添加到用户的购物车中
                oldMap.remove(entry.getKey());//移除离线购物车的数据
            });
        }
    }

    /**
     * 添加到reids数据库
     *
     * @param cartItem
     * @param num
     * @param newCartKey
     */
    private void addItemToCart(CartItem cartItem, Integer num, String newCartKey) {
        //先查看这个key的hash
        RMap<String, String> map = redissonClient.getMap(newCartKey);
        //查看是否已经包含这个商品
        boolean b = map.containsKey(cartItem.getProductSkuId() + "");
        if (b) {//如果已经包含这个商品,商品数量加一
            String o = map.get(cartItem.getProductSkuId() + "");//拿到数据
            CartItem item = JSON.parseObject(o, cartItem.getClass());
            item.setNum(item.getNum() + num);//增加数量
            String string = JSON.toJSONString(item);
            map.put(cartItem.getProductSkuId() + "", string);//重新放进去
        } else {//是全新的数据，直接放进去即可
            String string = JSON.toJSONString(cartItem);
            map.put(cartItem.getProductSkuId() + "", string);//直接放进去
        }
    }
}
