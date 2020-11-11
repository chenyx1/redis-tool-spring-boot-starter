package com.chenyx.redis.tool.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author chenyx
 * @desc 缓存基础类
 * @date 2020-06-28
 */

public abstract class BaseCache<K, V> {

    protected static final Logger LOGGER = LoggerFactory.getLogger(BaseCache.class);

    @Resource
    protected RedisTemplate<K, V> redisTemplate;



    /**
     * @desc 获取缓存
     * @author chenyx
     * @date 2020-06-28
     */
    public V getCacahe(K key) {
        V v = null;
        try {
            //解析key
            K realKey = parseKey(key);
            v = this.get(realKey);
            if (v != null) {
                return v;
            }
            synchronized (this) {
                v = this.get(realKey);
                if (v != null) {
                    return v;
                }
                //设置失效时间
                Long time = expireTime(key);
                //从数据中查询，并并加载到缓存
                v = loadCache(key,time);
                return v;
            }
        }catch (Exception e) {
            LOGGER.error("获取缓存异常:{}", e.getMessage());
        }
       return v;
    }

    /**
     * @desc 获取缓存集合
     * @author chenyx
     * @date 2020-06-28
     */
    public List<V> getListCache(K key) {
        List<V> list = new ArrayList<>();
        try {
            //解析key
            K realKey = parseKey(key);
            list = this.lGet(realKey,0,-1);
            if (!CollectionUtils.isEmpty(list)) {
                return list;
            }
            //反查数据库加载
            synchronized (this) {
                list = this.lGet(realKey,0,-1);
                if (!CollectionUtils.isEmpty(list)) {
                    return list;
                }
                //设置失效时间
                Long time = expireTime(key);
                //从数据中查询，并并加载到缓存
                list = loadListCache(key,time);
                return list;
            }
        }catch (Exception e) {
            LOGGER.error("获取缓存异常:{}", e.getMessage());
        }
        return list;
    }


    /**
     * @desc 导入缓存
     * @author chenyx
     * @date 2020-06-28
     */
    private V loadCache(K key,Long time) {
        V v= null;
        try {
            v = loadCacheFromDB(key);
            if (v != null) {
                K realKey = parseKey(key);
                if (time == null) {
                    this.set(realKey,v);
                } else {
                    this.set(realKey,v,time);
                }
            }
        }catch (Exception e) {
            LOGGER.error("导入缓存异常:{}", e.getMessage());
        }
        return  v;
    }


    /**
     * @desc 导入缓存集合
     * @author chenyx
     * @date 2020-06-28
     */
    private List<V> loadListCache(K key,Long time) {
        List<V> list = new ArrayList<>();
        try {
            list = loadListCacheFromDB(key);
            if (!CollectionUtils.isEmpty(list)) {
                K realKey = parseKey(key);
                if (time == null) {
                    this.lSet(realKey,list);
                } else {
                    this.lSet(realKey,list,time);
                }
            }
        }catch (Exception e) {
            LOGGER.error("导入缓存异常:{}", e.getMessage());
        }
        return  list;
    }

    /**
     * @desc 从数据查询实例
     * @author chenyx
     * @date 2020-06-28
     * */
    protected  V loadCacheFromDB(K key) {
        return null;
    }

    /**
     * @desc 从数据查询实例集合
     * @author chenyx
     * @date 2020-06-28
     * */
    protected  List<V> loadListCacheFromDB(K key) {
        return null;
    }


    /**
     * @desc 设置失效时间
     * @author chenyx
     * @date 2020-06-28
     * */
    protected  Long expireTime(K key) {
        Long expire = 60 * 60 * 24 * 7L;
        return  expire;
    }

    /**
     * @desc 解析逻辑key，获取redis真正key
     * @author chenyx
     * @date 2020-06-28
     * */
    protected K parseKey(K key) {
        return  key;
    }

    /**
     * @desc 获取缓存
     * @author chenyx
     * @date 2020-06-28
     */
    public Boolean removeCacahe(K key) {
        boolean result = true;
       if (this.hasKey(key)) {
           result = this.del(key);
       }
       return result;
    }

