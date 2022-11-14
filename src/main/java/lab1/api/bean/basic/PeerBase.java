package lab1.api.bean.basic;

import lab1.api.bean.Transaction;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static lab1.constants.Const.ONE;

/**
 * @project: CS677_Lab1
 * @description:
 * @author: zhexu
 * @create: 10/25/22
 **/
public class PeerBase implements Serializable {
    private UUID Id;
    private List<Address> neighbourPeerList;
    private Map<Integer, Transaction> transactionProccessRecordMap;
    private Address selfAddress;
    private Long measureTimes = 1L;
    private Long averageResponseTime = 0L;

    public Boolean addTransactionProccessRecordMap(Transaction transaction) {
        if (this.transactionProccessRecordMap.containsKey(transaction.hashCode())
                && this.transactionProccessRecordMap.get(transaction.hashCode()).getJumpNum() <= transaction.getJumpNum()) {
            return false;
        } else {
            Transaction copyTransaction = new Transaction();
            BeanUtils.copyProperties(transaction, copyTransaction);
            this.transactionProccessRecordMap.put(copyTransaction.hashCode(), copyTransaction);
            return true;
        }
    }

    public void updateMeanTime(Long responseTime){
        this.averageResponseTime = (this.averageResponseTime * this.measureTimes + responseTime) / (this.measureTimes + ONE);
        this.measureTimes += ONE;
    }

    public Transaction popTransactionProccessRecordMap(Transaction transaction) {
        if (this.transactionProccessRecordMap.containsKey(transaction.hashCode())) {
            Transaction response = this.transactionProccessRecordMap.get(transaction.hashCode());
            this.transactionProccessRecordMap.remove(transaction.hashCode());
            return response;
        } else {
            return null;
        }
    }

    public Boolean checkIfTransactionRecorded(Integer transactionHash) {
        return this.transactionProccessRecordMap.containsKey(transactionHash);
    }

    public Boolean isTransactionInitiator(Transaction transaction) {
        if (this.transactionProccessRecordMap.containsKey(transaction.hashCode())) {
            if (this.Id.equals(this.transactionProccessRecordMap.get(transaction.hashCode()).getInitiator())) {
                return true;
            }
        }
        return false;
    }

    public Boolean removeTransactionRecordSuccessful(Integer transactionHash) {
        if (checkIfTransactionRecorded(transactionHash)) {
            this.transactionProccessRecordMap.remove(transactionHash);
        }
        return !checkIfTransactionRecorded(transactionHash);
    }

    public PeerBase(UUID id, List<Address> neighbourPeerList, Map<Integer, Transaction> transactionProccessRecordMap, Address selfAddress) {
        Id = id;
        this.neighbourPeerList = neighbourPeerList;
        this.transactionProccessRecordMap = transactionProccessRecordMap;
        this.selfAddress = selfAddress;
    }

    public UUID getId() {
        return Id;
    }

    public void setId(UUID id) {
        Id = id;
    }

    public List<Address> getNeighbourPeerList() {
        return neighbourPeerList;
    }

    public void setNeighbourPeerList(List<Address> neighbourPeerList) {
        this.neighbourPeerList = neighbourPeerList;
    }

    public Map<Integer, Transaction> getTransactionProccessRecordMap() {
        return transactionProccessRecordMap;
    }

    public void setTransactionProccessRecordMap(Map<Integer, Transaction> transactionProccessRecordMap) {
        this.transactionProccessRecordMap = transactionProccessRecordMap;
    }

    public Address getSelfAddress() {
        return selfAddress;
    }

    public void setSelfAddress(Address selfAddress) {
        this.selfAddress = selfAddress;
    }

    public Long getMeasureTimes() {
        return measureTimes;
    }

    public void setMeasureTimes(Long measureTimes) {
        this.measureTimes = measureTimes;
    }

    public Long getAverageResponseTime() {
        return averageResponseTime;
    }

    public void setAverageResponseTime(Long averageResponseTime) {
        this.averageResponseTime = averageResponseTime;
    }

    @Override
    public String toString() {
        return "PeerBase{" +
                "Id=" + Id +
                ", neighbourPeerList=" + neighbourPeerList +
                ", transactionPathMap=" + transactionProccessRecordMap +
                ", address=" + selfAddress +
                '}';
    }
}
