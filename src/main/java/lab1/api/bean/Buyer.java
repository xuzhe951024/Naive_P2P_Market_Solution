package lab1.api.bean;

import lab1.api.bean.basic.Address;
import lab1.api.bean.basic.PeerBase;
import lab1.api.bean.basic.Product;
import lab1.api.bean.basic.factories.SingletonFactory;
import lab1.rpcServer.service.impl.LookupServiceImpl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Logger;

import static lab1.constants.Const.*;

/**
 * @project: CS677_Lab1
 * @description:
 * @author: zhexu
 * @create: 10/25/22
 **/
public class Buyer extends PeerBase implements Serializable {
    private List<Product> shopList;

    public Buyer(UUID id, List<Address> neighbourPeerList, Map<Integer, Transaction> transactionPathMap, Address address, List shopList) {
        super(id, neighbourPeerList, transactionPathMap, address);
        this.shopList = shopList;
    }

    public void startLookUp(Integer purchaseNumber,
                            Integer maxJump,
                            Integer lookupStartDelay,
                            Integer testNum) throws InterruptedException {
        Logger logger = Logger.getLogger(LOGGER_MAIN);
        logger.info(this.getSelfAddress().getDomain() + SPACE +
                "start looking up!");
        Random ra = new Random();
        LookupServiceImpl lookupService = new LookupServiceImpl();
        while (0 != testNum * ONE) {
            testNum -= testNum > 0 ? 1 : 0;
            Product product = this.shopList.get(ra.nextInt(this.shopList.size()));
            Transaction transaction = lookupService.lookUpGenerator(product, purchaseNumber, maxJump);
            lookupService.lookupSender(transaction);

            Integer waitBeforeNextRequest = ra.nextInt(lookupStartDelay) / ONE_HUNDRED;
            logger.info("Transaction" + SPACE +
                    "finished! now waiting for" + SPACE +
                    waitBeforeNextRequest + SPACE + MILLISECOND +
                    " before starting next transaction" + ENTER);
            Thread.sleep(waitBeforeNextRequest);
        }
    }

    @Override
    public String toString() {
        return super.toString() + "Buyer{" +
                "shopList=" + shopList +
                '}';
    }
}
