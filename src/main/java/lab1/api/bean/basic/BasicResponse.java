package lab1.api.bean.basic;

import java.io.Serializable;

/**
 * @project: CS677_Lab1
 * @description:
 * @author: zhexu
 * @create: 10/26/22
 **/
public class BasicResponse implements Serializable {
    private Integer status;
    private String discription;
    private String message;

    public BasicResponse(Integer status, String discription) {
        this.status = status;
        this.discription = discription;
        this.message = this.discription;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDiscription() {
        return discription;
    }

    public void setDiscription(String discription) {
        this.discription = discription;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "BasicResponse{" +
                "status=" + status +
                ", discription='" + discription + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
