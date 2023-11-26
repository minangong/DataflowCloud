package com.bdilab.dataflowCloud.workspace.workspace.utils;

import com.baomidou.mybatisplus.core.incrementer.DefaultIdentifierGenerator;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
/*
  主键UUID生成器

  @author liran
  @since 2021-07-09
 */
public class RandomIdGenerator extends DefaultIdentifierGenerator {
    public static String uuid() {
        return UUID.randomUUID().toString();
    }

    @Override
    public String nextUUID(Object entity) {
        return UUID.randomUUID().toString();
    }
}
