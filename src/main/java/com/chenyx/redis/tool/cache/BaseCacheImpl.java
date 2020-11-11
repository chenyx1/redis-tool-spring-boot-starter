package com.chenyx.redis.tool.cache;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;


/**
 * @desc 门店营业时间配置缓存
 * @author chenyx
 * @date 2020-06-28
 *
 * */
public class BaseCacheImpl extends BaseCache<String,Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseCacheImpl.class);

    @Override
    public Boolean removeCacahe(String key) {
        try {
            Set<String> keys = keys(key);
            LOGGER.info("BaseCacheImpl--->keys:{}", JSON.toJSONString(keys));
            LOGGER.info("BaseCacheImpl--->keys.size:{}",keys.size());
            Long delKey = redisTemplate.delete(keys);
            LOGGER.info("BaseCacheImpl--->delKey.size:{}",delKey);
        }catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     *  获取指定前缀的一系列key
     *  使用scan命令代替keys, Redis是单线程处理，keys命令在KEY数量较多时，
     *  操作效率极低【时间复杂度为O(N)】，该命令一旦执行会严重阻塞线上其它命令的正常请求
     * @param keyPrefix
     * @return
     */
    public Set<String> keys(String keyPrefix)throws Exception{
        String realKey = "*" + keyPrefix + "*";
        try {
            return redisTemplate.execute((RedisCallback<Set<String>>) connection -> {
                Set<String> binaryKeys = new HashSet<>();
                Cursor<byte[]> cursor = connection.scan(new ScanOptions.ScanOptionsBuilder().match(realKey).count(Integer.MAX_VALUE).build());
                while (cursor.hasNext()) {
                    binaryKeys.add(new String(cursor.next()));
                }
                return binaryKeys;
            });
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }
}
