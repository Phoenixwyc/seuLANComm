package cn.seu.edu.LANComm.ui;


import jpcap.packet.Packet;
import org.jfree.chart.ChartPanel;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.BlockingQueue;

/**
 * 绘制中频信号时域图
 * Created by Administrator on 2018/1/27.
 * @author WYCPhoenix
 * @date 2018-2-1-20:19
 */

public class PlotIntermediateFrequencyPart implements Runnable{
    private static final String CHART_CONTENT = "中频信号";
    private static final String CHART_TITLE = "中频信号时域图";
    private static final String XLABEL_NAME = "时间";
    private static final String YLABEL_NAME = "幅度";
    private static final double DATA_LENGTH_SHOWED = 10000D;
    private static final long UPDATE_INTERVAL = 100;
    private  CreateTimeSeriesChart timeSeriesChart;

    public JPanel createIntermediateFrequencyChart(BlockingQueue<Packet> dataToshow) {
        timeSeriesChart = new CreateTimeSeriesChart(CHART_CONTENT,
                CHART_TITLE, XLABEL_NAME, YLABEL_NAME, DATA_LENGTH_SHOWED, UPDATE_INTERVAL, dataToshow);

        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.add(timeSeriesChart);

        return panel;
    }

    @Override
    public void run() {
        new Thread(timeSeriesChart).start();
    }
}
