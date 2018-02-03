package cn.seu.edu.LANComm.ui;

import cn.seu.edu.LANComm.util.FontEnum;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import javax.swing.JFrame;

/**
 * Created by Administrator on 2018/2/1.
 * http://blog.csdn.net/danmo598/article/details/21541177
 * @author WYCPhoenix
 * @date 2018-2-1-22:05
 */
public class CreateTimeSeriesChart extends ChartPanel implements Runnable{

    private static TimeSeries timeSeries;
    private long undateIntervalInmills;

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
                                         String yAxisName, double dataLenShowd, long undateIntervalInmills) {
        super(createChart(chartContent, chartTitle, xAxisName, yAxisName, dataLenShowd));
        this.undateIntervalInmills = undateIntervalInmills;
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
        // TODO: 2018/2/1 消费者，数据来自网卡，实现timeSeries的数据更新
        while (true) {

            timeSeries.addOrUpdate(new Millisecond(), Math.random() * 100);
            try{
                Thread.sleep(this.undateIntervalInmills);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();

        String chartContent = "随机数";
        String chartTitle = "随机数标题";
        String xAxisLabel = "时间";
        String yAxisLabel = "随机数";
        double dataLenShowed = 10000d;
        long updateInterval = 100;

        CreateTimeSeriesChart createTimeSeriesChart = new CreateTimeSeriesChart(
                chartContent, chartTitle, xAxisLabel, yAxisLabel,dataLenShowed, updateInterval);
        frame.getContentPane().add(createTimeSeriesChart);
        frame.pack();
        frame.setVisible(true);
        (new Thread(createTimeSeriesChart)).start();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
