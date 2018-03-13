package cn.seu.edu.LANComm.communication.dispatcher;

import cn.seu.edu.LANComm.communication.util.DataLinkParameterEnum;
import cn.seu.edu.LANComm.communication.util.FramingDecoder;
import jpcap.JpcapWriter;
import jpcap.PacketReceiver;
import jpcap.packet.EthernetPacket;
import jpcap.packet.Packet;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2018/3/13.
 */
public class TransmittedSymbolPacketDispatcher implements PacketReceiver{
    private static final TimeUnit OFFER_TIMEOUT_UNIT = TimeUnit.MILLISECONDS;
    private long offerTimeout;
    private BlockingQueue<Packet> data;
    private JpcapWriter writer;

    public TransmittedSymbolPacketDispatcher(long offerTimeout, BlockingQueue<Packet> data, JpcapWriter writer) {
        this.offerTimeout = offerTimeout;
        this.data = data;
        this.writer = writer;
    }

    @Override
    public void receivePacket(Packet packet) {
        EthernetPacket ethernetPacket = (EthernetPacket) packet.datalink;
        if (ethernetPacket.frametype == Short.parseShort(DataLinkParameterEnum.FRAME_TYPE.getDataType())) {
            FramingDecoder decoder = new FramingDecoder(packet.data);
            if (decoder.getParameterIDentifier().getDataType().equals(DataLinkParameterEnum.TRANSMITTED_SYMBOL_DATA.getDataType())) {
                System.out.println("收到发送的数据符号");
                if (writer != null) {
                    writer.writePacket(packet);
                }
                try {
                    boolean success = data.offer(packet, offerTimeout, OFFER_TIMEOUT_UNIT);
                    if (!success) {
                        System.out.println("发送的数据符号插入失败");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public long getOfferTimeout() {
        return offerTimeout;
    }

    public void setOfferTimeout(long offerTimeout) {
        this.offerTimeout = offerTimeout;
    }

    public BlockingQueue<Packet> getData() {
        return data;
    }

    public void setData(BlockingQueue<Packet> data) {
        this.data = data;
    }

    public JpcapWriter getWriter() {
        return writer;
    }

    public void setWriter(JpcapWriter writer) {
        this.writer = writer;
    }
}
