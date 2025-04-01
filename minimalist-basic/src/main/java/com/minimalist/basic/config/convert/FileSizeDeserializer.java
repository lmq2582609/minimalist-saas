package com.minimalist.basic.config.convert;

import cn.hutool.core.io.unit.DataSizeUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;

/**
 * 接收数据时，对String类型的文件大小进行处理
 */
public class FileSizeDeserializer extends JsonDeserializer<Long> {

    @Override
    public Long deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        String text = jsonParser.getText();
        return StrUtil.isBlank(text) ? null : DataSizeUtil.parse(text);
    }

}
