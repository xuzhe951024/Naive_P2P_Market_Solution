package lab1.api.bean.config;

import lab1.api.bean.basic.Address;
import lab1.api.bean.basic.Product;
import lab1.api.bean.config.basic.InitConfigBasic;

import java.util.List;


/**
 * @project: CS677_Lab1
 * @description:
 * @author: zhexu
 * @create: 10/27/22
 **/
public class InitConfigForBuyer extends InitConfigBasic {

    private Integer maxJump;

    public Integer getMaxJump() {
        return maxJump;
    }

    public void setMaxJump(Integer maxJump) {
        this.maxJump = maxJump;
    }
}
