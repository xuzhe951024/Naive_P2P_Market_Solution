package lab1;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lab1.api.bean.Buyer;
import lab1.api.bean.Seller;
import lab1.api.bean.basic.factories.SingletonFactory;
import lab1.api.bean.config.InitConfigForBuyer;
import lab1.api.bean.config.InitConfigForSeller;
import lab1.api.util.LuckyPrinter;
import lab1.rpcServer.RpcServer;


import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import static lab1.constants.Const.*;


/**
 * @project: CS677_Lab1
 * @description:
 * @author: zhexu
 * @create: 10/27/22
 **/
public class App {
    public static void main(String[] args) throws IOException, InterruptedException {
        LuckyPrinter.print();
        ObjectMapper objectMapper = new ObjectMapper();
        SingletonFactory.setRole(args[0]);
        JsonNode jsonNode = objectMapper.readTree(new File(args[1]));
        Integer lookupStartDelay = Integer.valueOf(args[2]) * 1000;


        Logger loggerMain = Logger.getLogger(LOGGER_MAIN);

        if (ROLE_BUYER.equals(SingletonFactory.getRole())) {
            InitConfigForBuyer initConfigForBuyer = objectMapper.readValue(jsonNode.toString(), InitConfigForBuyer.class);
            SingletonFactory.setInitConfigForBuyer(initConfigForBuyer);
            Buyer buyer = SingletonFactory.getBuyer();
            SingletonFactory.setProductList(initConfigForBuyer.getProducts());
            initLoggers(LOG_FILE_DIR_PREFIX + buyer.getSelfAddress().getDomain());
            loggerMain.info("Initiating from file: " + buyer.toString());

            Integer maxJump = initConfigForBuyer.getMaxJump();
            Thread rpcServer = new Thread(() -> RpcServer.openServer(buyer.getSelfAddress().getPort()));
            rpcServer.start();
            Thread.sleep(lookupStartDelay);
            buyer.startLookUp(ONE, maxJump, lookupStartDelay, Integer.valueOf( args[2]));
        } else {
            InitConfigForSeller initConfigForSeller = objectMapper.readValue(jsonNode.toString(), InitConfigForSeller.class);
            SingletonFactory.setInitConfigForSeller(initConfigForSeller);
            Seller seller = SingletonFactory.getSeller();
            SingletonFactory.setProductList(initConfigForSeller.getProducts());
            SingletonFactory.setMaxStock(initConfigForSeller.getMaxStock());
            initLoggers("logs_" + seller.getSelfAddress().getDomain());
            loggerMain.info("Initiating from file: " + seller.toString());

            RpcServer.openServer(seller.getSelfAddress().getPort());
        }
    }

    private static void initLoggers(String logDir) {
        logDir = SingletonFactory.getSelfDomain() + SLASH + LOG_DIR_BASE +logDir;
        File dir = new File(logDir);
        dir.mkdirs();

        Map<String, FileHandler> fileHandlerMap = SingletonFactory.getFileHandlerMap(logDir);

        Logger loggerMain = Logger.getLogger(LOGGER_MAIN);
        loggerMain.addHandler(fileHandlerMap.get(LOGGER_MAIN));
        Logger loggerLookup = Logger.getLogger(LOGGER_LOOKUP_SERVICE);
        loggerLookup.addHandler(fileHandlerMap.get(LOGGER_LOOKUP_SERVICE));
        Logger loggerReply = Logger.getLogger(LOGGER_REPLY_SERVICE);
        loggerReply.addHandler(fileHandlerMap.get(LOGGER_REPLY_SERVICE));
        Logger loggerTrade = Logger.getLogger(LOGGER_TRADE);
        loggerTrade.addHandler(fileHandlerMap.get(LOGGER_TRADE));
        Logger loggerMeasureResult = Logger.getLogger(LOGGER_MESURE_RESULT);
        loggerMeasureResult.addHandler(fileHandlerMap.get(LOGGER_MESURE_RESULT));
    }
}
