package lab1.rpcServer.service;

import lab1.api.bean.Transaction;
import lab1.api.bean.basic.BasicResponse;

/**
 * @project: CS677_Lab1
 * @description:
 * @author: zhexu
 * @create: 10/26/22
 **/
public interface ReplyService {
    BasicResponse replySender(Transaction transaction);
}
