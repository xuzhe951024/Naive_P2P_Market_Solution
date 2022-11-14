package lab1.rpcServer.service.impl;

import lab1.api.bean.Seller;
import lab1.api.bean.Transaction;
import lab1.api.bean.basic.PeerBase;
import lab1.api.bean.basic.Product;
import lab1.api.bean.basic.factories.SingletonFactory;
import lab1.constants.Const;
import lab1.api.bean.basic.Address;
import lab1.api.bean.basic.BasicResponse;
import lab1.rpcClient.proxy.ProxyFactory;
import lab1.rpcClient.proxy.RPCInvocationHandler;
import lab1.rpcServer.service.LookupService;
import lab1.rpcServer.service.ReplyService;
import org.springframework.beans.BeanUtils;

import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;

import static lab1.constants.Const.*;
import static lab1.constants.ResponseCode.*;

/**
 * @project: CS677_Lab1
 * @description:
 * @author: zhexu
 * @create: 10/27/22
 **/
public class LookupServiceImpl implements LookupService {
    private Logger logger = Logger.getLogger(Const.LOGGER_LOOKUP_SERVICE);
    private PeerBase peerSelf;

    public LookupServiceImpl() {
        if (ROLE_SELLER.equals(SingletonFactory.getRole())) {
            this.peerSelf = SingletonFactory.getSeller();
        } else if (ROLE_BUYER.equals(SingletonFactory.getRole())) {
            this.peerSelf = SingletonFactory.getBuyer();
        }
    }

    @Override
    public Transaction lookUpGenerator(Product product, Integer purchaseNumber, Integer maxJump) {
        UUID transactionId = UUID.randomUUID();
        Transaction transaction = new Transaction(transactionId,
                peerSelf.getSelfAddress(),
                peerSelf.getId(),
                0,
                product,
                purchaseNumber,
                System.currentTimeMillis(),
                maxJump);
        return transaction;
    }

    @Override
    public BasicResponse lookupSender(Transaction transaction) {
        transaction.addRecordToLifeCycle(peerSelf.getSelfAddress().getDomain(), System.currentTimeMillis());

        logger.info("Received transaction:" + SPACE +
                transaction.getTransactionId() + SPACE +
                "from: " + SPACE +
                transaction.getSender().getDomain() + ENTER);

        if (transaction.reachMaxJump()) {
            BasicResponse response = new BasicResponse(STATUS_TIME_OUT, GET_DESCRIPTIONS.get(STATUS_TIME_OUT));
            logger.info("Transaction:" + SPACE +
                    transaction.getTransactionId() + SPACE +
                    "has reach the limitation of max Jump, abort." + ENTER);
            return response;
        }

        if (ROLE_SELLER.equals(SingletonFactory.getRole())) {
            logger.info("Seller Processing:");
            Seller seller = (Seller) this.peerSelf;
            if (seller.ifProductInStore(transaction.getProduct())) {
                if (this.peerSelf.checkIfTransactionRecorded(transaction.hashCode())) {
                    logger.info("transaction:" + SPACE +
                            transaction.getTransactionId() + SPACE +
                            " has already be processed!" + ENTER);
                    BasicResponse response = new BasicResponse(STATUS_NOT_IMPLEMENTED, GET_DESCRIPTIONS.get(STATUS_NOT_IMPLEMENTED));
                    return response;
                } else {
                    Thread replyToBuyer = new Thread(() -> replyTransaction(transaction));
                    replyToBuyer.start();
                    BasicResponse response = new BasicResponse(STATUS_ACCEPTED, GET_DESCRIPTIONS.get(STATUS_ACCEPTED));
                    response.setMessage("Seller found!\nSending reply:\n" + transaction + "\nto peer:\n" + transaction.getSender().toString() + ENTER);
                    logger.info(response.toString());
                    return response;
                }
            }
        }
        Thread keepLookingUp = new Thread(() -> keepLookingUpFunc(transaction));
        keepLookingUp.start();
        BasicResponse response = new BasicResponse(STATUS_ACCEPTED, GET_DESCRIPTIONS.get(STATUS_ACCEPTED));
        return response;
    }

    private void replyTransaction(Transaction transaction) {
        Transaction responseTransaction = new Transaction();
        BeanUtils.copyProperties(transaction, responseTransaction);
        responseTransaction.setSender(this.peerSelf.getSelfAddress());
        RPCInvocationHandler handler = new RPCInvocationHandler(transaction.getSender());
        ReplyService replyService = ProxyFactory.getInstance(ReplyService.class, handler);
        BasicResponse response = replyService.replySender(responseTransaction);
        logger.info("result:\n" + response.toString() + ENTER);
    }

    private void keepLookingUpFunc(Transaction transaction) {
        if (peerSelf.addTransactionProccessRecordMap(transaction)) {
            transaction.jumpIncreasing();
            transaction.setSender(peerSelf.getSelfAddress());
            for (Address peer : peerSelf.getNeighbourPeerList()) {
                RPCInvocationHandler handler = new RPCInvocationHandler(peer);
                LookupService lookupService = ProxyFactory.getInstance(LookupService.class, handler);
                logger.info("Sending transaction:\n" + transaction + "\nto peer:\n" + peer.toString() + ENTER);
                BasicResponse response = lookupService.lookupSender(transaction);
                logger.info("result:\n" + response.toString() + ENTER);
            }
        } else {
            logger.info(ENTER + "Transaction request with a lower number of jumping is exsisted!" + ENTER);
        }
    }
}
