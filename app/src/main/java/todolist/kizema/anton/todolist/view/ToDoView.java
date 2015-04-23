package todolist.kizema.anton.todolist.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import todolist.kizema.anton.todolist.app.App;
import todolist.kizema.anton.todolist.R;

/**
 * Created by Anton on 08.04.2015.
 */
public class ToDoView extends View {

    private static final int TEXT_COLOR = Color.BLACK;

    private Paint mTextTitlePaint, mTextDescrPaint, mRedPaint;
    private int mTitleHeight, mDescrHeight;
    private int mTextColor = TEXT_COLOR;

    Rect bounds;

    private String title, descr;

    public ToDoView(Context context) {
        super(context);

        init();
    }

    public ToDoView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(attrs);
        init();
    }

    public ToDoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(attrs);
        init();
    }

    private void init(AttributeSet attrs){
        TypedArray a = getContext().getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ToDoView,
                0, 0);

        try {
            title = a.getString(R.styleable.ToDoView_todotitle);
            descr = a.getString(R.styleable.ToDoView_tododescription);
        } finally {
            a.recycle();
        }
    }

    public void setTitle(String title){
        this.title = title;
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // Account for padding
        float xpad = (float)(getPaddingLeft() + getPaddingRight());
        float ypad = (float)(getPaddingTop() + getPaddingBottom());

        float ww = (float)w - xpad;
        float hh = (float)h - ypad;

        super.onSizeChanged(w, h, oldw, oldh);
    }

    private void init(){
        mTitleHeight = App.getPixel(18);
        mDescrHeight = App.getPixel(12);


        mTextDescrPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextDescrPaint.setColor(mTextColor);
        mTextDescrPaint.setTextSize(mDescrHeight);

        mTextTitlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextTitlePaint.setColor(mTextColor);
        mTextTitlePaint.setTextSize(mTitleHeight);

        bounds = new Rect();
        mTextTitlePaint.getTextBounds("a", 0, 1, bounds);

        mRedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRedPaint.setStyle(Paint.Style.FILL);
        mRedPaint.setColor(Color.RED);

//        mShadowPaint.setMaskFilter(new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawText(title, 40, 30, mTextTitlePaint);

//        canvas.drawText(title, canvas.getWidth() >> 1, (canvas.getHeight() + bounds.height()) >> 1, mTextTitlePaint);
    }

}
