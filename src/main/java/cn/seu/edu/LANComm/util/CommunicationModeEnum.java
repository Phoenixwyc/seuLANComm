package cn.seu.edu.LANComm.util;

/**
 * Created by Administrator on 2018/1/26.
 * @author WYCPhoenix
 * @date 2018-1-26-18:00
 *
 * 支持的通信类型枚举
 */
public enum CommunicationModeEnum {
    /**
     * DQPSK模式
     */
    DQOSK_MODE("DQPSK"),
    /**
     * DQPSK-DSSS模式
     */
    DQOSK_DSSS_MODE("DQPSK-DSSS"),
    /**
     * DQPSK-FH模式
     */
    DQOSK_FH_MODE("DQPSK-FH");
    private String communicationMode;
    CommunicationModeEnum(String communicationMode) {
        this.communicationMode = communicationMode;
    }

    public String getCommunicationMode() {
        return this.communicationMode;
    }

    @Override
    public String toString() {
        return "CommunicationModeEnum{" +
                "communicationMode='" + communicationMode + '\'' +
                '}';
    }
}
