package lab1.api.bean.config;

import java.util.List;

/**
 * @project: CS677_Lab1
 * @description:
 * @author: zhexu
 * @create: 10/28/22
 **/
public class ProfilesBean {
    private Integer sellerNumber;
    private Integer buyerNumber;
    private Integer port;
    private Integer maxmumStok;
    private List<String> productNameList;
    private Boolean deployOnSingleComputer;
    private Integer neighbourNum;
    private Integer maxJump;
    private Integer sleepBeforeStart;
    private Integer numberOfTests;


    public Integer getNumberOfTests() {
        return numberOfTests;
    }

    public void setNumberOfTests(Integer numberOfTests) {
        this.numberOfTests = numberOfTests;
    }

    public Integer getMaxJump() {
        return maxJump;
    }

    public void setMaxJump(Integer maxJump) {
        this.maxJump = maxJump;
    }

    public Integer getNeighbourNum() {
        return neighbourNum;
    }

    public void setNeighbourNum(Integer neighbourNum) {
        this.neighbourNum = neighbourNum;
    }

    public Boolean getDeployOnSingleComputer() {
        return deployOnSingleComputer;
    }

    public void setDeployOnSingleComputer(Boolean deployOnSingleComputer) {
        this.deployOnSingleComputer = deployOnSingleComputer;
    }

    public Integer getSellerNumber() {
        return sellerNumber;
    }

    public void setSellerNumber(Integer sellerNumber) {
        this.sellerNumber = sellerNumber;
    }

    public Integer getBuyerNumber() {
        return buyerNumber;
    }

    public void setBuyerNumber(Integer buyerNumber) {
        this.buyerNumber = buyerNumber;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getMaxmumStok() {
        return maxmumStok;
    }

    public void setMaxmumStok(Integer maxmumStok) {
        this.maxmumStok = maxmumStok;
    }

    public List<String> getProductNameList() {
        return productNameList;
    }

    public void setProductNameList(List<String> productNameList) {
        this.productNameList = productNameList;
    }

    public Integer getSleepBeforeStart() {
        return sleepBeforeStart;
    }

    public void setSleepBeforeStart(Integer sleepBeforeStart) {
        this.sleepBeforeStart = sleepBeforeStart;
    }

    @Override
    public String toString() {
        return "ProfilesBean{" +
                "sellerNumber=" + sellerNumber +
                ", buyerNumber=" + buyerNumber +
                ", port=" + port +
                ", maxmumStok=" + maxmumStok +
                ", productNameList=" + productNameList +
                ", deployOnSingleComputer=" + deployOnSingleComputer +
                ", neighbourNum=" + neighbourNum +
                ", maxJump=" + maxJump +
                ", sleepBeforeStart=" + sleepBeforeStart +
                ", numberOfTests=" + numberOfTests +
                '}';
    }
}
