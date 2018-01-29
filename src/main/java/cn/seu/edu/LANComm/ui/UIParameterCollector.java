package cn.seu.edu.LANComm.ui;

import cn.seu.edu.LANComm.util.CommunicationParameterEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * 收集所有组件中被选择的值
 * 这里采用一种比较危险的方式实现
 * 由程序开发者保证数据的完整性与正确性
 * Created by Administrator on 2018/1/27.
 * @author WYCPhoenix
 * @date 2018-1-28-14:58
 */
public final class UIParameterCollector {
    /**
     * 增加一个field与对应method的映射关系
     * 便于后序方法调用
     * key 通信参数名
     * value 值对应的setter方法名
     */
    private static  final Map<String, String> methodNameMappingValueSetter = new HashMap<>();
    static {
        methodNameMappingValueSetter.put(CommunicationParameterEnum.PARAMETER_FC.getCommunicationParameter(), "setFc");
        methodNameMappingValueSetter.put(CommunicationParameterEnum.PARAMETER_FH_HOP.getCommunicationParameter(), "setHop");
        methodNameMappingValueSetter.put(CommunicationParameterEnum.PARAMETER_FREQUENCE_OFFSET.getCommunicationParameter(), "setFrequenceOffset");
        methodNameMappingValueSetter.put(CommunicationParameterEnum.PARAMETER_RECEIVE_GAIN.getCommunicationParameter(), "setReceiveGain");
        methodNameMappingValueSetter.put(CommunicationParameterEnum.PARAMETER_TRANSMIT_GAIN.getCommunicationParameter(), "setTransmitGain");
        methodNameMappingValueSetter.put(CommunicationParameterEnum.PARAMETER_RB.getCommunicationParameter(), "setRb");

    }
    /**
     * 增加一个field与对应method的映射关系
     * 便于后序方法调用
     * key 通信参数名
     * value 单位对应的setter方法名
     */
    private static final Map<String, String> methodNameMappingUnitSetter = new HashMap<>();
    static {
        methodNameMappingUnitSetter.put(CommunicationParameterEnum.PARAMETER_FC.getCommunicationParameter(), "setFcUnit");
        methodNameMappingUnitSetter.put(CommunicationParameterEnum.PARAMETER_FH_HOP.getCommunicationParameter(), "setHopUnit");
        methodNameMappingUnitSetter.put(CommunicationParameterEnum.PARAMETER_FREQUENCE_OFFSET.getCommunicationParameter(), "setFrequenceOffsetUnit");
        methodNameMappingUnitSetter.put(CommunicationParameterEnum.PARAMETER_RECEIVE_GAIN.getCommunicationParameter(), "setReceiveGainUnit");
        methodNameMappingUnitSetter.put(CommunicationParameterEnum.PARAMETER_TRANSMIT_GAIN.getCommunicationParameter(), "setTransmitGainUnit");
        methodNameMappingUnitSetter.put(CommunicationParameterEnum.PARAMETER_RB.getCommunicationParameter(), "setRbUnit");

    }
    /**
     * 通信模式
     */
    private String mode;
    /**
     * 码元速率与单位
     */
    private Double rb;
    private String rbUnit;
    /**
     * 载波频率与单位
     */
    private Double fc;
    private String fcUnit;
    /**
     * 发射增益与单位
     */
    private Double transmitGain;
    private String transmitGainUnit;
    /**
     * 接收增益与单位
     */
    private Double receiveGain;
    private String receiveGainUnit;
    /**
     * 信号频偏与单位
     */
    private Double frequenceOffset;
    private String frequenceOffsetUnit;
    /**
     * 跳频速率与单位
     */
    private Double hop;
    private String hopUnit;
    /**
     * 收发模式选择
     */
    private String switchTransmitAndReceive;
    private boolean confirmButtonIsSelected;

    public UIParameterCollector() {}

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public Double getRb() {
        return rb;
    }

    public void setRb(Double rb) {
        this.rb = rb;
    }

    public String getRbUnit() {
        return rbUnit;
    }

    public void setRbUnit(String rbUnit) {
        this.rbUnit = rbUnit;
    }

    public Double getFc() {
        return fc;
    }

    public void setFc(Double fc) {
        this.fc = fc;
    }

    public String getFcUnit() {
        return fcUnit;
    }

    public void setFcUnit(String fcUnit) {
        this.fcUnit = fcUnit;
    }

    public Double getTransmitGain() {
        return transmitGain;
    }

    public void setTransmitGain(Double transmitGain) {
        this.transmitGain = transmitGain;
    }

    public String getTransmitGainUnit() {
        return transmitGainUnit;
    }

    public void setTransmitGainUnit(String transmitGainUnit) {
        this.transmitGainUnit = transmitGainUnit;
    }

    public Double getReceiveGain() {
        return receiveGain;
    }

    public void setReceiveGain(Double receiveGain) {
        this.receiveGain = receiveGain;
    }

    public String getReceiveGainUnit() {
        return receiveGainUnit;
    }

    public void setReceiveGainUnit(String receiveGainUnit) {
        this.receiveGainUnit = receiveGainUnit;
    }

    public Double getFrequenceOffset() {
        return frequenceOffset;
    }

    public void setFrequenceOffset(Double frequenceOffset) {
        this.frequenceOffset = frequenceOffset;
    }

    public String getFrequenceOffsetUnit() {
        return frequenceOffsetUnit;
    }

    public void setFrequenceOffsetUnit(String frequenceOffsetUnit) {
        this.frequenceOffsetUnit = frequenceOffsetUnit;
    }

    public Double getHop() {
        return hop;
    }

    public void setHop(Double hop) {
        this.hop = hop;
    }

    public String getHopUnit() {
        return hopUnit;
    }

    public void setHopUnit(String hopUnit) {
        this.hopUnit = hopUnit;
    }

    public static Map<String, String> getMethodNameMappingUnitSetter() {
        return methodNameMappingUnitSetter;
    }

    public static Map<String, String> getMethodNameMappingValueSetter() {
        return methodNameMappingValueSetter;
    }

    public String getSwitchTransmitAndReceive() {
        return switchTransmitAndReceive;
    }

    public void setSwitchTransmitAndReceive(String switchTransmitAndReceive) {
        this.switchTransmitAndReceive = switchTransmitAndReceive;
    }

    public boolean isConfirmButtonIsSelected() {
        return confirmButtonIsSelected;
    }

    public void setConfirmButtonIsSelected(boolean confirmButtonIsSelected) {
        this.confirmButtonIsSelected = confirmButtonIsSelected;
    }

    @Override
    public String toString() {
        return "UIParameterCollector{" +
                "mode='" + mode + '\'' +
                ", rb=" + rb +
                ", rbUnit='" + rbUnit + '\'' +
                ", fc=" + fc +
                ", fcUnit='" + fcUnit + '\'' +
                ", transmitGain=" + transmitGain +
                ", transmitGainUnit='" + transmitGainUnit + '\'' +
                ", receiveGain=" + receiveGain +
                ", receiveGainUnit='" + receiveGainUnit + '\'' +
                ", frequenceOffset=" + frequenceOffset +
                ", frequenceOffsetUnit='" + frequenceOffsetUnit + '\'' +
                ", hop=" + hop +
                ", hopUnit='" + hopUnit + '\'' +
                ", switchTransmitAndReceive='" + switchTransmitAndReceive + '\'' +
                ", confirmButtonIsSelected=" + confirmButtonIsSelected +
                '}';
    }
}
