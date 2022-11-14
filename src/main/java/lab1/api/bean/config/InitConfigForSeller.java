package lab1.api.bean.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lab1.api.bean.basic.Product;
import lab1.api.bean.config.basic.InitConfigBasic;
import lab1.api.util.ProductDeSerializer;
import lab1.api.util.ProductSerializer;

import java.util.Map;

/**
 * @project: CS677_Lab1
 * @description:
 * @author: zhexu
 * @create: 10/28/22
 **/
public class InitConfigForSeller extends InitConfigBasic {

    @JsonProperty("map")
    @JsonDeserialize(keyUsing = ProductDeSerializer.class)
    private Map<Product, Integer> stock;
    private Integer maxStock;

    public Map<Product, Integer> getStock() {
        return stock;
    }

    public void setStock(Map<Product, Integer> stock) {
        this.stock = stock;
    }

    public Integer getMaxStock() {
        return maxStock;
    }

    public void setMaxStock(Integer maxStock) {
        this.maxStock = maxStock;
    }
}
