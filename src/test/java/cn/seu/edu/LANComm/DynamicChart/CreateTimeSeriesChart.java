package cn.seu.edu.LANComm.DynamicChart;

import cn.seu.edu.LANComm.util.FontEnum;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2018/2/1.
 * http://blog.csdn.net/danmo598/article/details/21541177
 * @author WYCPhoenix
 * @date 2018-2-1-22:05
 */
public class CreateTimeSeriesChart extends ChartPanel implements Runnable{

    private long undateIntervalInmills;
    private volatile boolean timeSeriesChartIsRunning = true;
    BlockingQueue<Double> blockingQueue;
    private static TimeSeries timeSeries;
    /**
     * 创建时序图
     * @param chartTitle 标题
     * @param xAxisName X轴label
     * @param yAxisName Y轴label
     * @param dataLenShowd 展示的数据长度，建议设大一点，以减少CPU压力
     * @param undateIntervalInmills 数据刷新时间，建议设大一点
     */
    public CreateTimeSeriesChart(String chartContent, String chartTitle, String xAxisName,
                                 String yAxisName, double dataLenShowd, long undateIntervalInmills, BlockingQueue<Double> blockingQueue) {

        super(createChart(chartContent, chartTitle, xAxisName, yAxisName, dataLenShowd));
        this.undateIntervalInmills = undateIntervalInmills;
        this.blockingQueue = blockingQueue;
    }

    @SuppressWarnings("all")
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
        try {
            while (timeSeriesChartIsRunning) {
                Double value = blockingQueue.poll(5000, TimeUnit.MILLISECONDS);
                if (value != null) {
                    timeSeries.addOrUpdate(new Millisecond(), value.doubleValue());
                }
                Thread.sleep(undateIntervalInmills);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
