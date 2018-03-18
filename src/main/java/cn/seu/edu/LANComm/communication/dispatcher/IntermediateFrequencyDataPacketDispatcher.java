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
 * 中频采样信号分发器
 * Created by Administrator on 2018/3/13.
 */
public class IntermediateFrequencyDataPacketDispatcher implements PacketReceiver{
    private static TimeUnit OFFER_TIMEOUT_UNIT = TimeUnit.MILLISECONDS;
    private long offerTimeout;
    private BlockingQueue<Packet> data;
    private BlockingQueue<Packet> dataForFFT;
    private JpcapWriter writer;
    private JpcapCaptor captor;
    private volatile boolean isRunning = true;

    public IntermediateFrequencyDataPacketDispatcher(long offerTimeout, BlockingQueue<Packet> data,
                                                     BlockingQueue<Packet> dataForFFT, JpcapWriter writer) {
        this.offerTimeout = offerTimeout;
        this.data = data;
        this.dataForFFT = dataForFFT;
        this.writer =writer;
    }

    @Override
    public void receivePacket(Packet packet) {
        EthernetPacket ethernetPacket = (EthernetPacket) packet.datalink;
        if (Short.parseShort(DataLinkParameterEnum.FRAME_TYPE.getDataType()) == ethernetPacket.frametype) {
            FramingDecoder decoder = new FramingDecoder(packet.data);
            if (decoder.getParameterIDentifier().getDataType().equals(DataLinkParameterEnum.INTERMEDIATE_DATA.getDataType())) {
                if (writer != null) {
                    writer.writePacket(packet);
                }
                try {
                    boolean success = data.offer(packet, offerTimeout, OFFER_TIMEOUT_UNIT);
                    boolean successFFT = dataForFFT.offer(packet, offerTimeout, OFFER_TIMEOUT_UNIT);
                    System.out.println("插入时FFT数据缓冲区大小 " + dataForFFT.size());
                    System.out.println("插入时中频时域信号缓冲区大小 " + data.size());
                    if (!success) {
                        System.out.println("中频信号数据插入失败");
                    }
                    if (!successFFT) {
                        System.out.println("中频信号FFT数据插入失败");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        if (!isRunning) {
            captor.breakLoop();
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
