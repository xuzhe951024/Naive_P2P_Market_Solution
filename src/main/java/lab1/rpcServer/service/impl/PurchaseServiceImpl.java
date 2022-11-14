package lab1.rpcServer.service.impl;

import lab1.api.bean.Seller;
import lab1.api.bean.Transaction;
import lab1.api.bean.basic.Address;
import lab1.api.bean.basic.BasicResponse;
import lab1.api.bean.basic.Product;
import lab1.api.bean.basic.factories.SingletonFactory;
import lab1.rpcServer.service.PurchaseService;

import java.util.logging.Logger;

import static lab1.constants.Const.*;
import static lab1.constants.ResponseCode.*;

/**
 * @project: CS677_Lab1
 * @description:
 * @author: zhexu
 * @create: 10/28/22
 **/
public class PurchaseServiceImpl implements PurchaseService {
    private Logger logger = Logger.getLogger(LOGGER_TRADE);

    @Override
    public BasicResponse purchase(Transaction transaction) {
        if (ROLE_SELLER.equals(SingletonFactory.getRole())) {
            Seller seller = SingletonFactory.getSeller();

            transaction.addRecordToLifeCycle(seller.getSelfAddress().getDomain(), System.currentTimeMillis());
            seller.updateMeanTime(transaction.getTotalTransTime());
            logger.info(IMPORTANT_LOG_WRAPPER +
                    "testing number: " + seller.getMeasureTimes() + ENTER +
                    "transaction:" + SPACE +
                    transaction.getTransactionId() + SPACE +
                    "jump path:" + ENTER +
                    transaction.getJumpPath() + ENTER +
                    "mean transport time:" + ENTER +
                    transaction.getMeanTransTime() + MILLISECOND + ENTER +
                    "mean response time:" + ENTER +
                    seller.getAverageResponseTime() + MILLISECOND +
                    IMPORTANT_LOG_WRAPPER + ENTER
            );

            Integer sellResult = seller.cosumeStock(transaction.getProduct(), transaction.getPurchaseNumber());
            if (0 < sellResult) {
                logger.info("Success purchased from buyer: " + SPACE +
                        transaction.getInitiator() + SPACE +
                        "product: " + SPACE +
                        transaction.getProduct() + SPACE +
                        "number: " + SPACE +
                        transaction.getPurchaseNumber() + SPACE +
                        "stock status:" + SPACE +
                        seller.getStock() + ENTER);

                BasicResponse response = new BasicResponse(STATUS_SUCCESS, GET_DESCRIPTIONS.get(STATUS_SUCCESS));
                return response;
            } else if (-1 == sellResult) {
                logger.info("Failed purchased from buyer: " +
                        transaction.getInitiator() + SPACE +
                        "product: " + SPACE +
                        transaction.getProduct() + SPACE +
                        "number:" +
                        transaction.getPurchaseNumber() + ENTER);
                BasicResponse response = new BasicResponse(STATUS_FORBIDDEN, GET_DESCRIPTIONS.get(STATUS_FORBIDDEN));
                response.setMessage(ENTER + "No Product in stock!" + ENTER);
                return response;
            } else {
                logger.info("No enough sock of product: " + SPACE +
                        transaction.getProduct() + SPACE +
                        "for purchase number of:" +
                        transaction.getPurchaseNumber() + ENTER);
                if (0 >= seller.getStock().get(transaction.getProduct())) {
                    seller.reImportWhenSold();
                }
                BasicResponse response = new BasicResponse(STATUS_FORBIDDEN, GET_DESCRIPTIONS.get(STATUS_FORBIDDEN));
                response.setMessage(ENTER + "Product stock insufficient!" + ENTER);
                return response;
            }
        } else {
            logger.info(ENTER + "Prohibition of payment to buyer!" + ENTER);
            BasicResponse response = new BasicResponse(STATUS_FORBIDDEN, GET_DESCRIPTIONS.get(STATUS_FORBIDDEN));
            return response;
        }
    }
}
