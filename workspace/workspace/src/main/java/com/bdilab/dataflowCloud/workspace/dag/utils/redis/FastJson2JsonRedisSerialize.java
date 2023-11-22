package com.bdilab.dataflowCloud.workspace.dag.utils.redis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * FastJson2JsonRedisSerialize for redis serialize.
 *
 * @author wh
 * @version 1.0
 * @date 2021/10/12
 */
@Slf4j
public class FastJson2JsonRedisSerialize<T> implements RedisSerializer<T> {

  public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
  private final SerializerFeature[] serializerFeatures = new SerializerFeature[] {};
  private static final String AutoType = "@type";
  private final Class<T> clazz;

  public FastJson2JsonRedisSerialize(Class<T> clazz) {
    super();
    ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
    this.clazz = clazz;
  }


  /**
   * Serialization.
   */
  @Override
  public byte[] serialize(T t) throws SerializationException {
    if (null == t) {
      return new byte[0];
    }
    return JSON.toJSONString(t, serializerFeatures).getBytes(DEFAULT_CHARSET);
  }

  /**
   * des serialization.
   */
  @Override
  public T deserialize(byte[] bytes) throws SerializationException {
    if (null == bytes || bytes.length <= 0) {
      return null;
    }
    String str = new String(bytes, DEFAULT_CHARSET);
    try {
//      log.debug("Object string: {}", str);
      return (T) JSON.parseObject(str, clazz);
    }
    catch (JSONException jsonException) {
      log.error("Failed to deserialize for JSON String: {}", str);
      throw new SerializationException("Failed to deserialize for JSON String: " + str, jsonException);
    }
  }

}