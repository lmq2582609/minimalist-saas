package com.minimalist.basic.config.convert;

import cn.hutool.core.collection.CollectionUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.util.Collection;

/**
 * 返回数据时，对Long类型的集合转换成String后再返回，防止Long类型精度丢失
 */
public class LongArrJsonSerializer extends JsonSerializer<Collection<Long>> {

    @Override
    public void serialize(Collection<Long> values, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (CollectionUtil.isNotEmpty(values)) {
            String [] strArr = values.stream().map(l -> Long.toString(l)).toArray(String[]::new);
            jsonGenerator.writeArray(strArr, 0, strArr.length);
        } else {
            jsonGenerator.writeNull();
        }
    }

}
