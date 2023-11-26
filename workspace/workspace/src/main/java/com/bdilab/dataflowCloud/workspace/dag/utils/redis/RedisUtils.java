package com.bdilab.dataflowCloud.workspace.dag.utils.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis API Utils.
 *
 * @author wh
 * @version 1.0
 * @date 2021/10/12
 */
@Component
public class RedisUtils {
  private RedisTemplate<String, Object> redisTemplate;

  public RedisTemplate<String, Object> getRedisTemplate() {
    return redisTemplate;
  }

  @Resource(name = "redisTemplateNoTransaction")
  public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }




  public void del(String... key) {
    if (key != null && key.length > 0) {
      if (key.length == 1) {
        redisTemplate.delete(key[0]);
      } else {
        redisTemplate.delete(CollectionUtils.arrayToList(key));
      }
    }
  }
  // ================================Map=================================

  /**
   * HashGet.
   *
   * @param key  key
   * @param item item Cannot be null
   */
  public Object hget(String key, String item) {
    return redisTemplate.opsForHash().get(key, item);
  }


  /**
   * Gets all keys corresponding to the hashKey.

   * @param key key
   * @return Corresponding to multiple key values.
   */
  public Map<Object, Object> hmget(String key) {
    return redisTemplate.opsForHash().entries(key);
  }

  /**
   * Puts data into a hash table, or creates it if it doesn't exist.

   * @param key   key
   * @param item  item
   * @param value value
   * @return True is success, false is failure
   */
  public boolean hset(String key, String item, Object value) {
    redisTemplate.opsForHash().put(key, item, value);
    return true;
  }
  /**
   * HashSet.

   * @param key key
   * @param map Corresponding to multiple key values
   * @return True is success, false is failure
   */
  public boolean hmset(String key, Map<?, ?> map) {
    redisTemplate.opsForHash().putAll(key, map);
    return true;
  }



  /**
   * Delete a value from the hash table.
   *
   * @param key  key, Cannot be null
   * @param item item, Can enable multiple, cannot be null.
   */
  public void hdel(String key, Object... item) {
    redisTemplate.opsForHash().delete(key, item);
  }

  /**
   * Determines whether the value of the item exists in the hash table.
   *
   * @param key  key, Cannot be null
   * @param item item, Cannot be null
   * @return True is present, false is absent.
   */
  public boolean hashHasKey(String key, String item) {
    return redisTemplate.opsForHash().hasKey(key, item);
  }

  /**
   * If the hash increment does not exist, it creates one and returns the new value.

   * @param key  key
   * @param item item
   * @param by   How many to add (greater than 0).
   */
  public double hincr(String key, String item, double by) {
    return redisTemplate.opsForHash().increment(key, item, by);
  }

  /**
   * A hash of diminishing.
   *
   * @param key  key
   * @param item item
   * @param by   How much to subtract (less than 0).
   */
  public double hdecr(String key, String item, double by) {
    return redisTemplate.opsForHash().increment(key, item, -by);
  }

}
