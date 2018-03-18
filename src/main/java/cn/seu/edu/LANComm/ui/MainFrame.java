package cn.seu.edu.LANComm.ui;

import cn.seu.edu.LANComm.communication.EthernetPacketSender;
import cn.seu.edu.LANComm.communication.dispatcher.*;
import cn.seu.edu.LANComm.communication.receiver.DataReceiver;
import cn.seu.edu.LANComm.communication.util.DataLinkParameterEnum;
import cn.seu.edu.LANComm.communication.util.FramingDecoder;
import cn.seu.edu.LANComm.communication.util.MACStringConvertor;
import cn.seu.edu.LANComm.communication.util.NetworkInterfaceUtil;
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
import java.util.HashMap;
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
                frameSet.setVisible(true);
                System.out.println("主界面 " + frameSet.getUIParameterCollector());
            }
        });
    }
}

class FrameSet extends JFrame {

    UIParameterCollector collector = new UIParameterCollector();
    private JButton confirmButton;

    /**
     * 四个控制绘图的线程
     */
    private PlotIntermediateFrequencyPart plotIntermediateFrequencyPart;
    private PlotIntermediateFrequencyFFTPart plotIntermediateFrequencyFFTPart;
    private PlotConstellationDiagramPart plotConstellationDiagramPart;
    private PlotHoppingPatternPart plotHoppingPatternPart;

    /**
     *
     */
    CommunicationStatusPart communicationStatusPart;
    /**
     * 5个控制数据接收的线程
     */
    private ConstellationDataPacketDispatcher constellationDataPacketDispatcher;
    private HoppingPatternDataPacketDispatcher hoppingPatternDataPacketDispatcher;
    private IntermediateFrequencyDataPacketDispatcher intermediateFrequencyDataPacketDispatcher;
    private ReceivedSymbolPacketDispatcher receivedSymbolPacketDispatcher;
    private TransmittedSymbolPacketDispatcher transmittedSymbolPacketDispatcher;

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

    private volatile boolean sendStarted = false;

