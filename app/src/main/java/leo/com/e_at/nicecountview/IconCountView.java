package leo.com.e_at.nicecountview;


import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class IconCountView extends LinearLayout {
    private ImageView mImageView;
    private NiceCountView mCountView;
    private int mSelectedRes;
    private int mNormalRes;
    private boolean mIsSelected;

    public IconCountView(Context context) {
        this(context, null);
    }

    public IconCountView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IconCountView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        View layout = LayoutInflater.from(context).inflate(R.layout.icon_count_view, this);
        mCountView = layout.findViewById(R.id.count_view);
        mImageView = layout.findViewById(R.id.iv_icon);
        TypedArray ary = context.obtainStyledAttributes(attrs, R.styleable.IconCountView);

        final boolean isSelected = ary.getBoolean(R.styleable.IconCountView_state, false);
        int normalRes = ary.getResourceId(R.styleable.IconCountView_normalRes, R.drawable.icon_praise_normal);
        int selectedRes = ary.getResourceId(R.styleable.IconCountView_selectedRes, R.drawable.icon_collect_selected);
        long count = ary.getInt(R.styleable.IconCountView_count, 0);
        String zeroText = ary.getString(R.styleable.IconCountView_zeroText);
        int textNormalColor = ary.getColor(R.styleable.IconCountView_textNormalColor, getResources().getColor(R.color.text_gray));
        int textSelectedColor = ary.getColor(R.styleable.IconCountView_textSelectedColor, getResources().getColor(R.color.text_gray));

        int textSize = ary.getDimensionPixelSize(R.styleable.IconCountView_textSize, getResources().getDimensionPixelOffset(R.dimen.dimen_text_normal));

        ary.recycle();

        setIconRes(normalRes, selectedRes);
        initCountView(zeroText, count, textNormalColor, textSelectedColor, textSize, isSelected);
        setSelected(isSelected);

        layout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                praiseChange(!isSelected);
            }
        });
    }

    private void initCountView(String zeroText, long count, int textNormalColor, int textSelectedColor, int textSize, boolean isSelected) {
        mCountView.setZeroText(zeroText);
        mCountView.setCount(count);
        mCountView.setTextSize(textSize);
        mCountView.setNormalColor(textNormalColor);
        mCountView.setSelectedColor(textSelectedColor);
        mCountView.setIsSelected(isSelected);
    }

    private void setIconRes(int normalRes, int selectedRes) {
        mNormalRes = normalRes;
        mSelectedRes = selectedRes;
        mImageView.setImageResource(mNormalRes);
    }

    public void setZeroText(String zeroText) {
        mCountView.setZeroText(zeroText);
    }

    public void setCount(long count) {
        mCountView.setCount(count);
    }


    public void setState(boolean isSelected) {
        mIsSelected = isSelected;
        mImageView.setImageResource(mIsSelected ? mSelectedRes : mNormalRes);
    }


    private void praiseChange(boolean isPraised) {
        mIsSelected = isPraised;
        mImageView.setImageResource(mIsSelected ? mSelectedRes: mNormalRes);

        animateImageView(isPraised);
        if (isPraised) {
            mCountView.addCount();
        } else {
            mCountView.minusCount();
        }

        if (mChangedListener != null) {
            mChangedListener.select(mIsSelected);
        }
    }

    private void animateImageView(boolean isPraised) {
        float toScale = isPraised ? 1.2f : 0.9f;
        PropertyValuesHolder propertyValuesHolderX = PropertyValuesHolder.ofFloat("scaleX", 1.0f, toScale, 1.0f);
        PropertyValuesHolder propertyValuesHolderY = PropertyValuesHolder.ofFloat("scaleY", 1.0f, toScale, 1.0f);
        ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(mImageView, propertyValuesHolderX, propertyValuesHolderY);
        objectAnimator.start();
    }

    private OnSelectedChangedListener mChangedListener;

    public void setOnSelectedChangedListener(OnSelectedChangedListener listener) {
        mChangedListener = listener;
    }

    public interface OnSelectedChangedListener {
        void select(boolean isSelected);
    }

}
