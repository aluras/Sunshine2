package app.com.example.andre.sunshine2;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by sn1007071 on 19/10/2015.
 */
public class DirectionView extends View{

    private int mDegree;
    private Paint mArrowPaint;

    public void setDegree(int degree){
        mDegree= degree;
        invalidate();
        requestLayout();
    }

    private void init(){
        mArrowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public DirectionView(Context context) {
        super(context);
    }

    public DirectionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.DirectionView,
                0, 0);

        try {
            mDegree = a.getInteger(R.styleable.DirectionView_degree, 0);
        } finally {
            a.recycle();
        }
    }

    public DirectionView(Context context, AttributeSet attrs, int defStyleRes) {
        super(context, attrs, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.rotate(mDegree,50,50);

        Paint mPiePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPiePaint.setStyle(Paint.Style.STROKE);
        mPiePaint.setTextSize(22);

        RectF rect = new RectF(0, 0, 100, 100);

        canvas.drawOval(rect, mPiePaint);
        canvas.drawLine(50, 50,50,0,mPiePaint);

    }
}
