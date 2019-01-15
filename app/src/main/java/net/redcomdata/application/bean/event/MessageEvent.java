package net.redcomdata.application.bean.event;

/**
 * <pre>
 *     author : leede
 *     time   : 2018/09/14
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class MessageEvent {
    private String message;
    private int msgType;//0,微信登录成功 1微信支付结果，2支付宝支付结果

    public  MessageEvent(String message){
        this.message=message;
    }

    public  MessageEvent(int msgType, String message){
        this.message=message;
        this.msgType = msgType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }
}
