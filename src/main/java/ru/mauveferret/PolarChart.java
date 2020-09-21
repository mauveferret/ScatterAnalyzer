package ru.mauveferret;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PolarPlot;
import org.jfree.chart.renderer.DefaultPolarItemRenderer;
import org.jfree.data.xy.DefaultXYZDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;

import javax.swing.*;

public class PolarChart {

    public JFreeChart createPolarChart(String title, int[] data, double dPhi,
                                              boolean legend, boolean tooltips, boolean urls) {


        XYSeriesCollection dataset = new  XYSeriesCollection();
        XYSeries series1 = new XYSeries("Object 1");
        for (int i = 0; i < data.length; i++) {
            series1.add(i*dPhi, data[i]);
        }
            dataset.addSeries(series1);

        // Create dataset


        // Create chart
        JFreeChart chart = ChartFactory.createPolarChart(
                "Polar Chart Example | WWW.BORAJI.COM", // Chart title
                dataset,
                true,
                true,
                false
        );

        ChartPanel panel = new ChartPanel(chart);
        panel.setMouseZoomable(false);
       // panel.setContentPane(panel);


        PolarPlot plot = new PolarPlot();
        plot.setDataset(dataset);
        NumberAxis rangeAxis = new NumberAxis();
        rangeAxis.setAxisLineVisible(false);
        rangeAxis.setTickMarksVisible(false);
        rangeAxis.setTickLabelInsets(new RectangleInsets(0.0, 0.0, 0.0, 0.0));
        plot.setAxis(rangeAxis);
        plot.setRenderer(new DefaultPolarItemRenderer());
        JFreeChart chart2 = new JFreeChart(
                title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);
        //currentTheme.apply(chart);
        return chart2;

    }
}
