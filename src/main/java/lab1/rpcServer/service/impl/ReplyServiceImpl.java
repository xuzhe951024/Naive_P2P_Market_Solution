package lab1.rpcServer.service.impl;

import lab1.api.bean.Buyer;
import lab1.api.bean.Transaction;
import lab1.api.bean.basic.Address;
import lab1.api.bean.basic.BasicResponse;
import lab1.api.bean.basic.PeerBase;
import lab1.api.bean.basic.factories.SingletonFactory;
import lab1.constants.Const;
import lab1.rpcClient.proxy.ProxyFactory;
import lab1.rpcClient.proxy.RPCInvocationHandler;
import lab1.rpcServer.service.HelloService;
import lab1.rpcServer.service.PurchaseService;
import lab1.rpcServer.service.ReplyService;

import java.util.logging.Logger;
;
import static lab1.constants.Const.*;
import static lab1.constants.ResponseCode.*;
import static lab1.constants.ResponseCode.STATUS_NOT_IMPLEMENTED;

/**
 * @project: CS677_Lab1
 * @description:
 * @author: zhexu
 * @create: 10/28/22
 **/
public class ReplyServiceImpl implements ReplyService {
    private Logger logger = Logger.getLogger(LOGGER_REPLY_SERVICE);
    private Logger loggerMeasureResult = Logger.getLogger(LOGGER_MESURE_RESULT);
    private PeerBase peerSelf;

    public ReplyServiceImpl() {
        if (ROLE_SELLER.equals(SingletonFactory.getRole())) {
            this.peerSelf = SingletonFactory.getSeller();
        } else if (ROLE_BUYER.equals(SingletonFactory.getRole())) {
            this.peerSelf = SingletonFactory.getBuyer();
        }
    }

    @Override
    public BasicResponse replySender(Transaction transaction) {
        transaction.addRecordToLifeCycle(peerSelf.getSelfAddress().getDomain(), System.currentTimeMillis());

        logger.info("Received reply:" + SPACE +
                transaction.getTransactionId() + SPACE +
                "from: " + SPACE +
                transaction.getSender().getDomain() + ENTER);

        if (ROLE_BUYER.equals(SingletonFactory.getRole()) &&
                this.peerSelf.isTransactionInitiator(transaction)) {
            if (!this.peerSelf.checkIfTransactionRecorded(transaction.hashCode())) {
                logger.info("transaction:" + SPACE +
                        transaction.getTransactionId() + SPACE +
                        " has already be processed!" + ENTER);
                BasicResponse response = new BasicResponse(STATUS_NOT_IMPLEMENTED, GET_DESCRIPTIONS.get(STATUS_NOT_IMPLEMENTED));
                return response;
            } else {
                long transactionEndTime = System.currentTimeMillis();
                logger.info("Transaction: " + SPACE +
                        transaction.getTransactionId() +
                        "\nresponse time: " + SPACE +
                        (transactionEndTime - transaction.getCreateTime()) / ONE_THOUSAND + SPACE +
                        " seconds" + ENTER);
                Thread purchase = new Thread(() -> startPurchasing(transaction));
                purchase.start();
                BasicResponse response = new BasicResponse(STATUS_ACCEPTED, GET_DESCRIPTIONS.get(STATUS_ACCEPTED));
                return response;
            }
        }

        Thread keepReplying = new Thread(() -> keepReplyingFunc(transaction));
        keepReplying.start();
        BasicResponse response = new BasicResponse(STATUS_ACCEPTED, GET_DESCRIPTIONS.get(STATUS_ACCEPTED));
        return response;
    }

    private void startPurchasing(Transaction transaction) {
        //              starting purchasing
        RPCInvocationHandler handler = new RPCInvocationHandler(transaction.getSender());
        PurchaseService purchaseService = ProxyFactory.getInstance(PurchaseService.class, handler);
        logger.info("Starting purchase to: " + SPACE +
                transaction.getSender() + SPACE +
                "product: " + SPACE +
                transaction.getProduct() + ENTER);
        BasicResponse response = purchaseService.purchase(transaction);
        logger.info("result:\n" + response.toString() + ENTER);

        peerSelf.updateMeanTime(transaction.getTotalTransTime());
        loggerMeasureResult.info(IMPORTANT_LOG_WRAPPER +
                "testing number: " + peerSelf.getMeasureTimes() + ENTER +
                "transaction:" + SPACE +
                transaction.getTransactionId() + SPACE +
                "jump path:" + ENTER +
                transaction.getJumpPath() + ENTER +
                "mean transport time:" + ENTER +
                transaction.getMeanTransTime() + MILLISECOND + ENTER +
                "mean response time:" + ENTER +
                peerSelf.getAverageResponseTime() + MILLISECOND +
                IMPORTANT_LOG_WRAPPER + ENTER);
    }

    private void keepReplyingFunc(Transaction transaction) {
        Transaction replyTransaction = peerSelf.popTransactionProccessRecordMap(transaction);
        if (null != replyTransaction) {
            Address nextAdd = replyTransaction.getSender();
            RPCInvocationHandler handler = new RPCInvocationHandler(nextAdd);
            ReplyService replyService = ProxyFactory.getInstance(ReplyService.class, handler);
            logger.info("Sending reply:\n" + transaction + "\nto peer:\n" + nextAdd.toString() + ENTER);
            BasicResponse response = replyService.replySender(transaction);
            logger.info("result:\n" + response.toString() + ENTER);
        } else {
            logger.info(ENTER + "Transaction record does not exsist!" + ENTER);
        }
    }
}
