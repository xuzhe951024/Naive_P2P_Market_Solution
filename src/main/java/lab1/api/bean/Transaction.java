package lab1.api.bean;

import lab1.api.bean.basic.Address;
import lab1.api.bean.basic.Product;

import java.io.Serializable;
import java.util.*;

import static lab1.constants.Const.MILLISECOND;

/**
 * @project: CS677_Lab1
 * @description:
 * @author: zhexu
 * @create: 10/26/22
 **/
public class Transaction implements Serializable {
    private UUID transactionId;
    private Address sender;
    private UUID initiator;
    private Integer jumpNum;
    private Product product;
    private Integer purchaseNumber;

    private long createTime;
    private Integer maxJump;
    private Map<String, Long> lifeCycle = new HashMap<>();
    private Long lastUpdateTime = 0L;

    public Transaction() {
    }

    public Transaction(UUID transactionId, Address sender, UUID initiator, Integer jumpNum, Product product, Integer purchaseNumber, long createTime, Integer maxJump) {
        this.transactionId = transactionId;
        this.sender = sender;
        this.initiator = initiator;
        this.jumpNum = jumpNum;
        this.product = product;
        this.purchaseNumber = purchaseNumber;
        this.createTime = createTime;
        this.maxJump = maxJump;
    }

    public void addRecordToLifeCycle(String domain, Long time) {
        this.lifeCycle.put(domain, time - this.lastUpdateTime);
        this.lastUpdateTime = time;
    }

    public List<String> getJumpPath() {
        List<String> path = new LinkedList<String>();
        for (String domain : this.lifeCycle.keySet()) {
            String[] split = domain.split("\\.");
            path.add(split[2] + ":" + this.lifeCycle.get(domain) + MILLISECOND);
        }
        return path;
    }

    public Long getMeanTransTime() {
        Long totalTime = 0L;
        for (Long time : this.lifeCycle.values()) {
            totalTime += time;
        }
        return totalTime / this.lifeCycle.size();
    }

    public Long getTotalTransTime(){
        Long totalTime = 0L;
        for (Long time : this.lifeCycle.values()) {
            totalTime += time;
        }
        return totalTime;
    }

    public Boolean reachMaxJump() {
        return this.jumpNum >= this.maxJump;
    }

    public Integer jumpIncreasing() {
        this.jumpNum += 1;
        return this.jumpNum;
    }

    public Integer jumDecreasing() {
        if (this.jumpNum >= 1) {
            this.jumpNum -= 1;
        }
        return this.jumpNum;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(UUID transactionId) {
        this.transactionId = transactionId;
    }

    public Address getSender() {
        return sender;
    }

    public void setSender(Address sender) {
        this.sender = sender;
    }

    public UUID getInitiator() {
        return initiator;
    }

    public void setInitiator(UUID initiator) {
        this.initiator = initiator;
    }

    public Integer getJumpNum() {
        return jumpNum;
    }

    public void setJumpNum(Integer jumpNum) {
        this.jumpNum = jumpNum;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getPurchaseNumber() {
        return purchaseNumber;
    }

    public void setPurchaseNumber(Integer purchaseNumber) {
        this.purchaseNumber = purchaseNumber;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public Integer getMaxJump() {
        return maxJump;
    }

    public void setMaxJump(Integer maxJump) {
        this.maxJump = maxJump;
    }

    public Map<String, Long> getLifeCycle() {
        return lifeCycle;
    }

    public void setLifeCycle(Map<String, Long> lifeCycle) {
        this.lifeCycle = lifeCycle;
    }

    public Long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId=" + transactionId +
                ", sender=" + sender +
                ", initiator=" + initiator +
                ", jumpNum=" + jumpNum +
                ", product=" + product +
                ", purchaseNumber=" + purchaseNumber +
                ", createTime=" + createTime +
                ", maxJump=" + maxJump +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return transactionId.equals(that.transactionId) && initiator.equals(that.initiator) && product.equals(that.product);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionId, initiator, product);
    }
}
