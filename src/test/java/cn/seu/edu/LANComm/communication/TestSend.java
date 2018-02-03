package cn.seu.edu.LANComm.communication;


import com.sun.org.apache.bcel.internal.generic.NEW;
import jpcap.JpcapCaptor;
import jpcap.JpcapSender;
import jpcap.packet.EthernetPacket;
import jpcap.packet.Packet;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2018/1/30.
 */
public class TestSend {
    public static void main(String[] args) throws Exception{
        InetAddress address = InetAddress.getLocalHost();
        NetworkInterface net = NetworkInterface.getByInetAddress(address);
        System.out.println("DisplayName " + net.getDisplayName());
        System.out.println("MAC " + transBytesToStr(net.getHardwareAddress()));
        System.out.println("Index " + net.getIndex());
        List<InterfaceAddress> interfaces = net.getInterfaceAddresses();
        for (InterfaceAddress address1 : interfaces) {
            System.out.println(address1);
        }
        System.out.println("Name " + net.getName());
        System.out.println("MTU " + net.getMTU());
        System.out.println(net.isVirtual());

    }

    public static String transBytesToStr(byte[] bytes){
        StringBuffer buffer = new StringBuffer();
        for(int i = 0; i < bytes.length; i++){
            if(i != 0)
                buffer.append("-");
            //bytes[i]&0xff将有符号byte数值转换为32位有符号整数，其中高24位为0，低8位为byte[i]
            int intMac = bytes[i]&0xff;
            //toHexString函数将整数类型转换为无符号16进制数字
            String str = Integer.toHexString(intMac);
            if(str.length() == 0){
                buffer.append("0");
            }
            buffer.append(str);
        }
        return buffer.toString().toUpperCase();
    }


    /*
    public void send() {
        // 枚举网卡设备
        jpcap.NetworkInterface[] devices = JpcapCaptor.getDeviceList();
        // 默认选择第一个网卡
        jpcap.NetworkInterface device = devices[0];
        // 数据报发送
        JpcapSender sender = null;
        // 待发送数据包
        Packet packet = new Packet();
        try {
            // 打开设备
            sender = JpcapSender.openDevice(device);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 设置以太网数据帧
        EthernetPacket ethernetPacket = new EthernetPacket();
        // 同步头、间隔标识、CRC由系统自动添加
        // 源MAC、目的MAC、数据段、协议类型/数据长度 由用户指定
        int type = Integer.decode("0X7799");
        ethernetPacket.frametype = (short) type;
        // 目标MAC地址
        byte[] destMAC = stomac(macAddress);
        byte[] srcMAC = null;
        try {
            srcMAC = stoMAC(getLocalMac(InetAddress.getLocalHost()));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        ethernetPacket.dst_mac = destMAC;
        ethernetPacket.src_mac = srcMAC;
        packet.datalink = ethernetPacket;

        // 设置字节的数据和长度
        byte[] data = new byte[54];
        String typeStr = "helloworld";
        byte[] types = typeStr.getBytes();
        for (int i = 0; i < types.length; i++) {
            data[i] = types[i];
        }
        packet.data = data;
        sender.sendPacket(packet);
    }
    */

    /**
     * 将一个使用标准表示方式的MAC转为byte数组
     * @param macString 一个使用标准表示方式的MAC地址字符串
     * @return
     */
    private byte[] stringToMAC(String macString) {
        // 判断MAC是否合法
        String macPattern = "([A-Fa-f0-9]{2}-){5}[A-Fa-f0-9]{2}";
        Pattern pattern = Pattern.compile(macPattern);
        Matcher matcher = pattern.matcher(macString);
        if (!matcher.matches()) {
            return null;
        }
        String[] macPartition = macString.split("\\-");
        byte[] mac = new byte[6];
        for (int i = 0; 1 < macPartition.length; i++) {
            mac[i] = (byte)Integer.parseInt(macPartition[i]);
        }
        return mac;
    }

    /**
     * 将MAC地址转为字符串，使用"-"分隔
     * @param mac
     * @return
     */
    private String macToString(byte[] mac) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < mac.length; i++) {
            String string = Integer.toHexString(mac[i] & 0XFF);
            if (i != mac.length - 1) {
                stringBuilder.append("-");
            }
        }
        return stringBuilder.toString();
    }
}
