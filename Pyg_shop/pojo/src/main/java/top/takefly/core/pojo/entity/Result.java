package top.takefly.core.pojo.entity;

import java.io.Serializable;

/**
 *
 */
public class Result  implements Serializable {

    //标志此时是否成功
    /**
     *成功标志
     */
    private boolean success;
    //返回提示信息
    /**
     *提示信息
     */
    private String info;

    public Result(boolean success, String info) {
        this.success = success;
        this.info = info;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
