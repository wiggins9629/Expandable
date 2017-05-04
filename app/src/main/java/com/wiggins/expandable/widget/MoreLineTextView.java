package com.wiggins.expandable.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wiggins.expandable.R;
import com.wiggins.expandable.utils.DensityUtil;
import com.wiggins.expandable.widget.expandable.AlignTextView;

/**
 * @Description 多文本折叠/展开效果
 * @Author 一花一世界
 */
public class MoreLineTextView extends LinearLayout implements View.OnClickListener {

    private AlignTextView mTvContent;
    private LinearLayout mLlExpand;
    private TextView mTvExpand;
    private ImageView mIvExpand;

    private int mTextColor;//内容区域字体颜色
    private float mTextSize;//内容区域字体大小
    private int mMaxLine;//内容区域最大行数
    private Drawable mExpandDrawable;//展开/收起图标
    private int mDurationMillis;//展开/收起动画时间
    private boolean mClickAll;//可点击区域，默认展开/收起区域点击

    private static final int DEFAULT_MAX_LINES = 5;//默认最大行数
    private static final int DEFAULT_ANIM_TIME = 350;//默认动画执行时间
    private boolean isExpand;//是否展开
    private boolean mRelayout;//是否有重新绘制

    public MoreLineTextView(Context context) {
        this(context, null);
    }

    public MoreLineTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initWithAttrs(attrs);
    }

    public MoreLineTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initWithAttrs(attrs);
    }

    private void initWithAttrs(AttributeSet attrs) {
        if (isInEditMode()) {
            //显示一个IDE编辑状态下标题栏
            TextView textView = new TextView(getContext());
            textView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            addView(textView);
        } else {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.MoreTextStyle);
            mTextColor = typedArray.getColor(R.styleable.MoreTextStyle_textColor, ContextCompat.getColor(getContext(), R.color.gray));
            mTextSize = typedArray.getDimension(R.styleable.MoreTextStyle_textSize, DensityUtil.sp2px(14));
            mMaxLine = typedArray.getInt(R.styleable.MoreTextStyle_maxLine, DEFAULT_MAX_LINES);
            mExpandDrawable = typedArray.getDrawable(R.styleable.MoreTextStyle_expandIcon);
            mDurationMillis = typedArray.getInt(R.styleable.MoreTextStyle_durationMillis, DEFAULT_ANIM_TIME);
            mClickAll = typedArray.getBoolean(R.styleable.MoreTextStyle_clickAll, false);
            if (mExpandDrawable == null) {
                mExpandDrawable = ContextCompat.getDrawable(getContext(), R.drawable.icon_green_arrow_down);
            }
            typedArray.recycle();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initView();
    }

    protected void initView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.more_expand_shrink, this);
        mTvContent = (AlignTextView) view.findViewById(R.id.tv_content);
        mLlExpand = (LinearLayout) view.findViewById(R.id.ll_expand);
        mTvExpand = (TextView) view.findViewById(R.id.tv_expand);
        mIvExpand = (ImageView) view.findViewById(R.id.iv_expand);
        if (mClickAll) {
            mTvContent.setOnClickListener(this);
        }
        bindTextView();
    }

    protected void bindTextView() {
        mTvContent.setTextColor(mTextColor);
        mTvContent.getPaint().setTextSize(mTextSize);
        mIvExpand.setImageDrawable(mExpandDrawable);
        mLlExpand.setOnClickListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 如果没有变化，测量并返回
        if (!mRelayout || getVisibility() == View.GONE) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        mRelayout = false;

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // 内容区域初始显示行高
        mTvContent.setHeight(mTvContent.getLineHeight() * (mMaxLine > mTvContent.getLineCount() ? mTvContent.getLineCount() : mMaxLine));
        mLlExpand.post(new Runnable() {

            @Override
            public void run() {
                // 是否显示折叠效果
                mLlExpand.setVisibility(mTvContent.getLineCount() > mMaxLine ? View.VISIBLE : View.GONE);
            }
        });
    }

    /**
     * @Description 设置显示内容
     */
    public void setText(String str) {
        mRelayout = true;
        mTvContent.setText(str);
        setVisibility(TextUtils.isEmpty(str) ? View.GONE : View.VISIBLE);
    }

    /**
     * @Description 设置内容区域点击，默认展开/收起区域点击
     */
    public void setListener() {
        mTvContent.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (mTvContent.getLineCount() <= mMaxLine) {
            return;
        }
        isExpand = !isExpand;
        mTvContent.clearAnimation();
        final int deltaValue;
        final int startValue = mTvContent.getHeight();
        if (isExpand) {
            deltaValue = mTvContent.getLineHeight() * mTvContent.getLineCount() - startValue;//计算要展开高度
            RotateAnimation animation = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            animation.setDuration(mDurationMillis);
            animation.setFillAfter(true);
            mIvExpand.startAnimation(animation);
            mTvExpand.setText(getContext().getString(R.string.collapse));
        } else {
            deltaValue = mTvContent.getLineHeight() * mMaxLine - startValue;//为负值，收缩的高度
            RotateAnimation animation = new RotateAnimation(180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            animation.setDuration(mDurationMillis);
            animation.setFillAfter(true);
            mIvExpand.startAnimation(animation);
            mTvExpand.setText(getContext().getString(R.string.expand));
        }
        Animation animation = new Animation() {
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                //interpolatedTime:为当前动画帧对应的相对时间，值总在0-1之间,原始长度+高度差*（从0到1的渐变）即表现为动画效果
                mTvContent.setHeight((int) (startValue + deltaValue * interpolatedTime));
            }
        };
        animation.setDuration(mDurationMillis);
        mTvContent.startAnimation(animation);
    }
}
