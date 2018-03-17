package cn.seu.edu.LANComm.ui;

import cn.seu.edu.LANComm.communication.EthernetPacketSender;
import cn.seu.edu.LANComm.communication.dispatcher.ConstellationDataPacketDispatcher;
import cn.seu.edu.LANComm.communication.dispatcher.HoppingPatternDataPacketDispatcher;
import cn.seu.edu.LANComm.communication.dispatcher.IntermediateFrequencyDataPacketDispatcher;
import cn.seu.edu.LANComm.communication.receiver.DataReceiver;
import cn.seu.edu.LANComm.communication.util.*;
import cn.seu.edu.LANComm.util.CommunicationModeEnum;
import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;
import jpcap.PacketReceiver;
import jpcap.packet.EthernetPacket;
import jpcap.packet.Packet;

import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Administrator on 2018/1/26.
 * @author WYCPhoenix
 * @date 2018-1-29-15:46
 */
public class MainFrame {

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                FrameSet frameSet = new FrameSet("实时数据显示");
                frameSet.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frameSet.setBitErrorRateTextFieldString("0.000001");
                frameSet.setNormalStatus();
                frameSet.setVisible(true);
                System.out.println("主界面 " + frameSet.getUIParameterCollector());
            }
        });
    }
}

class FrameSet extends JFrame {
    /**
     * 跳频模式标识，结尾必须为-FH
     */
    private static final String FH = "-FH";

    UIParameterCollector collector = new UIParameterCollector();
    private JRadioButton normalStatusRadioButton;
    private JRadioButton abnormalStatusRadioButton;
    private JTextField bitErrorRateTextField;
    private JButton confirmButton;

    /**
     * 接收的星座数据
     */
    BlockingQueue<Packet> constellationData = new LinkedBlockingQueue<>();
    /**
     * 接收的中频信号
     */
    BlockingQueue<Packet> intermediateFrequenceData = new LinkedBlockingQueue<>();
    /**
     * 接收的中频信号，用于计算FFT
     */
    BlockingQueue<Packet> intermediateFrequenceDataFFT = new LinkedBlockingQueue<>();
    /**
     * 跳频图案
     */
    BlockingQueue<Packet> hoppingPatternData = new LinkedBlockingQueue<>();
    /**
     * 发送的符号
     */
    BlockingQueue<Packet> transmittedSymbol = new LinkedBlockingQueue<>();
    /**
     * 接收的符号
     */
    BlockingQueue<Packet> receivedSymbol = new LinkedBlockingQueue<>();

    private Map<String, Float> sampleRate = new HashMap<>();

    private boolean receiveStarted = false;

    private boolean sendStarted = false;

    /**
     * 设置最优尺寸
     */
    private static final int DEFAULT_WIDTH = 1000;
    private static final int DEFAULT_HEIGHT = 300;