    /**
     * 设置最优尺寸
     */
    private static final int DEFAULT_WIDTH = 1000;
    private static final int DEFAULT_HEIGHT = 300;
    /**
     * 下位机停止指令
     */
    private static final String STOP_COMMAND = "停止";
    /**
     * 配置参数据发送
     */
    private static final String SEND_DATA = "发送";
    /**
     * 接收数据
     */
    private static final String RECEIVE_DATA = "接收";
    /**
     * 工作状态确认
     */
    private static final String CONFIRM = "确认";


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
        plotIntermediateFrequencyPart = new PlotIntermediateFrequencyPart(intermediateFrequencyPartPanel);
        plotIntermediateFrequencyPart.createIntermediateFrequencyChart(intermediateFrequenceData);
        plotPanel.add(intermediateFrequencyPartPanel);
        // 中频功率谱图
        JPanel intermediateFrequencyFFTPartPanel  = new JPanel();
        intermediateFrequencyFFTPartPanel.setBackground(Color.WHITE);
        intermediateFrequencyFFTPartPanel.setSize(new Dimension(DEFAULT_WIDTH / 4, DEFAULT_HEIGHT));
         plotIntermediateFrequencyFFTPart = new PlotIntermediateFrequencyFFTPart(intermediateFrequencyFFTPartPanel);
        plotIntermediateFrequencyFFTPart.createIntermediateFrequencyFFTChart(intermediateFrequenceDataFFT);
        plotPanel.add(intermediateFrequencyFFTPartPanel);
        // 星座图
        JPanel constellationDiagramPartPanel = new JPanel();
        constellationDiagramPartPanel.setBackground(Color.WHITE);
        constellationDiagramPartPanel.setSize(new Dimension(DEFAULT_WIDTH / 4, DEFAULT_HEIGHT));
        plotConstellationDiagramPart = new PlotConstellationDiagramPart(constellationDiagramPartPanel);
        plotConstellationDiagramPart.createConstellationDiagramChart(constellationData);
        plotPanel.add(constellationDiagramPartPanel);
        // 跳频图案图
        JPanel hoppingPatterPartPanel = new JPanel();
        hoppingPatterPartPanel.setBackground(Color.WHITE);
        hoppingPatterPartPanel.setSize(new Dimension(DEFAULT_WIDTH / 4, DEFAULT_HEIGHT));
        plotHoppingPatternPart = new PlotHoppingPatternPart(hoppingPatterPartPanel);
        plotHoppingPatternPart.createHoppingPatternChart(hoppingPatternData);
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
                if (collector.getSwitchTransmitAndReceive().equals(SEND_DATA)){
                    // TODO: 2018/2/25 这里执行数据发送，主要完成参数的发送、连接测试
                    // 发送参数配置数据
                    System.out.println("待发送的数据");
                    showArray(collector.getParameterSelected());
                    EthernetPacketSender.sendEthernetPacket(new String[]{collector.getTxMAC(), collector.getRxMAC()},
                            collector.getLocalMAC(), DataLinkParameterEnum.PARAMETER_SETTING, collector.getParameterSelected());
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
                                                plotIntermediateFrequencyFFTPart.setSampleRate(sampleRate.get(collector.getRxMAC()));
                                                jpcapCaptor.breakLoop();
                                                System.out.println("emmm， 中频采样接收完成，中频接收资源关闭 " +
                                                "发送端 " + collector.getTxMAC() + "中频采样率 " + sampleRate.get(collector.getTxMAC()) +
                                                "接收端 " + collector.getRxMAC() + " 中频采样率 " + sampleRate.get(collector.getRxMAC()) +
                                                "中频功率谱采样率 " + plotIntermediateFrequencyFFTPart.getSampleRate());
                                                sendStarted = true;

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
                } else if (collector.getSwitchTransmitAndReceive().equals(RECEIVE_DATA)){
                    // TODO: 2018/2/25 这里执行数据接收
                    if (sendStarted) {
                        // 发送启动指示
                        EthernetPacketSender.sendEthernetPacket(new String[]{collector.getRxMAC(), collector.getTxMAC()}, collector.getLocalMAC(),
                                DataLinkParameterEnum.COMMUNICATION_START, new float[]{0F});

                        // 各种数据的接收线程
                        // 中频信号接收
                        Thread intermediateFrequenceDataPlotter = new Thread(plotIntermediateFrequencyPart);
                        intermediateFrequenceDataPlotter.start();
                        intermediateFrequencyDataPacketDispatcher = new IntermediateFrequencyDataPacketDispatcher(4000,
                                                                                            intermediateFrequenceData, intermediateFrequenceDataFFT, null);
                        String TxFilter = "ether src " + collector.getTxMAC().replace("-", ":");
                        DataReceiver intermediateFrequencyDataReceiver = new DataReceiver(collector.getLocalMAC(),
                                                                                            TxFilter, intermediateFrequencyDataPacketDispatcher);
                        intermediateFrequencyDataPacketDispatcher.setCaptor(intermediateFrequencyDataReceiver.getCaptor());
                        new Thread(intermediateFrequencyDataReceiver).start();

                        // 中频信号FFT接收
                        Thread intermediateFrequencyFFTPlotter = new Thread(plotIntermediateFrequencyFFTPart);
                        intermediateFrequencyFFTPlotter.start();
                        
                        // 接收端星座数据接收
                        Thread constellationDiagramPlotter = new Thread(plotConstellationDiagramPart);
                        constellationDiagramPlotter.start();
                        constellationDataPacketDispatcher = new ConstellationDataPacketDispatcher(4000, constellationData, null);
                        DataReceiver constellationDataReceiver = new DataReceiver(collector.getLocalMAC(),
                                TxFilter, constellationDataPacketDispatcher);
                        constellationDataPacketDispatcher.setCaptor(constellationDataReceiver.getCaptor());
                        new Thread(constellationDataReceiver).start();
                        
                        // 接收端跳频图案
                        Thread hoppingPatternPlotter = new Thread(plotHoppingPatternPart);
                        hoppingPatternPlotter.start();

                        hoppingPatternDataPacketDispatcher = new HoppingPatternDataPacketDispatcher(4000, hoppingPatternData, null);
                        DataReceiver hoppingPatternDataReceiver = new DataReceiver(collector.getLocalMAC(), TxFilter, hoppingPatternDataPacketDispatcher);
                        hoppingPatternDataPacketDispatcher.setCaptor(hoppingPatternDataReceiver.getCaptor());
                        new Thread(hoppingPatternDataReceiver).start();

                        // TODO: 2018/3/17 在这里开启误码率计算线程
                        transmittedSymbolPacketDispatcher = new TransmittedSymbolPacketDispatcher(4000, transmittedSymbol, null);
                        DataReceiver transmittedDataReceiver = new DataReceiver(collector.getLocalMAC(), TxFilter, transmittedSymbolPacketDispatcher);
                        transmittedSymbolPacketDispatcher.setCaptor(transmittedDataReceiver.getCaptor());
                        receivedSymbolPacketDispatcher = new ReceivedSymbolPacketDispatcher(4000, receivedSymbol, null);
                        DataReceiver receivedDataReceiver = new DataReceiver(collector.getLocalMAC(), TxFilter, receivedSymbolPacketDispatcher);
                        receivedSymbolPacketDispatcher.setCaptor(receivedDataReceiver.getCaptor());
                        new Thread(transmittedDataReceiver).start();
                        new Thread(receivedDataReceiver).start();
                        Thread symbolErroRate = new Thread(communicationStatusPart = new CommunicationStatusPart(transmittedSymbol, receivedSymbol));
                        symbolErroRate.start();

                        confirmButton.setActionCommand(STOP_COMMAND);
                        confirmButton.setText(STOP_COMMAND);
                        System.out.println("确认按钮的指令：" + confirmButton.getActionCommand());
                        
                    } else {
                        JOptionPane.showMessageDialog(null, "先发送配置参数，在启动数据接收", "错误", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });


        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (confirmButton.getActionCommand().equals(STOP_COMMAND)) {
                    sendStarted = false;
                    confirmButton.setActionCommand(SEND_DATA);
                    confirmButton.setText(CONFIRM);
                    /**
                     * 停止绘图线程
                     */
                    plotIntermediateFrequencyPart.stop();
                    plotIntermediateFrequencyFFTPart.stop();
                    plotConstellationDiagramPart.stop();
                    plotHoppingPatternPart.stop();
                    /**
                     * 停止误码率计算线程
                     */
                    communicationStatusPart.stop();
                    /**
                     * 停止数据接收线程
                     */
                    constellationDataPacketDispatcher.stop();
                    hoppingPatternDataPacketDispatcher.stop();
                    intermediateFrequencyDataPacketDispatcher.stop();
                    receivedSymbolPacketDispatcher.stop();
                    transmittedSymbolPacketDispatcher.stop();
                    /**
                     * 发送停止指令，下位机回到接收参数状态
                     */
                    EthernetPacketSender.sendEthernetPacket(new String[]{collector.getTxMAC(), collector.getRxMAC()},
                            collector.getLocalMAC(), DataLinkParameterEnum.COMMUNICATION_STOP, new float[]{0.0F});
                    JOptionPane.showMessageDialog(null, "停止接收，请重新设置参数开始新一轮数据接收");
                    System.out.println("确认按钮的指令：" + confirmButton.getActionCommand());
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


    private void showArray(float[] data) {
        for (float item : data) {
            System.out.print(item + " ");
        }
        System.out.println();
    }

}
