package lab1.api.bean;

import lab1.api.bean.basic.Address;
import lab1.api.bean.basic.PeerBase;
import lab1.api.bean.basic.Product;
import lab1.api.bean.basic.factories.SingletonFactory;

import java.io.Serializable;
import java.util.*;
import java.util.logging.Logger;

import static lab1.constants.Const.ENTER;
import static lab1.constants.Const.LOGGER_TRADE;

/**
 * @project: CS677_Lab1
 * @description:
 * @author: zhexu
 * @create: 10/25/22
 **/
public class Seller extends PeerBase implements Serializable {
    private Map<Product, Integer> stock;

    public Seller(UUID id, List<Address> neighbourPeerList, Map<Integer, Transaction> transactionProccessRecordMap, Address selfAddress, Map<Product, Integer> stock) {
        super(id, neighbourPeerList, transactionProccessRecordMap, selfAddress);
        this.stock = stock;
    }

    public Map<Product, Integer> getStock() {
        return stock;
    }

    public void reImportWhenSold(){
        Random ra = new Random();
        List<Product> productList = SingletonFactory.getProductList();
        Integer maxStock = SingletonFactory.getMaxStock();
        Map<Product, Integer> newStock = new HashMap<>();
        newStock.put(
          productList.get(ra.nextInt(productList.size())), ra.nextInt(maxStock)
        );
        Logger logger = Logger.getLogger(LOGGER_TRADE);
        logger.info("Stock updated:" + ENTER +
                this.getStock() + ENTER);
    }

    public void setStock(Map<Product, Integer> stock) {
        this.stock = stock;
    }

    public Integer quaryStock(Product product) {
        return this.stock.get(product);
    }

    public Integer addStock(Product product, Integer number) {
        this.stock.put(product, this.stock.get(product) + number);
        return this.stock.get(product);
    }

    public Integer cosumeStock(Product product, Integer number) {
        if (!this.stock.containsKey(product)) {
            return -1;
        }
        if (this.stock.get(product) >= number) {
            this.stock.put(product, this.stock.get(product) - number);
            if (0 >= this.stock.get(product)){
                reImportWhenSold();
                return 0;
            }
            return this.stock.get(product);
        }
        return -2;
    }

    public Boolean ifProductInStore(Product product) {
        return this.stock.containsKey(product);
    }

    @Override
    public String toString() {
        return super.toString() + "Seller{" +
                "stock=" + stock +
                '}';
    }
}