    public FrameSet(String title) {
        // 主窗口大小
        super.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        // 主窗口居中
        super.setLocationRelativeTo(null);
        // 主窗口标题
        super.setTitle(title);
        // 主窗口大小可调
        super.setResizable(true);
        // ICON
        Image mainFrameIcon = new ImageIcon("MainIcon.jpg").getImage();
        super.setIconImage(mainFrameIcon);
        super.setLayout(new FlowLayout());

        // MAC地址交换
        MACExchangeDialog.showDialog(collector);

        // 参数设置面板
        JPanel parameterPanel = new JPanel();
        parameterPanel.setBackground(Color.WHITE);
        // 通信模式选择部分
        parameterPanel.add(CommunicationModeSelectorAndParameterSettingPart.createCommunicationModeSelectorAndParameterSettingPanel("LANComm.proerties", collector));

        // 通信状态部分
        Map<String, Object> statusPanel = CommunicationStatusPart.createStatusPanel();
        parameterPanel.add((JPanel)statusPanel.get(CommunicationStatusPart.getPanelKey()));
        normalStatusRadioButton = (JRadioButton) statusPanel.get(CommunicationStatusPart.getNormalStatusKey());
        abnormalStatusRadioButton = (JRadioButton) statusPanel.get(CommunicationStatusPart.getAbnormalStatusKey());
        bitErrorRateTextField = (JTextField) statusPanel.get(CommunicationStatusPart.getBitErrorRateTextKey());

        // 通信收发确认部分
        Map<String, Object> txRxSelectorPart = CommunicationTXRxSelectorPart.createCommunicationTXRxSelectorPanel(collector);
        parameterPanel.add((JPanel)txRxSelectorPart.get(CommunicationTXRxSelectorPart.getStatusPanel()));
        confirmButton = (JButton) txRxSelectorPart.get(CommunicationTXRxSelectorPart.getConfirmButton());

        // 绘图面板
        JPanel plotPanel = new JPanel(new GridLayout(1,4));
        plotPanel.setBackground(Color.WHITE);
        plotPanel.setSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        // 中频时域图
        JPanel intermediateFrequencyPartPanel = new JPanel();
        intermediateFrequencyPartPanel.setBackground(Color.WHITE);
        intermediateFrequencyPartPanel.setSize(new Dimension(DEFAULT_WIDTH / 4, DEFAULT_HEIGHT));
        PlotIntermediateFrequencyPart intermediateFrequencyPart = new PlotIntermediateFrequencyPart(intermediateFrequencyPartPanel);
        intermediateFrequencyPart.createIntermediateFrequencyChart(intermediateFrequenceData);
        plotPanel.add(intermediateFrequencyPartPanel);
        // 中频功率谱图
        JPanel intermediateFrequencyFFTPartPanel  = new JPanel();
        intermediateFrequencyFFTPartPanel.setBackground(Color.WHITE);
        intermediateFrequencyFFTPartPanel.setSize(new Dimension(DEFAULT_WIDTH / 4, DEFAULT_HEIGHT));
        PlotIntermediateFrequencyFFTPart intermediateFrequencyFFTPart = new PlotIntermediateFrequencyFFTPart(intermediateFrequencyFFTPartPanel);
        intermediateFrequencyFFTPart.createIntermediateFrequencyFFTChart(intermediateFrequenceDataFFT);
        plotPanel.add(intermediateFrequencyFFTPartPanel);
        // 星座图
        JPanel constellationDiagramPartPanel = new JPanel();
        constellationDiagramPartPanel.setBackground(Color.WHITE);
        constellationDiagramPartPanel.setSize(new Dimension(DEFAULT_WIDTH / 4, DEFAULT_HEIGHT));
        PlotConstellationDiagramPart constellationDiagramPart = new PlotConstellationDiagramPart(constellationDiagramPartPanel);
        constellationDiagramPart.createConstellationDiagramChart(constellationData);
        plotPanel.add(constellationDiagramPartPanel);
        // 跳频图案图
        JPanel hoppingPatterPartPanel = new JPanel();
        hoppingPatterPartPanel.setBackground(Color.WHITE);
        hoppingPatterPartPanel.setSize(new Dimension(DEFAULT_WIDTH / 4, DEFAULT_HEIGHT));
        PlotHoppingPatternPart hoppingPatternPart = new PlotHoppingPatternPart(hoppingPatterPartPanel);
        hoppingPatternPart.createHoppingPatternChart(hoppingPatternData);
        plotPanel.add(hoppingPatterPartPanel);
        // 组件汇总
        JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.setBackground(Color.WHITE);
        panel.add(parameterPanel);
        panel.add(plotPanel);
        super.add(panel);
        super.pack();

        /**
         * 这里会比较复杂，单独拿出来，控制线程的启停与数据的收发
         */
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (collector.getSwitchTransmitAndReceive().equals("发送")){
                    // TODO: 2018/2/25 这里执行数据发送，主要完成参数的发送、连接测试
                    // 发送参数配置数据
                    System.out.println("待发送的数据");
                    showArray(getParameterSelected());
                    EthernetPacketSender.sendEthernetPacket(new String[]{collector.getTxMAC(), collector.getRxMAC()},
                            collector.getLocalMAC(), DataLinkParameterEnum.PARAMETER_SETTING, getParameterSelected());
                    // 接收中频采样信号, 一次性使用，写一个匿名内部类算球~
                    // 有空再重构~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                    // 根据过滤器规则，MAC地址使用":"分隔而不是"-"
                    String srcFilter = "ether src " + collector.getRxMAC().replace("-", ":") + " or " + collector.getTxMAC().replace("-", ":");
                    String localMAC = collector.getLocalMAC();
//                    Map<String, Float> sampleRate = new HashMap<>();
                    try {
                        NetworkInterface deviceUsed = NetworkInterfaceUtil.getDesignateDeviceByMACString(localMAC);
                        if (deviceUsed != null) {
                            JpcapCaptor jpcapCaptor = JpcapCaptor.openDevice(deviceUsed, 2000, false, 2000);
                            jpcapCaptor.setFilter(srcFilter, true);
                            PacketReceiver sampleRateReceiver = new PacketReceiver() {
                                @Override
                                public void receivePacket(Packet packet) {
                                    EthernetPacket ethernetPacket = (EthernetPacket) packet.datalink;
                                    // 对帧的FrameType进行过滤
                                    if (Short.parseShort(DataLinkParameterEnum.FRAME_TYPE.getDataType()) == ethernetPacket.frametype) {
                                        FramingDecoder decoder = new FramingDecoder(packet.data);
                                        if (decoder.getParameterIDentifier().getDataType().equals(DataLinkParameterEnum.SAMPLE_RATE.getDataType())) {
                                            // 对源地址进行过滤
                                            if (MACStringConvertor.macToString(ethernetPacket.src_mac).equals(collector.getTxMAC())) {
                                                float[] sampleRateTx = decoder.getTransmittedData();
                                                sampleRate.put(collector.getTxMAC(), new Float(sampleRateTx[0]));
                                                System.out.println("收到Tx: " + collector.getTxMAC() + " 中频采样率: " + sampleRateTx[0]);
                                            } else if (MACStringConvertor.macToString(ethernetPacket.src_mac).equals(collector.getRxMAC())) {
                                                float[] sampleRateRx = decoder.getTransmittedData();
                                                sampleRate.put(collector.getRxMAC(), new Float(sampleRateRx[0]));
                                                System.out.println("收到Rx: " + collector.getRxMAC() + " 中频采样率: " + sampleRateRx[0]);
                                            }
                                            if (sampleRate.keySet().size() == 2) {
                                                // 设置中频采样率
                                                intermediateFrequencyFFTPart.setSampleRate(sampleRate.get(collector.getRxMAC()));
                                                jpcapCaptor.breakLoop();
                                                System.out.println("emmm， 中频采样接收完成，中频接收资源关闭 " +
                                                "发送端 " + collector.getTxMAC() + "中频采样率 " + sampleRate.get(collector.getTxMAC()) +
                                                "接收端 " + collector.getRxMAC() + " 中频采样率 " + sampleRate.get(collector.getRxMAC()) +
                                                "中频功率谱采样率 " + intermediateFrequencyFFTPart.getSampleRate());
                                            }
                                        }
                                    }
                                }
                            };
                            jpcapCaptor.loopPacket(-1, sampleRateReceiver);
                            if (jpcapCaptor != null) {
                                jpcapCaptor.close();
                            }
                        }
                    } catch (IOException e1) {
                        System.out.println("fuck 接收中频采样是怎么打不开端口！！");
                    }
                } else if (collector.getSwitchTransmitAndReceive().equals("接收")){
                    // TODO: 2018/2/25 这里执行数据接收
                    if (!receiveStarted) {
                        // 发送启动指示
                        EthernetPacketSender.sendEthernetPacket(new String[]{collector.getRxMAC(), collector.getTxMAC()}, collector.getLocalMAC(),
                                DataLinkParameterEnum.COMMUNICATION_START, new float[]{0F});

                        // 各种数据的接收线程
                        // 中频信号接收
                        Thread intermediateFrequenceDataPlotter = new Thread(intermediateFrequencyPart);
                        intermediateFrequenceDataPlotter.start();
                        IntermediateFrequencyDataPacketDispatcher intermediateFrequencyDataPacketDispatcher = new IntermediateFrequencyDataPacketDispatcher(4000,
                                                                                            intermediateFrequenceData, intermediateFrequenceDataFFT, null);
                        String TxFilter = "ether src " + collector.getTxMAC().replace("-", ":");
                        DataReceiver IntermediateFrequencyDataReceiver = new DataReceiver(collector.getLocalMAC(),
                                                                                            TxFilter, intermediateFrequencyDataPacketDispatcher);
                        new Thread(IntermediateFrequencyDataReceiver).start();

                        // 中频信号FFT接收
                        Thread intermediateFrequencyFFTPlotter = new Thread(intermediateFrequencyFFTPart);
                        intermediateFrequencyFFTPlotter.start();
                        
                        // 接收端星座数据接收
                        Thread constellationDiagramPlotter = new Thread(constellationDiagramPart);
                        constellationDiagramPlotter.start();
                        ConstellationDataPacketDispatcher constellationDataPacketDispatcher = new ConstellationDataPacketDispatcher(4000, constellationData, null);
                        DataReceiver constellationDataReceiver = new DataReceiver(collector.getLocalMAC(),
                                TxFilter, constellationDataPacketDispatcher);
                        new Thread(constellationDataReceiver).start();
                        
                        // 接收端跳频图案
                        Thread hoppingPatternPlotter = new Thread(hoppingPatternPart);
                        hoppingPatternPlotter.start();

                        HoppingPatternDataPacketDispatcher hoppingPatternDataPacketDispatcher = new HoppingPatternDataPacketDispatcher(4000, hoppingPatternData, null);
                        DataReceiver hoppingPatternDataReceiver = new DataReceiver(collector.getLocalMAC(), TxFilter, hoppingPatternDataPacketDispatcher);
                        new Thread(hoppingPatternDataReceiver).start();

                        // TODO: 2018/3/17 在这里开启误码率计算线程 
                        
                    }
                }
            }
        });
    }

    /**
     * 通信参数收集器
     * note：仅用于调用getter方法，禁止使用setter方法
     * @return 通信参数设置汇总
     */
    public UIParameterCollector getUIParameterCollector() {
        return collector;
    }

    /**
     *  系统正常状态
     */
    public void setNormalStatus() {
        normalStatusRadioButton.setForeground(Color.BLUE);
        normalStatusRadioButton.setBackground(Color.GREEN);
        normalStatusRadioButton.setSelected(true);
        // 重置故障状态
        abnormalStatusRadioButton.setForeground(Color.BLACK);
        abnormalStatusRadioButton.setBackground(Color.WHITE);
    }

    /**
     * 系统关故障状态
     */
    public void setAbnormalStatus() {
        abnormalStatusRadioButton.setSelected(true);
        abnormalStatusRadioButton.setBackground(Color.RED);
        abnormalStatusRadioButton.setForeground(Color.ORANGE);
        // 重置正常状态
        normalStatusRadioButton.setBackground(Color.WHITE);
        normalStatusRadioButton.setForeground(Color.BLACK);
    }

    /**
     * 设置误码率数值
     * @param bitErrorRate 误码率，字符串
     */
    public void setBitErrorRateTextFieldString(String bitErrorRate) {
        if (bitErrorRate == null) {
            bitErrorRateTextField.setText("-1");
        }
        bitErrorRateTextField.setText(bitErrorRate);
    }

    /**
     * 对UIParameterCollector的参数进行汇总
     * key为参数名。value为参数值
     * 注意这里没有对UIParameterCollector参数的合法性进行校验
     * 同时，插入顺序就是以后的发送顺序
     * @return
     */
    public float[] getParameterSelected() {
        List<Float> temp = new ArrayList<>();
        // 通信模式
        String commMode = collector.getMode();
        EnumSet<CommunicationModeEnum> modeEnums = EnumSet.allOf(CommunicationModeEnum.class);
        Iterator<CommunicationModeEnum> iterator = modeEnums.iterator();
        while (iterator.hasNext()) {
            CommunicationModeEnum modeEnum = iterator.next();
            if (modeEnum.getCommunicationMode().equals(commMode)) {
                temp.add(new Float(modeEnum.getModeCode()));
                break;
            }
        }
        // 码元速率
        Float Rb = collector.getRb();
        String unitRb = collector.getRbUnit();
        temp.add(new Float(Rb * getValueByUnit(unitRb)));
        // 载波速率
        Float Fc = collector.getFc();
        String unitFc = collector.getFcUnit();
        temp.add(new Float(Fc * getValueByUnit(unitFc)));
        // 频偏
        Float frequenceOffset = collector.getFrequenceOffset();
        String unitOffset = collector.getFrequenceOffsetUnit();
        temp.add(new Float(frequenceOffset * getValueByUnit(unitOffset)));
        // 发送增益
        Float transmitGain = collector.getTransmitGain();
        String unitTransmitGain = collector.getTransmitGainUnit();
        temp.add(new Float(transmitGain * getValueByUnit(unitTransmitGain)));
        // 接收增益
        Float receiveGain = collector.getReceiveGain();
        String unitReceiveGain = collector.getReceiveGainUnit();
        temp.add(new Float(receiveGain * getValueByUnit(unitReceiveGain)));
        // 跳变速率
        if (commMode.endsWith(FH)) {
            Float hops = collector.getHop();
            String unitHop = collector.getHopUnit();
            temp.add(new Float(hops * getValueByUnit(unitHop)));
        }

        //转为待发送数据
        float[] res = new float[temp.size()];
        for (int i = 0; i < temp.size(); i++) {
            res[i] = temp.get(i).floatValue();
        }
        return res;
    }

    private float getValueByUnit(String unit) {
        float res = -1F;
        EnumSet<ParameterUnitEnum> enums = EnumSet.allOf(ParameterUnitEnum.class);
        Iterator<ParameterUnitEnum> iterator = enums.iterator();
        while (iterator.hasNext()) {
            ParameterUnitEnum unitEnum = iterator.next();
            if (unitEnum.getUnit().equals(unit)) {
                res = unitEnum.getValue();
                break;
            }
        }
        return res;
    }

    private void showArray(float[] data) {
        for (float item : data) {
            System.out.print(item + " ");
        }
        System.out.println();
    }


}
