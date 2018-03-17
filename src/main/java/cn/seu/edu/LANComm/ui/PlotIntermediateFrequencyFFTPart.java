package cn.seu.edu.LANComm.ui;

import jpcap.packet.Packet;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.BlockingQueue;

/**
 * 在这里绘制中频信号的功率谱
 * Created by Administrator on 2018/1/27.
 */
public class PlotIntermediateFrequencyFFTPart implements Runnable{
    private static final String CHART_CONTENT = "中频信号功率谱";
    private static final String CHART_TITLE = "中频信号功率谱图";
    private static final String XLABEL_NAME = "频率 MHz";
    private static final String YLABEL_NAME = "幅度";
    private static final double DATA_LENGTH_SHOWED = 1000D;
    private static final long UPDATE_INTERVAL = 100;
    private  IntermediateFrequencyFFTChart intermediateFrequencyFFTChart;
    private JPanel chartPanel;
    private volatile float sampleRate;

    public PlotIntermediateFrequencyFFTPart(JPanel chartPanel) {
        this.chartPanel = chartPanel;
    }

    public JPanel createIntermediateFrequencyFFTChart(BlockingQueue<Packet> dataToShowed) {
        intermediateFrequencyFFTChart = new IntermediateFrequencyFFTChart(CHART_CONTENT, CHART_TITLE,
                XLABEL_NAME, YLABEL_NAME, DATA_LENGTH_SHOWED, UPDATE_INTERVAL, dataToShowed, chartPanel);
        chartPanel.setBackground(Color.WHITE);
        chartPanel.add(intermediateFrequencyFFTChart);

        return chartPanel;
    }

    @Override
    public void run() {
        System.out.println("中频FFT接收线程开始");
        intermediateFrequencyFFTChart.setSampleRate(getSampleRate());
        new Thread(intermediateFrequencyFFTChart).start();
        System.out.println("设置中频采样率为 " + intermediateFrequencyFFTChart.getSampleRate());
    }

    public float getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(float sampleRate) {
        this.sampleRate = sampleRate;
    }
}
