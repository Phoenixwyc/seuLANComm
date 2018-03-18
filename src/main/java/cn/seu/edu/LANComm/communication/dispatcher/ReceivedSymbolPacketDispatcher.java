package cn.seu.edu.LANComm.communication.dispatcher;

import cn.seu.edu.LANComm.communication.util.DataLinkParameterEnum;
import cn.seu.edu.LANComm.communication.util.FramingDecoder;
import jpcap.JpcapCaptor;
import jpcap.JpcapWriter;
import jpcap.PacketReceiver;
import jpcap.packet.EthernetPacket;
import jpcap.packet.Packet;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2018/3/13.
 */
public class ReceivedSymbolPacketDispatcher implements PacketReceiver{
    private static final TimeUnit OFFER_TIMEOUT_UNIT = TimeUnit.MILLISECONDS;
    private long offerTimeout;
    private BlockingQueue<Packet> data;
    private JpcapWriter writer;
    private JpcapCaptor captor;
    private volatile boolean isRunning = true;

    public ReceivedSymbolPacketDispatcher(long offerTimeout, BlockingQueue<Packet> data) {
        this.offerTimeout = offerTimeout;
        this.data = data;
    }

    @Override
    public void receivePacket(Packet packet) {
        EthernetPacket ethernetPacket = (EthernetPacket) packet.datalink;
        if (ethernetPacket.frametype == Short.parseShort(DataLinkParameterEnum.FRAME_TYPE.getDataType())) {
            FramingDecoder decoder = new FramingDecoder(packet.data);
            if (decoder.getParameterIDentifier().getDataType().equals(DataLinkParameterEnum.RECEIVED_SYMBOL_DATA.getDataType())) {
                writer = getWriter();
                if (writer != null) {
                    writer.writePacket(packet);
                }
                try {
                    boolean success = data.offer(packet, offerTimeout, OFFER_TIMEOUT_UNIT);
                    System.out.println("插入时接收符号数据缓冲区大小为 " + data.size());
                    if (!success) {
                        System.out.println("接收符号数据包插入失败");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        if (!isRunning) {
            captor = getCaptor();
            if (captor != null) {
                captor.breakLoop();
                System.out.println("接收符号接收线程停止");
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

    public void stop() {
        this.isRunning = false;
    }

    public JpcapCaptor getCaptor() {
        return captor;
    }

    public void setCaptor(JpcapCaptor captor) {
        this.captor = captor;
    }
}
