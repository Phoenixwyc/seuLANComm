package cn.seu.edu.LANComm.ui;

import jpcap.packet.Packet;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.BlockingQueue;

/**
 * 这这里绘制星座图相关数据
 * Created by Administrator on 2018/1/27.
 * @author WYCPhoenix
 */
public class PlotConstellationDiagramPart implements Runnable{
    private static final String CHART_CONTENT = "接收星座";
    private static final String CHART_TITLE = "接收端星座图";
    private static final String XLABEL_NAME = "同相 I";
    private static final String YLABEL_NAME = "正交 Q";
    private static final double DATA_LENGTH_SHOWED = 3D;
    private static final long UPDATE_INTERVAL = 1;
    private  ConstellationDiagramChart constellationDiagramChart;
    private JPanel chartPanel;

    public PlotConstellationDiagramPart(JPanel chartPanel) {
        this.chartPanel = chartPanel;
    }

    public JPanel createConstellationDiagramChart(BlockingQueue<Packet> dataToShowed) {
        constellationDiagramChart = new ConstellationDiagramChart(CHART_CONTENT, CHART_TITLE,
                XLABEL_NAME, YLABEL_NAME, DATA_LENGTH_SHOWED, UPDATE_INTERVAL, dataToShowed, chartPanel);

        chartPanel.setBackground(Color.WHITE);
        chartPanel.add(constellationDiagramChart);

        return chartPanel;
    }
    @Override
    public void run() {
        System.out.println("接收星座线程启动");
        new Thread(constellationDiagramChart).start();
    }
}
