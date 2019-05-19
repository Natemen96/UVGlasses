package graphes;

import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;


/**
 * Created by Andrew on 1/20/17.
 *
 *      MonitorGraph modifies an existing MP Android Chart to display data generated from
 *  an fNIRS data collecting system.
 *
 *  This class only has the potential to modify a line chart.
 */

public class MonitorGraph
{
    private LineData lineData;
    private Legend legend;
    private XAxis xAxis;
    private YAxis yAxis;
    private YAxis yAxis2;

    public LineChart createChart(LineChart chart)
    {
        //  Sets the descriptions of the graph
        chart.setDescription("");

        //  Enables value highlighting
        //chart.setHighlightPerDragEnabled(true);

        //  Enables touch gestures
        chart.setTouchEnabled(true);

        //  Enables scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setDrawGridBackground(false);

        //  Enable pinch zoom to avoid scaling x and y separately
        chart.setPinchZoom(true);

        //  Disables double-touch zoom
        chart.setDoubleTapToZoomEnabled(false);

        //  Set background color
        chart.setBackgroundColor(Color.WHITE);

        //  Creates data and adds it to the chart
        lineData = new LineData();
        lineData.setValueTextColor(Color.BLUE);
        chart.setData(lineData);

        //  Creates the legend
        legend = chart.getLegend();
        legend.setForm(Legend.LegendForm.LINE);
        legend.setTextColor(Color.WHITE);

        //  Creates the X Axis
        //xAxis = chart.getXAxis();
        //xAxis.setTextColor(Color.WHITE);
        //xAxis.setDrawGridLines(false);
        //xAxis.setAvoidFirstLastClipping(true);
        //xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        //  Creates the Y Axis
        yAxis = chart.getAxisLeft();
        yAxis.setTextColor(Color.BLACK);
        yAxis.setAxisMaxValue(12f);
        yAxis.setAxisMinValue(0f);
        yAxis.setDrawGridLines(true);

        //  Disables the right y-axis
        yAxis2 = chart.getAxisRight();
        yAxis2.setEnabled(false);

        return chart;
    }
}
