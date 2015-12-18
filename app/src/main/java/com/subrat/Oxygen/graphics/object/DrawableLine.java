package com.subrat.Oxygen.graphics.object;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

import com.subrat.Oxygen.interfaces.LineInterface;
import com.subrat.Oxygen.physics.object.PhysicsLine;
import com.subrat.Oxygen.utilities.Configuration;
import com.subrat.Oxygen.utilities.MathUtils;
import com.subrat.Oxygen.utilities.ObjectMapper;

/**
 * Created by subrat.panda on 18/12/15.
 */
public class DrawableLine extends DrawableObject implements LineInterface {
    private PointF start;
    public PointF getStart() { return start; }
    public void setStart(PointF point) { start = point; }

    private PointF end;
    public PointF getEnd() { return end; }
    public void setEnd(PointF point) { end = point; }

    private void setEndPoints(PointF start, PointF end) {
        this.start = start; this.end = end;
    }

    static Paint linePainter = null;

    // Only to be called from GraphicsObjectBuilder
    public DrawableLine(PointF start, PointF end) {
        setEndPoints(start, end);
    }

    protected Paint getLinePainter() {
        if (linePainter == null) {
            linePainter = new Paint();
            linePainter.setColor(Color.YELLOW);
            linePainter.setAntiAlias(true);
            linePainter.setStyle(Paint.Style.STROKE);
            linePainter.setStrokeWidth(MathUtils.getMathUtils().getPixelFromMeter(Configuration.LINE_THICKNESS));
        }

        return linePainter;
    }

    public boolean draw(Canvas canvas) {
        PointF startPixel = MathUtils.getMathUtils().getPixelBasedPointFromMeterBasedPoint(start);
        PointF endPixel = MathUtils.getMathUtils().getPixelBasedPointFromMeterBasedPoint(end);
        canvas.drawLine(startPixel.x, startPixel.y, endPixel.x, endPixel.y, getLinePainter());
        return true;
    }
}
