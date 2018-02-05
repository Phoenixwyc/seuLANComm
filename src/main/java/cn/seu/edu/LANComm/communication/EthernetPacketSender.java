package cn.seu.edu.LANComm.communication;

import cn.seu.edu.LANComm.communication.util.DataLinkParameterEnum;
import cn.seu.edu.LANComm.communication.util.FramingEncoder;
import cn.seu.edu.LANComm.communication.util.MACStringConvertor;
import jpcap.JpcapCaptor;
import jpcap.JpcapSender;
import jpcap.NetworkInterface;
import jpcap.packet.EthernetPacket;
import jpcap.packet.Packet;

import java.io.IOException;

/**
 * Created by Administrator on 2018/1/29.
 * @author WYCPhoenix
 * @date 2018-2-5-19:54
 */
public class EthernetPacketSender {
    /**
     * 发送数据最小的个数
     */
    private static final int MINIUM_BYTES = 46;
    private static final int MAXIUM_BYTES = 1400;
    /**
     * 以太帧消息类型
     */
    private static final short FRAME_TYPE = 8511;
    /**
     * 发送MAC帧
     * @param destMAC 目的MAC地址 6 byte
     * @param srcMAC 源MAC地址 6 byte
     * @param frameType 协议类型或长度 4 byte；
     * @param data 数据段， 大于 46 byte 且小于1400 byte
     *             嗯，虽然试过更小的字节数也是可以的，但还是按照CSMA/CD协议来吧
     * @param sender jpcap发送实例
     */
    @SuppressWarnings("all")
    private static void sendData(short frameType, byte[]destMAC, byte[] srcMAC, byte[] data, JpcapSender sender) {
        if (!checkData(frameType, destMAC, srcMAC, data, sender)) {
            System.out.println("参数不合理");
        }
        // 组装MAC帧
        EthernetPacket ethernetPacket = new EthernetPacket();
        // 同步头、间隔标识、CRC由系统添加，其余用于指定
        ethernetPacket.frametype = frameType;
        ethernetPacket.src_mac = srcMAC;
        ethernetPacket.dst_mac = destMAC;
        Packet packet = new Packet();
        packet.datalink = ethernetPacket;
        packet.data = data;
        sender.sendPacket(packet);
    }

    /**
     * 校验发送帧的数据长度
     * @param destMAC
     * @param srcMAC
     * @param frameType
     * @param data
     * @param sender
     * @return
     */
    @SuppressWarnings("all")
    private static boolean checkData(short frameType, byte[]destMAC, byte[] srcMAC, byte[] data, JpcapSender sender) {
        if (destMAC.length != 6) {
            return false;
        }
        if (srcMAC.length != 6) {
            return false;
        }
        if (data.length <MINIUM_BYTES || data.length > MAXIUM_BYTES) {
            return false;
        }
        if (sender == null) {
            return false;
        }
        return true;
    }

    /**
     * 向所有指定的MAC地址发送数据包
     * @param receiverMACs 目的MAC
     * @param localMAC 本地MAC
     * @param dataLinkParameterEnum 消息类型
     * @param data 待发送的消息
     */
    public static void sendEthernetPacket(String[] receiverMACs, String localMAC, DataLinkParameterEnum dataLinkParameterEnum, float[] data) {
        // 获取本地设备，其MAC地址 = localMAC
        NetworkInterface[] devices = JpcapCaptor.getDeviceList();
        NetworkInterface deviceUsed =  null;
        JpcapSender sender = null;
        for (NetworkInterface device : devices) {
            String macString = MACStringConvertor.macToString(device.mac_address);
            if (macString.equals(localMAC)) {
                deviceUsed = device;
                break;
            }
        }
        try {
            sender = JpcapSender.openDevice(deviceUsed);
        } catch (IOException e) {
            // TODO: 2018/2/5 增加端口打开异常的处理
        } finally {
            // TODO: 2018/2/5 资源回收
        }
        // float数组转为字节数组
        byte[] dataBytes = FramingEncoder.getByteToSend(dataLinkParameterEnum, data);
        // 检查dataBytes长度，长度不足 MINIUM_BYTES 的填充0
        byte[] bytesToSend = null;
        if (dataBytes.length < MINIUM_BYTES) {
            bytesToSend = new byte[MINIUM_BYTES];
            for (int i = 0; i < MINIUM_BYTES; i++) {
                if (i < dataBytes.length) {
                    bytesToSend[i] = dataBytes[i];
                } else {
                    bytesToSend[i] = 0;
                }
            }
        } else {
            bytesToSend = dataBytes;
        }
        byte[] srcMAC = MACStringConvertor.stringToMAC(localMAC);
        // 向所有指定MAC发送数据帧
        for (String MAC : receiverMACs) {
            byte[] desMAC = MACStringConvertor.stringToMAC(MAC);
            sendData(FRAME_TYPE, desMAC, srcMAC, bytesToSend, sender);
        }
    }

    public static void main(String[] args) {
        String[] desMAC = {"00-0C-29-62-C7-C8",
                            "00-0C-29-1E-AA-3F"};
        String srcMAC = "00-23-24-B0-96-A7";
        float[] data = {3F,3.14F,3F,3.14F};
        System.out.println("开始发送");
        DataLinkParameterEnum dataLinkParameterEnum = DataLinkParameterEnum.TEST;
        sendEthernetPacket(desMAC, srcMAC, dataLinkParameterEnum, data);
        System.out.println("发送结束");
    }
}
