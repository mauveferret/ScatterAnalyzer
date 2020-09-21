package ru.mauveferret;

/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2004, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc.
 * in the United States and other countries.]
 *
 * -------------------
 * PolarChartDemo.java
 * -------------------
 * (C) Copyright 2004, by Solution Engineering, Inc. and Contributors.
 *
 * Original Author:  Daniel Bridenbecker, Solution Engineering, Inc.;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *
 * $Id: PolarChartDemo.java,v 1.4 2004/04/30 11:19:22 mungady Exp $
 *
 * Changes
 * -------
 * 19-Jan-2004 : Version 1, contributed by DB with minor changes by DG (DG);
 *
 */


import java.awt.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.PolarChartPanel;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PolarPlot;
import org.jfree.chart.renderer.DefaultPolarItemRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;

import javax.swing.*;

/**
 * <code>PolarChartDemo</code> demonstrates the capabilities of the {@link PolarPlot}.
 *
 * @author  Daniel Bridenbecker, Solution Engineering, Inc.
 */
public class PolarChart extends ApplicationFrame {

    /**
     * Creates a new instance of the demo.
     *
     * @param title  the frame title.
     */
    String title;

    public PolarChart(final String title, int[] thetaDistr,double dTheta ) {
        super(title);
        this.title = title;
        final XYDataset dataset = createDataset(thetaDistr, dTheta);
        final JFreeChart chart = createChart(dataset);

        final ChartPanel chartPanel = new PolarChartPanel(chart);

        chartPanel.setPreferredSize(new Dimension(500, 500));
        chartPanel.setEnforceFileExtensions(false);
        chartPanel.setMouseZoomable(true,true);
        setContentPane(chartPanel);

        JFrame f = new JFrame(title);
        f.setTitle(title);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setLayout(new BorderLayout(0, 5));
        f.add(chartPanel, BorderLayout.CENTER);
        chartPanel.setMouseWheelEnabled(true);
        chartPanel.setHorizontalAxisTrace(true);
        chartPanel.setVerticalAxisTrace(true);

        PolarPlot polPlot = (PolarPlot) chart.getPlot();

        polPlot.setAngleGridlinePaint(Color.GREEN);
        polPlot.setRadiusGridlinePaint(Color.GREEN);
        Font font = new Font("Verdana", Font.BOLD, 24);
        polPlot.setAngleLabelPaint(Color.WHITE);
        polPlot.setAngleLabelFont(font);

        ValueAxis axis = polPlot.getAxis();
        axis.setTickLabelFont(font);
        axis.setLabelFont(font);
        axis.setMinorTickCount(5);
        chartPanel.getChart().getPlot().setBackgroundPaint(Color.BLACK);
        chartPanel.getChart().getPlot().setOutlinePaint(Color.WHITE);

        DefaultPolarItemRenderer renderer = (DefaultPolarItemRenderer) polPlot.getRenderer();
        renderer.setSeriesStroke(0, new BasicStroke(5.0f));;
        renderer.setSeriesFilled(0, true);

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setFont(font);
        f.add(panel, BorderLayout.SOUTH);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    /**
     * Creates a sample dataset.
     *
     * @return A sample dataset.
     */
    private XYDataset createDataset(int[] thetaDistr,double dTheta) {
        final XYSeriesCollection data = new XYSeriesCollection();
        XYSeries series = new XYSeries(title);
        for (int i=0; i<thetaDistr.length; i++) {
            series.add(i*dTheta, thetaDistr[i]);
        }
        //final XYSeries series2 = createRandomData("Series 2", 50.0, 5.0);
        //final XYSeries series3 = createRandomData("Series 3", 25.0, 1.0);
        data.addSeries(series);
        //data.addSeries(series2);
        //data.addSeries(series3);
        return data;
    }


    /**
     * Creates a sample chart.
     *
     * @param dataset  the dataset.
     *
     * @return A sample chart.
     */
    private JFreeChart createChart(final XYDataset dataset) {
        final JFreeChart chart = ChartFactory.createPolarChart(
                "Polar Chart "+title, dataset, true, true, false
        );
       return chart;
    }

}