package lab1.api.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import lab1.api.bean.basic.Product;

import java.io.IOException;
import java.io.StringWriter;

/**
 * @project: CS677_Lab1
 * @description:
 * @author: zhexu
 * @create: 10/29/22
 **/
public class ProductSerializer extends JsonSerializer<Product> {

    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public void serialize(Product product, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        StringWriter writer = new StringWriter();
        mapper.writeValue(writer, product);
        jsonGenerator.writeFieldName(writer.toString());
    }
}
