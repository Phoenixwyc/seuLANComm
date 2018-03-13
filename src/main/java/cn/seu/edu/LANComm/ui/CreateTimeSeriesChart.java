package cn.seu.edu.LANComm.ui;

import cn.seu.edu.LANComm.communication.util.FramingDecoder;
import cn.seu.edu.LANComm.util.FontEnum;
import jpcap.packet.Packet;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2018/2/1.
 * http://blog.csdn.net/danmo598/article/details/21541177
 * @author WYCPhoenix
 * @date 2018-2-1-22:05
 */
public class CreateTimeSeriesChart extends ChartPanel implements Runnable{

    private static TimeSeries timeSeries;
    private long undateIntervalInmills;
    private BlockingQueue<Packet> data;
    private JPanel chartPanel;
    /**
     * 创建时序图
     * @param chartContent legend
     * @param chartTitle 标题
     * @param xAxisName X轴label
     * @param yAxisName Y轴label
     * @param dataLenShowd 展示的数据长度，建议设大一点，以减少CPU压力
     * @param undateIntervalInmills 数据刷新时间，建议设大一点
     */
    public CreateTimeSeriesChart(String chartContent, String chartTitle, String xAxisName,
                                         String yAxisName, double dataLenShowd, long undateIntervalInmills, BlockingQueue<Packet> data, JPanel chartPanel) {
        super(createChart(chartContent, chartTitle, xAxisName, yAxisName, dataLenShowd));
        this.undateIntervalInmills = undateIntervalInmills;
        this.data = data;
        this.chartPanel = chartPanel;
    }

    private static JFreeChart createChart(String chartContent, String chartTitle, String xAxisName,
                                          String yAxisName, double dataLenShowed) {

        StandardChartTheme standardChartTheme = new StandardChartTheme("EN");
        standardChartTheme.setExtraLargeFont(FontEnum.CHART_TITLE_FONT.getFont());
        standardChartTheme.setRegularFont(FontEnum.CHART_TITLE_FONT.getFont());
        standardChartTheme.setLargeFont(FontEnum.CHART_TITLE_FONT.getFont());
        ChartFactory.setChartTheme(standardChartTheme);

        timeSeries = new TimeSeries(chartContent);
        TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection(timeSeries);
        JFreeChart jFreeChart = ChartFactory.createTimeSeriesChart(chartTitle, xAxisName,
                yAxisName, timeSeriesCollection, true, true, false);

        ValueAxis valueAxis = jFreeChart.getXYPlot().getDomainAxis();
        valueAxis.setAutoRange(true);
        valueAxis.setFixedAutoRange(dataLenShowed);
        valueAxis.setLabelFont(FontEnum.CHART_XYLABEL_FONT.getFont());

        return jFreeChart;

    }
    @Override
    public void run() {
        System.out.println("中频信号绘图线程启动");
        // TODO: 2018/2/1 消费者，数据来自网卡，实现timeSeries的数据更新
        try {
            while (true) {
                Packet packet = data.poll(10000, TimeUnit.MILLISECONDS);
                if (packet != null) {
                    float[] dataToAdd = new FramingDecoder(packet.data).getTransmittedData();
                    for (float data : dataToAdd) {
                        timeSeries.addOrUpdate(new Millisecond(), data);
                    }
                    Thread.sleep(this.undateIntervalInmills);
                } else {
                    System.out.println("数据缓冲区为空");
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**
     * 重写ChartPanel的getPreferredSize
     * 实现画布大小动态调整
     * @return
     */
    @Override
    public Dimension getPreferredSize() {

        return chartPanel.getSize();
    }

    public BlockingQueue<Packet> getData() {
        return data;
    }

    public void setData(BlockingQueue<Packet> data) {
        this.data = data;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();

        String chartContent = "随机数";
        String chartTitle = "随机数标题";
        String xAxisLabel = "时间";
        String yAxisLabel = "随机数";
        double dataLenShowed = 10000d;
        long updateInterval = 100;
        BlockingQueue<Packet> data = null;

        CreateTimeSeriesChart createTimeSeriesChart = new CreateTimeSeriesChart(
                chartContent, chartTitle, xAxisLabel, yAxisLabel,dataLenShowed, updateInterval, data, new JPanel());
        frame.getContentPane().add(createTimeSeriesChart);
        frame.pack();
        frame.setVisible(true);
        (new Thread(createTimeSeriesChart)).start();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
