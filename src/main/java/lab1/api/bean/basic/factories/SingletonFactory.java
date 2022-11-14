package lab1.api.bean.basic.factories;

import lab1.api.bean.Buyer;
import lab1.api.bean.Seller;
import lab1.api.bean.Transaction;
import lab1.api.bean.basic.Product;
import lab1.api.bean.config.InitConfigForBuyer;
import lab1.api.bean.config.InitConfigForSeller;
import lab1.api.util.LoggerFormator;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.FileHandler;

import static lab1.constants.Const.*;

/**
 * @project: CS677_Lab1
 * @description:
 * @author: zhexu
 * @create: 10/27/22
 **/
public class SingletonFactory {
    private volatile static Buyer buyer;
    private volatile static Seller seller;
    private volatile static String role;
    private volatile static Map<String, FileHandler> fileHandlerMap;
    private volatile static List<Product> productList;
    private static Integer maxStock;
    private static InitConfigForBuyer initConfigForBuyer;
    private static InitConfigForSeller initConfigForSeller;


    private SingletonFactory() {
    }

    public static void setInitConfigForBuyer(InitConfigForBuyer config) {
        initConfigForBuyer = config;
    }

    public static void setInitConfigForSeller(InitConfigForSeller configForSeller) {
        initConfigForSeller = configForSeller;
    }

    public static void setProductList(List<Product> products){
        productList = new ArrayList<>(products);
    }

    public static void setMaxStock(Integer number){
        maxStock = number;
    }


    public static void setRole(String r) {
        role = r;
    }

    public static String getRole() {
        return role;
    }

    public static Integer getMaxStock(){
        return maxStock;
    }


    public static List<Product> getProductList(){
        return productList;
    }

    public static String getSelfDomain(){
        if (ROLE_SELLER.equals(getRole())){
            return getSeller().getSelfAddress().getDomain();
        } else {
            return getBuyer().getSelfAddress().getDomain();
        }
    }

    public static Buyer getBuyer() {
        if (!ROLE_BUYER.equals(getRole())) {
            return null;
        }
        if (null == buyer) {
            synchronized (Buyer.class) {
                if (null == buyer) {
                    buyer = new Buyer(UUID.randomUUID(),
                            initConfigForBuyer.getNeighbours(),
                            new HashMap<Integer, Transaction>(),
                            initConfigForBuyer.getSelfAdd(),
                            initConfigForBuyer.getProducts());
                }
            }
        }
        return buyer;
    }

    public static Seller getSeller() {
        if (!ROLE_SELLER.equals(getRole())) {
            return null;
        }
        if (null == seller) {
            synchronized (Seller.class) {
                if (null == seller) {
                    seller = new Seller(UUID.randomUUID(),
                            initConfigForSeller.getNeighbours(),
                            new HashMap<Integer, Transaction>(),
                            initConfigForSeller.getSelfAdd(),
                            initConfigForSeller.getStock());
                }
            }
        }
        return seller;
    }

    public static Map<String, FileHandler> getFileHandlerMap(String baseDir) {
        if (null == fileHandlerMap) {
            fileHandlerMap = new HashMap();
            try {
                FileHandler FILE_MAIN_LOG = new FileHandler(baseDir + SLASH + LOGGER_MAIN + LOG_FILE_SUFIX, true);
                FILE_MAIN_LOG.setFormatter(new LoggerFormator());

                FileHandler FILE_LOOKUP_SERVICE_LOG = new FileHandler(baseDir + SLASH + LOGGER_LOOKUP_SERVICE + LOG_FILE_SUFIX, true);
                FILE_LOOKUP_SERVICE_LOG.setFormatter(new LoggerFormator());

                FileHandler FILE_REPLY_SERVICE_LOG = new FileHandler(baseDir + SLASH + LOGGER_REPLY_SERVICE + LOG_FILE_SUFIX, true);
                FILE_REPLY_SERVICE_LOG.setFormatter(new LoggerFormator());

                FileHandler FILE_TRADE_LOG = new FileHandler(baseDir + SLASH + LOGGER_TRADE + LOG_FILE_SUFIX, true);
                FILE_TRADE_LOG.setFormatter(new LoggerFormator());

                FileHandler FILE_MEASURE_RESULT = new FileHandler(baseDir + SLASH + LOGGER_MESURE_RESULT + LOG_FILE_SUFIX, true);

                fileHandlerMap.put(LOGGER_MAIN, FILE_MAIN_LOG);
                fileHandlerMap.put(LOGGER_LOOKUP_SERVICE, FILE_LOOKUP_SERVICE_LOG);
                fileHandlerMap.put(LOGGER_REPLY_SERVICE, FILE_REPLY_SERVICE_LOG);
                fileHandlerMap.put(LOGGER_TRADE,FILE_TRADE_LOG);
                fileHandlerMap.put(LOGGER_MESURE_RESULT,FILE_MEASURE_RESULT);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return fileHandlerMap;
    }
}
