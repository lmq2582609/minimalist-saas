package com.minimalist.common.convert;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;

/**
 * 返回数据时，对Long类型的文件大小进行处理
 */
public class FileSizeSerializer extends JsonSerializer<Long> {

    @Override
    public void serialize(Long value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (ObjectUtil.isNotNull(value)) {
            jsonGenerator.writeString(FileUtil.readableFileSize(value));
        } else {
            jsonGenerator.writeNull();
        }
    }

}
