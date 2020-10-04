package ru.mauveferret;


import java.awt.*;
import java.text.DecimalFormat;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.PolarChartPanel;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.TickUnits;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PolarPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.DefaultPolarItemRenderer;
import org.jfree.chart.renderer.PolarItemRenderer;
import org.jfree.chart.renderer.xy.XYBlockRenderer;
import org.jfree.chart.title.PaintScaleLegend;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;

import javax.swing.*;


public class PolarChart extends ApplicationFrame {

    String title;
    double[] thetaDistr;
    double dTheta;

    public PolarChart(final String title, double[] thetaDistr,double dTheta ) {
        super(title);
        this.title = title;
        this.thetaDistr = thetaDistr;
        this.dTheta = dTheta;

        JFrame f = new JFrame(title);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("pics/CrocoLogo.png")));
        f.setLayout(new BorderLayout(0, 5));

        ChartPanel chartPanel = new ChartPanel(createChart(createDataset())) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(640, 640);
            }
        };

        chartPanel.getChart().getPlot().setBackgroundPaint(Color.BLACK);
        chartPanel.getChart().getPlot().setOutlinePaint(Color.WHITE);
        chartPanel.setMouseZoomable(true,true);
        setContentPane(chartPanel);
        f.add(chartPanel, BorderLayout.CENTER);
        f.pack();
        //f.setLocationRelativeTo(null);
        f.setVisible(true);
        chartPanel.setMouseWheelEnabled(true);
        chartPanel.setHorizontalAxisTrace(true);
        chartPanel.setVerticalAxisTrace(true);

    }

    private XYDataset createDataset() {
        final XYSeriesCollection data = new XYSeriesCollection();
        XYSeries mainSeries = new XYSeries(title);
        double max = 0;
        for (int i=0; i<thetaDistr.length; i++) {
            if (i*dTheta!=90) mainSeries.add(i*dTheta-90, thetaDistr[i]);
            if (thetaDistr[i]>max) max = thetaDistr[i];
        }
        XYSeries cosineSeries = new XYSeries("cosine");
        for (int i=0; i<thetaDistr.length; i++) {
             cosineSeries.add(i*dTheta-90, max*Math.cos(Math.toRadians(i*dTheta-90)));
        }
        data.addSeries(mainSeries);
        data.addSeries(cosineSeries);
        return data;
    }


    private JFreeChart createChart(XYDataset dataset) {

        final JFreeChart chart =  ChartFactory.createPolarChart(
              "Polar Chart "+title, dataset, true, true, true);
        PolarPlot polPlot =(PolarPlot)  chart.getPlot();
        polPlot.setAngleGridlinePaint(Color.GREEN);
        DecimalFormat df2 = new DecimalFormat("0");

        polPlot.setAngleTickUnit(new NumberTickUnit(15, df2, 4));
        polPlot.setRadiusGridlinePaint(Color.GREEN);
        Font font = new Font("Verdana", Font.BOLD, 24);
        polPlot.setAngleLabelPaint(Color.WHITE);
        polPlot.setAngleLabelFont(font);

        ValueAxis axis = polPlot.getAxis();
        axis.setTickLabelFont(font);
        axis.setLabelFont(font);


        DefaultPolarItemRenderer renderer = (DefaultPolarItemRenderer) polPlot.getRenderer();

        renderer.setSeriesVisible(0, true);
        renderer.setSeriesVisible(1, true);

        renderer.setSeriesStroke(0, new BasicStroke(2.0f));
        renderer.setSeriesStroke(1,new BasicStroke(
                4.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                1.0f, new float[] {10.0f, 6.0f}, 0.0f ));

        renderer.setSeriesFilled(1,false);
        renderer.setSeriesFilled(0,true);

        renderer.setSeriesPaint(0, Color.RED);
        renderer.setSeriesPaint(1,Color.CYAN);

        return chart;
    }

}