    /**
     * @desc 指定缓存失效时间
     * @author chenyx
     * @date 2020-06-28
     */
    public boolean expire(K key, long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            LOGGER.error("指定缓存失效时间异常:{}", e.getMessage());
            return false;
        }
    }

    /**
     * @return 时间(秒) 返回0代表为永久有效
     * @desc 根据key 获取过期时间
     * @author chenyx
     * @date 2020-06-28
     */
    public long getExpire(K key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * @desc 判断key是否存在
     * @author chenyx
     * @date 2020-06-28
     */
    public boolean hasKey(K key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            LOGGER.error("判断key是否存在异常:{}", e.getMessage());
            return false;
        }
    }

    /**
     * @desc 删除缓存
     * @author chenyx
     * @date 2020-06-28
     */
    public boolean del(K... key) {
        boolean result = false;
        try {
            if (key != null && key.length > 0) {
                if (key.length == 1) {
                    result = redisTemplate.delete(key[0]);
                } else {
                    redisTemplate.delete(CollectionUtils.arrayToList(key));
                    result = true;
                }
            }
        } catch (Exception e) {
            LOGGER.error("删除缓存异常:{}", e.getMessage());
            return false;
        }
        return result;
    }


    /**
     * @desc 普通缓存获取
     * @author chenyx
     * @date 2020-06-28
     */
    public V get(K key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * @desc 普通缓存放入
     * @author chenyx
     * @date 2020-06-28
     */
    public boolean set(K key, V value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            LOGGER.error("普通缓存放入异常:{}", e.getMessage());
            return false;
        }
    }

    /**
     * @desc 普通缓存放入并设置时间
     * @author chenyx
     * @date 2020-06-28
     */
    public boolean set(K key, V value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            LOGGER.error("通缓存放入并设置时间异常:{}", e.getMessage());
            return false;
        }
    }

    /**
     * @desc 递增
     * @author chenyx
     * @date 2020-06-28
     */
    public long incr(K key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * @desc 递减
     * @author chenyx
     * @date 2020-06-28
     */
    public long decr(K key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        return redisTemplate.opsForValue().decrement(key, delta);
    }

    /**
     * 将数据放入set缓存
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSet(K key, V... values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            LOGGER.error("将数据放入set缓存异常:{}", e.getMessage());
            return 0;
        }
    }

    /**
     * 将set数据放入缓存
     *
     * @param key    键
     * @param time   时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSetAndTime(K key, long time, V... values) {
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            if (time > 0)
                expire(key, time);
            return count;
        } catch (Exception e) {
            LOGGER.error("将set数据放入缓存并设置失效时间异常:{}", e.getMessage());
            return 0;
        }
    }

    /**
     * 获取set缓存的长度
     *
     * @param key 键
     * @return
     */
    public long sGetSetSize(K key) {
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            LOGGER.error("获取set缓存的长度异常:{}", e.getMessage());
            return 0;
        }
    }

    /**
     * 移除值为value的
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    public long setRemove(K key, V... values) {
        try {
            long count = redisTemplate.opsForSet().remove(key, values);
            return count;
        } catch (Exception e) {
            LOGGER.error("移除值为value异常:{}", e.getMessage());
        }
        return 0;
    }
    // ===============================list=================================

    /**
     * 获取list缓存的内容
     *
     * @param key   键
     * @param start 开始
     * @param end   结束 0 到 -1代表所有值
     * @return
     */
    public List<V> lGet(K key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            LOGGER.error("获取list缓存的内容异常:{}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取list缓存的长度
     *
     * @param key 键
     * @return
     */
    public long lGetListSize(K key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            LOGGER.error("获取list缓存的长度异常:{}", e.getMessage());
            return 0;
        }
    }

    /**
     * 通过索引 获取list中的值
     *
     * @param key   键
     * @param index 索引 index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return
     */
    public V lGetIndex(K key, long index) {
        try {
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            LOGGER.error("过索引 获取list中的值异常:{}", e.getMessage());
            return null;
        }
    }

    /**
     * 放入list缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public boolean lSet(K key, V value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            LOGGER.error("放入list缓存异常:{}", e.getMessage());
            return false;
        }
    }

    /**
     * 放入list缓存并设置失效时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    public boolean lSet(K key, V value, long time) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            if (time > 0)
                expire(key, time);
            return true;
        } catch (Exception e) {
            LOGGER.error("放入list缓存并设置失效时间异常:{}", e.getMessage());
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public boolean lSet(K key, List<V> value) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            return true;
        } catch (Exception e) {
            LOGGER.error("将list放入缓存异常:{}", e.getMessage());
            return false;
        }
    }

    /**
     * 将list放入缓存并设置失效时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    public boolean lSet(K key, List<V> value, long time) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            if (time > 0)
                expire(key, time);
            return true;
        } catch (Exception e) {
            LOGGER.error("将list放入缓存并设置失效时间异常:{}", e.getMessage());
            return false;
        }
    }


    /**
     * 移除N个值为value
     *
     * @param key   键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    public long lRemove(K key, long count, Object value) {
        try {
            long remove = redisTemplate.opsForList().remove(key, count, value);
            return remove;
        } catch (Exception e) {
            LOGGER.error("移除N个值为value异常:{}", e.getMessage());
        }
        return 0;
    }


}
