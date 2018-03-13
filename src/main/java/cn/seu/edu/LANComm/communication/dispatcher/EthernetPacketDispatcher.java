package cn.seu.edu.LANComm.communication.dispatcher;

import cn.seu.edu.LANComm.communication.util.DataLinkParameterEnum;
import cn.seu.edu.LANComm.communication.util.FramingDecoder;
import jpcap.PacketReceiver;
import jpcap.packet.EthernetPacket;
import jpcap.packet.Packet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 实现以太网数据的分发
 * Created by Administrator on 2018/1/29.
 */
public class EthernetPacketDispatcher implements PacketReceiver{
    private static final TimeUnit TIME_UNIT = TimeUnit.MILLISECONDS;
    /**
     * 帧插入超时
     */
    private long offerTimeout;
    /**
     * 接收的星座数据
     */
    BlockingQueue<Packet> constellationData;
    /**
     * 接收的中频信号
     */
    BlockingQueue<Packet> intermediateFrequenceData;
    /**
     * 跳频图案
     */
    BlockingQueue<Packet> hoppingPatternData;
    /**
     * 发送的符号
     */
    BlockingQueue<Packet> transmittedSymbol;
    /**
     * 接收的符号
     */
    BlockingQueue<Packet> receivedSymbol;


    public EthernetPacketDispatcher(long offerTimeout, BlockingQueue<Packet> constellationData,
                                    BlockingQueue<Packet> intermediateFrequenceData, BlockingQueue<Packet> hoppingPatternData,
                                    BlockingQueue<Packet> transmittedSymbol, BlockingQueue<Packet> receivedSymbol) {
        this.offerTimeout = offerTimeout;
        this.constellationData = constellationData;
        this.intermediateFrequenceData = intermediateFrequenceData;
        this.hoppingPatternData = hoppingPatternData;
        this.transmittedSymbol = transmittedSymbol;
        this.receivedSymbol = receivedSymbol;
    }

    public EthernetPacketDispatcher(){}

    @Override
    public void receivePacket(Packet packet) {
        // 数据分发
        EthernetPacket ethernetPacket = (EthernetPacket) packet.datalink;
        short frameType = ethernetPacket.frametype;
        byte[] receivedBytes = packet.data;

        // 过滤帧
        if (Short.parseShort(DataLinkParameterEnum.FRAME_TYPE.getDataType()) == frameType) {
            FramingDecoder decoder = new FramingDecoder(receivedBytes);
            String parameterIDentifier = decoder.getParameterIDentifier().getDataType();
            try {
                boolean success = false;
                // 星座数据
                if (DataLinkParameterEnum.CONSTELLATION_DATA.getDataType().equals(parameterIDentifier)) {
                    success = constellationData.offer(packet, offerTimeout, TIME_UNIT);
                    System.out.println("收到星座数据包" + constellationData.size());
                    // 中频信号
                } else if (DataLinkParameterEnum.INTERMEDIATE_DATA.getDataType().equals(parameterIDentifier)) {
                    success = intermediateFrequenceData.offer(packet, offerTimeout, TIME_UNIT);
                    System.out.println("收到中频信号数据包" + intermediateFrequenceData.size());
                    // 跳频图案
                } else if (DataLinkParameterEnum.HOPPING_PATTERN_DATA.getDataType().equals(parameterIDentifier)) {
                    success = hoppingPatternData.offer(packet, offerTimeout, TIME_UNIT);
                    System.out.println("收到跳频图案数据包" + hoppingPatternData.size());
                    // 发送的符号
                } else if (DataLinkParameterEnum.TRANSMITTED_SYMBOL_DATA.getDataType().equals(parameterIDentifier)) {
                    success = transmittedSymbol.offer(packet, offerTimeout, TIME_UNIT);
                    System.out.println("收到发送符号数据包" + transmittedSymbol.size());
                    // 接收的符号
                } else if (DataLinkParameterEnum.RECEIVED_SYMBOL_DATA.getDataType().equals(parameterIDentifier)) {
                    success = receivedSymbol.offer(packet, offerTimeout, TIME_UNIT);
                    System.out.println("收到接收符号数据包" + receivedSymbol.size());
                }

                if (!success) {
                    System.out.println("插入超时，没有数据没有消费");
                }
            } catch (InterruptedException e) {
                System.out.println("阻塞线程中断，这里可以考虑关线程");
            } finally {
                System.out.println("关闭资源");
            }
        }
    }

    public BlockingQueue<Packet> getConstellationData() {
        return constellationData;
    }

    public BlockingQueue<Packet> getIntermediateFrequenceData() {
        return intermediateFrequenceData;
    }

    public BlockingQueue<Packet> getHoppingPatternData() {
        return hoppingPatternData;
    }

    public BlockingQueue<Packet> getTransmittedSymbol() {
        return transmittedSymbol;
    }

    public BlockingQueue<Packet> getReceivedSymbol() {
        return receivedSymbol;
    }

    public long getOfferTimeout() {
        return offerTimeout;
    }

    public void setOfferTimeout(long offerTimeout) {
        this.offerTimeout = offerTimeout;
    }
}

