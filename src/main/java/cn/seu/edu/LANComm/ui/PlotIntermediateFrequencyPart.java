package cn.seu.edu.LANComm.ui;


import jpcap.packet.Packet;

import javax.swing.JPanel;
import java.awt.Color;
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
    private static final double DATA_LENGTH_SHOWED = 2000D;
    private static final long UPDATE_INTERVAL = 10;
    private  CreateTimeSeriesChart timeSeriesChart;
    private JPanel chartPanel;
    private volatile boolean isRunning = true;

    public PlotIntermediateFrequencyPart(JPanel chartPanel) {
        this.chartPanel = chartPanel;
    }

    public JPanel createIntermediateFrequencyChart(BlockingQueue<Packet> dataToshow) {
        timeSeriesChart = new CreateTimeSeriesChart(CHART_CONTENT,
                CHART_TITLE, XLABEL_NAME, YLABEL_NAME, DATA_LENGTH_SHOWED, UPDATE_INTERVAL, dataToshow, chartPanel);


        chartPanel.setBackground(Color.WHITE);
        chartPanel.add(timeSeriesChart);

        return chartPanel;
    }

    @Override
    public void run() {
        System.out.println("中频信号接收线程启动");
        new Thread(timeSeriesChart).start();
    }

    public void stop() {
        timeSeriesChart.stop();
    }
}
