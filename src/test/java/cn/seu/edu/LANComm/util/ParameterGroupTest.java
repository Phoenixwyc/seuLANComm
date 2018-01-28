package cn.seu.edu.LANComm.util;

import com.sun.org.apache.bcel.internal.generic.NEW;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/1/26.
 */
public class ParameterGroupTest {

    @Test
    public void TestGroupByUnit() {
        Map<String, String> dqpskConfig = new HashMap<>();
        dqpskConfig.put("DQPSK-Rb", "[100-bps, 00-kbps, 1-Mbps, 10-Mbps]");
        dqpskConfig.put("DQPSK-Fc", "[1-kHz, 1-MHz, 2-MHz, 10-GHz]");
        dqpskConfig.put("DQPSK-Transmit-Gain", "[1-dBm, 2-dBm, 3-dBm, 10-dBm]");
        dqpskConfig.put("DQPSK-Receive-Gain", "[1-dBm, 2-dBm, 3-dBm, 10-dBm]");
        dqpskConfig.put("DQPSK-Frequence-Offset", "[1-Hz, 2-Hz, 3-kHz]");
        Map<String, Map<String, List<Double>>> actual = ParameterGroup.groupByUnit(dqpskConfig);
        Map<String, Map<String, List<Double>>> expected = new HashMap<>();

        List<Double> bpsList = new ArrayList<>();
        Map<String, List<Double>> innerMapRb = new HashMap<>();
        bpsList.add(new Double(100));
        innerMapRb.put("bps", bpsList);
        List<Double> kbpsList = new ArrayList<>();
        kbpsList.add(new Double(00));
        innerMapRb.put("kbps", kbpsList);
        List<Double> MbpsList = new ArrayList<>();
        MbpsList.add(new Double(1));
        MbpsList.add(new Double(10));
        innerMapRb.put("Mbps", MbpsList);
        expected.put("DQPSK-Rb", innerMapRb);

        List<Double> kHzList = new ArrayList<>();
        Map<String, List<Double>> innerMapFc = new HashMap<>();
        kHzList.add(new Double(1));
        innerMapFc.put("kHz", kHzList);
        List<Double> MHzList = new ArrayList<>();
        MHzList.add(new Double(1));
        MHzList.add(new Double(2));
        innerMapFc.put("MHz", MHzList);
        List<Double> GHzList = new ArrayList<>();
        GHzList.add(new Double(10));
        innerMapFc.put("GHz", GHzList);
        expected.put("DQPSK-Fc", innerMapFc);

        List<Double> dBmListTG = new ArrayList<>();
        Map<String, List<Double>> innerMapTG = new HashMap<>();
        dBmListTG.add(new Double(1));
        dBmListTG.add(new Double(2));
        dBmListTG.add(new Double(3));
        dBmListTG.add(new Double(10));
        innerMapTG.put("dBm", dBmListTG);
        expected.put("DQPSK-Transmit-Gain", innerMapTG);

        List<Double> dBmListRG = new ArrayList<>();
        Map<String, List<Double>> innerMapRG = new HashMap<>();
        dBmListRG.add(new Double(1));
        dBmListRG.add(new Double(2));
        dBmListRG.add(new Double(3));
        dBmListRG.add(new Double(10));
        innerMapRG.put("dBm", dBmListRG);
        expected.put("DQPSK-Receive-Gain", innerMapRG);

        List<Double> offsetHzList = new ArrayList<>();
        Map<String, List<Double>> innerMapOffset = new HashMap<>();
        offsetHzList.add(new Double(1));
        offsetHzList.add(new Double(2));
        innerMapOffset.put("Hz", offsetHzList);
        List<Double> offsetkHzList = new ArrayList<>();
        offsetkHzList.add(new Double(3));
        innerMapOffset.put("kHz", offsetkHzList);
        expected.put("DQPSK-Frequence-Offset", innerMapOffset);
        System.out.println(actual);
        System.out.println(expected);

        Assert.assertEquals("should be same", expected, actual);
    }

}
