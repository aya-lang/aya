package aya.ext.plot;

import aya.exceptions.runtime.IOError;
import aya.exceptions.runtime.ValueError;
import aya.ext.graphics.Canvas;
import aya.ext.graphics.GraphicsInstructionStore;
import aya.obj.symbol.SymbolConstants;
import aya.util.DictReader;
import aya.util.FileUtils;
import aya.util.Sym;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.ui.RefineryUtilities;

import javax.swing.JFrame;
import java.io.File;
import java.io.IOException;

public class RenderConfig {
    public static String getDocString(String leftPad) {
        return (leftPad + "width::num : width of the plot in pixels. default=500\n"
                + leftPad + "height::num : height of the plot in pixels. default=400\n"
                + leftPad + "show::num (bool) : open the chart in a new window. default=true\n"
                + leftPad + "filename::str : if provided, saves the chart in a file with the given name\n"
                + leftPad + "canvas::dict : if provided, renders the chart onto an existing canvas\n"
                + leftPad + "    id::num : the id of the canvas to draw onto\n"
                + leftPad + "    x::num : the x offset for the chart\n"
                + leftPad + "    y::num : the y offset for the chart\n"
        );
    }

    /** Width of the plot. Default: 500 */
    public final int width;
    /** Height of the plot. Default: 400 */
    public final int height;
    /** Open the Chart in a new window. Default: true */
    public final boolean show;
    /** If provided, saves the chart in a file with the given name */
    public final String filename;
    /** If provided, renders the Chart onto an existing canvas */
    public final CanvasOptions canvas;

    public RenderConfig(DictReader d) {
        this.width = d.getInt(SymbolConstants.WIDTH, 500);
        this.height = d.getInt(SymbolConstants.HEIGHT, 400);
        this.show = d.getBool(Sym.sym("show"), true);
        this.filename = d.getString(Sym.sym("filename"));
        this.canvas = d.hasKey(Sym.sym("canvas")) ? new CanvasOptions(d.getDictReaderEx(Sym.sym("canvas"))) : null;
    }

    public static class CanvasOptions {
        /** The id of the canvas to draw to */
        public final int id;
        /** The x offset for placing the chart on the canvas */
        public final int x;
        /** The y offset for placing the chart on the canvas */
        public final int y;

        public CanvasOptions(DictReader canvasD) {
            this.id = canvasD.getIntEx(Sym.sym("id"));
            this.x = canvasD.getInt(Sym.sym("x"), 0);
            this.y = canvasD.getInt(Sym.sym("y"), 0);
        }
    }

    public void renderChart(String operatorName, JFreeChart chart) {
        if (filename != null && !filename.isEmpty()) {
            exportChart(operatorName, chart);
        }
        if (show) {
            openWindow(chart);
        }
        if (canvas != null) {
            renderToCanvas(chart);
        }
    }

    private void exportChart(String operatorName, JFreeChart chart) {
        File exportFile = FileUtils.resolveFile(filename);
        try {
            if (filename.toLowerCase().endsWith(".png")) {
                ChartUtilities.saveChartAsPNG(exportFile, chart, width, height);
            } else if (filename.toLowerCase().endsWith(".jpg")) {
                ChartUtilities.saveChartAsJPEG(exportFile, chart, width, height);
            } else {
                throw new ValueError(operatorName + ": Please specify either '*.png' ot '*.jpg' in the filename\n"
                        + "Received: " + filename);
            }
        } catch (IOException e) {
            throw new IOError(operatorName, exportFile.getAbsolutePath(), e);
        }
    }

    private void openWindow(JFreeChart chart) {
        ChartPanel cp = new ChartPanel(chart);
        cp.setPreferredSize(new java.awt.Dimension(width, height));
        JFrame frame = new JFrame();
        frame.setContentPane(cp);

        frame.pack();
        RefineryUtilities.centerFrameOnScreen(frame);
        frame.setVisible(true);
    }

    private void renderToCanvas(JFreeChart chart) {
        Canvas cvs = GraphicsInstructionStore.canvas_table.getCanvas(canvas.id);
        cvs.getG2D().drawImage(chart.createBufferedImage(width, height), canvas.x, canvas.y, null);
    }
}
