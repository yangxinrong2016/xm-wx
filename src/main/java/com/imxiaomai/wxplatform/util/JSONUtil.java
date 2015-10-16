package com.imxiaomai.wxplatform.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * Created by zengyaowen on 15-10-16.
 */
public class JSONUtil {
    public static String toJson(Object o) {
        return JSON.toJSONString(o, SerializerFeature.BrowserCompatible, SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteNullListAsEmpty, SerializerFeature.WriteNullStringAsEmpty,
                SerializerFeature.WriteDateUseDateFormat);
    }
}
