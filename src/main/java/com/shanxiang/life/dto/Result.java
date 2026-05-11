package com.shanxiang.life.dto;

public class Result {
    private Boolean success;
    private String errorMsg;
    private Object data;

    // 成功放行时调用的方法
    public static Result ok(Object data) {
        Result result = new Result();
        result.success = true;
        result.data = data;
        return result;
    }

    public static Result ok() {
        Result result = new Result();
        result.success = true;
        return result;
    }

    // 限流阻断时调用的方法
    public static Result fail(String errorMsg) {
        Result result = new Result();
        result.success = false;
        result.errorMsg = errorMsg;
        return result;
    }

    // 必须保留的底层序列化方法
    public Boolean getSuccess() { return success; }
    public void setSuccess(Boolean success) { this.success = success; }
    public String getErrorMsg() { return errorMsg; }
    public void setErrorMsg(String errorMsg) { this.errorMsg = errorMsg; }
    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }
}