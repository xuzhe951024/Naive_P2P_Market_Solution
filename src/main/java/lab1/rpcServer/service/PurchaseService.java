package lab1.rpcServer.service;

import lab1.api.bean.Transaction;
import lab1.api.bean.basic.Address;
import lab1.api.bean.basic.BasicResponse;
import lab1.api.bean.basic.Product;

/**
 * @project: CS677_Lab1
 * @description:
 * @author: zhexu
 * @create: 10/28/22
 **/
public interface PurchaseService {
    public BasicResponse purchase(Transaction transaction);
}
