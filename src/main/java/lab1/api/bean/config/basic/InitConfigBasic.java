package lab1.api.bean.config.basic;

import lab1.api.bean.basic.Address;
import lab1.api.bean.basic.Product;

import java.util.List;

/**
 * @project: CS677_Lab1
 * @description:
 * @author: zhexu
 * @create: 10/28/22
 **/
public class InitConfigBasic {
    private List<Address> neighbours;
    private  Address selfAdd;

    private List<Product> products;

    public List<Address> getNeighbours() {
        return neighbours;
    }

    public void setNeighbours(List<Address> neighbours) {
        this.neighbours = neighbours;
    }

    public Address getSelfAdd() {
        return selfAdd;
    }

    public void setSelfAdd(Address selfAdd) {
        this.selfAdd = selfAdd;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
