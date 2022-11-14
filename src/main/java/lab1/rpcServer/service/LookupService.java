package lab1.rpcServer.service;

import lab1.api.bean.Transaction;
import lab1.api.bean.basic.BasicResponse;
import lab1.api.bean.basic.Product;

/**
 * @project: CS677_Lab1
 * @description:
 * @author: zhexu
 * @create: 10/26/22
 **/
public interface LookupService {
    Transaction lookUpGenerator(Product product, Integer purchaseNumber, Integer maxJump);
    BasicResponse lookupSender(Transaction transaction);
}
