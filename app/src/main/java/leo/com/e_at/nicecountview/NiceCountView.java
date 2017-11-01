package leo.com.e_at.nicecountview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;


public class NiceCountView extends View {
    private Paint mNormalPaint, mSelectedPaint;
    private int height, width;

    private static final int CONTENT_SPACE = 1;
    private static final int WIDTH_PADDING = 60;
    private static final int HEIGHT_PADDING = 12;

    private long mCurCount, mNewCount;
    private String mStrNewCount;
    private List<String> mCurNumberList = new ArrayList<>();
    private List<String> mNewNumberList = new ArrayList<>();
    private final int TEXT_DEFAULT_SIZE = getResources().getDimensionPixelSize(R.dimen.dimen_text_normal);
    private ValueAnimator mAnimator;
    private float mCurAniValue;
    private Rect mRect = new Rect();
    private Rect mDigitalRect = new Rect();
    private String mZeroText;
    private boolean isSelected = false;

    public NiceCountView(Context context) {
        this(context, null);
    }

    public NiceCountView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NiceCountView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mNormalPaint = new Paint();
        mSelectedPaint = new Paint();

        mNormalPaint.setTextSize(TEXT_DEFAULT_SIZE);
        mNormalPaint.setColor(getResources().getColor(R.color.text_gray));
        mNormalPaint.setStyle(Paint.Style.FILL);
        mNormalPaint.setAntiAlias(true);

        mSelectedPaint.setColor(getResources().getColor(R.color.text_gray));
        mSelectedPaint.setTextSize(TEXT_DEFAULT_SIZE);
        mSelectedPaint.setAntiAlias(true);
        mSelectedPaint.setStyle(Paint.Style.FILL);

        mNormalPaint.getTextBounds("0", 0, 1, mDigitalRect);

        initAnimator();
        postInvalidate();
    }

    private void initAnimator() {
        mAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
        mAnimator.setDuration(500);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mCurAniValue = (float) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mCurCount = mNewCount;
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int len = mNewNumberList.size();
        float y = HEIGHT_PADDING + mDigitalRect.height();
        for (int i = 0; i < len; i++) {
            String newDigital = mNewNumberList.get(i);
            String oldDigital = "";
            if (mCurNumberList.size() > i) {
                oldDigital = mCurNumberList.get(i);
            }
            float x = (mDigitalRect.width() + WIDTH_PADDING) * i;
            if (newDigital.equals(oldDigital)) {
                canvas.drawText(newDigital, x, y, getCurPaint(isSelected));
            } else if (mNewCount > mCurCount) {
                if (!TextUtils.isEmpty(oldDigital)) {
                    drawOut(canvas, oldDigital, x, y - (mCurAniValue * HEIGHT_PADDING));
                }

                drawIn(canvas, newDigital, x, y + (HEIGHT_PADDING - mCurAniValue * HEIGHT_PADDING));
            } else {
                if (!TextUtils.isEmpty(oldDigital)) {
                    drawOut(canvas, oldDigital, x, y + (mCurAniValue * HEIGHT_PADDING));
                }

                drawIn(canvas, newDigital, x, y - (HEIGHT_PADDING - mCurAniValue * HEIGHT_PADDING));
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mRect.setEmpty();
        mNormalPaint.getTextBounds(mStrNewCount, 0, mStrNewCount.length(), mRect);
        int textWidth = mRect.width() + WIDTH_PADDING * 2;
        int textHeight = mRect.height() + HEIGHT_PADDING * 2;
        int dw = resolveSizeAndState(textWidth, widthMeasureSpec, 0);
        int dh = resolveSizeAndState(textHeight, heightMeasureSpec, 0);
        setMeasuredDimension(dw, dh);

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        changeCount(0);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(mAnimator != null && mAnimator.isRunning()){
            mAnimator.end();
        }
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public void setNormalColor(int color) {
        mNormalPaint.setColor(color);
    }

    public void setSelectedColor(int color) {
        mSelectedPaint.setColor(color);
    }

    public void setTextSize(int size) {
        mNormalPaint.setTextSize(size);
        mSelectedPaint.setTextSize(size);
    }

    public void addCount() {
        changeCount(1);
        isSelected = true;
    }

    public void minusCount() {
        changeCount(-1);
        isSelected = false;
    }

    public void setZeroText(String text) {
        mZeroText = text;
    }

    public void setCount(long count) {
        mCurCount = count;
        changeCount(0);
    }

    public void changeCount(long count) {
        if (mAnimator != null && mAnimator.isRunning()) {
            mAnimator.end();
        }

        mNewCount = mCurCount + count;
        convert2DigitalList(mCurCount, mCurNumberList);
        convert2DigitalList(mNewCount, mNewNumberList);


        if (mNewCount > 0) {
            mStrNewCount = String.valueOf(mNewCount);
        } else {
            mStrNewCount = mZeroText;
        }

        if (mAnimator != null && mNewCount != mCurCount) {
            mAnimator.start();
        } else {
            requestLayout();
        }
    }

    /**
     * 淡入
     *
     * @return
     * @paramters
     */
    private void drawIn(Canvas canvas, String digital, float x, float y) {
        Paint inPaint = getCurPaint(isSelected);
        inPaint.setAlpha((int) (mCurAniValue * 255));
        inPaint.setTextSize(TEXT_DEFAULT_SIZE * (mCurAniValue * 0.5f + 0.5f));
        canvas.drawText(digital, x, y, inPaint);
        inPaint.setAlpha(255);
        inPaint.setTextSize(TEXT_DEFAULT_SIZE);
    }

    /**
     * 淡出
     *
     * @return
     * @paramters
     */
    private void drawOut(Canvas canvas, String digital, float x, float y) {
        Paint outPaint = getCurPaint(!isSelected);
        outPaint.setAlpha((255 - (int) (mCurAniValue * 255)));
        outPaint.setTextSize(TEXT_DEFAULT_SIZE * (1.0f - mCurAniValue * 0.5f));
        canvas.drawText(digital, x, y, outPaint);
        outPaint.setAlpha(255);
        outPaint.setTextSize(TEXT_DEFAULT_SIZE);
    }

    private void convert2DigitalList(long num, List<String> digitalList) {
        digitalList.clear();
        if (num == 0) {
            digitalList.add(mZeroText);
        }

        while (num > 0) {
            digitalList.add(0, String.valueOf(num % 10));
            num /= 10;
        }
    }

    private Paint getCurPaint(boolean isSelected) {
        return isSelected ? mSelectedPaint : mNormalPaint;
    }
}
