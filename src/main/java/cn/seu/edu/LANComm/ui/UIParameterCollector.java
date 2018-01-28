package cn.seu.edu.LANComm.ui;

/**
 * 收集所有组件中被选择的值
 * 这里采用一种比较危险的方式实现
 * 由程序开发者保证数据的完整性与正确性
 * Created by Administrator on 2018/1/27.
 * @author WYCPhoenix
 * @date 2018-1-28-14:58
 */
public final class UIParameterCollector {
    private static String mode;
    private static String rb;
    private static String fc;
    private static String transmitGain;
    private static String receiveGain;
    private static String frequenceOffset;
    private static String hop;


    public static String getMode() {
        return mode;
    }

    public static void setMode(String mode) {
        UIParameterCollector.mode = mode;
    }

    public static String getRb() {
        return rb;
    }

    public static void setRb(String rb) {
        UIParameterCollector.rb = rb;
    }

    public static String getFc() {
        return fc;
    }

    public static void setFc(String fc) {
        UIParameterCollector.fc = fc;
    }

    public static String getTransmitGain() {
        return transmitGain;
    }

    public static void setTransmitGain(String transmitGain) {
        UIParameterCollector.transmitGain = transmitGain;
    }

    public static String getReceiveGain() {
        return receiveGain;
    }

    public static void setReceiveGain(String receiveGain) {
        UIParameterCollector.receiveGain = receiveGain;
    }

    public static String getFrequenceOffset() {
        return frequenceOffset;
    }

    public static void setFrequenceOffset(String frequenceOffset) {
        UIParameterCollector.frequenceOffset = frequenceOffset;
    }

    public static String getHop() {
        return hop;
    }

    public static void setHop(String hop) {
        UIParameterCollector.hop = hop;
    }
}
