
package com.wiggins.expandable.widget.expandable;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wiggins.expandable.R;
import com.wiggins.expandable.utils.DensityUtil;

/**
 * @Description 多文本折叠/展开效果
 * @Author 一花一世界
 */
public class ExpandableTextView extends LinearLayout implements View.OnClickListener {

    private static final int MAX_COLLAPSED_LINES = 5;//默认最高行数
    private static final int DEFAULT_ANIM_DURATION = 350;//默认动画执行时间
    protected AlignTextView mTvContent;//内容TextView
    protected LinearLayout mLlExpand;//展开/收起布局
    protected TextView mTvExpand;//展开/收起TextView
    protected ImageView mIvExpandLeft;//展开/收起图标
    protected ImageView mIvExpandRight;//展开/收起图标
    private boolean mRelayout;//是否有重新绘制
    private boolean mCollapsed = true;//默认收起
    private Drawable mExpandDrawable;//展开图片
    private Drawable mCollapseDrawable;//收起图片
    private int mAnimationDuration;//动画执行时间
    private boolean mAnimating;//是否正在执行动画
    private OnExpandStateChangeListener mListener;//展开收起状态回调
    private SparseBooleanArray mCollapsedStatus;//ListView等列表情况下保存每个item的收起/展开状态
    private int mPosition;//列表位置
    private int mMaxCollapsedLines;//设置内容最大行数，超过隐藏
    private int mCollapsedHeight;//这个LinearLayout容器的高度
    private int mTextHeightWithMaxLines;//内容tv真实高度（含padding）
    private int mMarginBetweenTxtAndBottom;//内容tvMarginTopAmndBottom高度
    private int contentTextColor;//内容颜色
    private int collapseExpandTextColor;//收起展开颜色
    private float contentTextSize;//内容字体大小
    private float collapseExpandTextSize;//收起展开字体大小
    private String textCollapse;//收起文字
    private String textExpand;//展开文字
    private int grarity;//收起展开位置，默认左边
    private int drawableGrarity;//收起展开图标位置，默认在右边
    private boolean mClickAll;//可点击区域，默认展开/收起区域点击
    private boolean mIsDisplayIcon;//是否显示展开/收起图标，默认显示

    public ExpandableTextView(Context context) {
        this(context, null);
    }

    public ExpandableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ExpandableTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    /**
     * @Description 初始化属性
     */
    private void init(AttributeSet attrs) {
        if (isInEditMode()) {
            //显示一个IDE编辑状态下标题栏
            TextView textView = new TextView(getContext());
            textView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            addView(textView);
        } else {
            mCollapsedStatus = new SparseBooleanArray();

            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ExpandableTextView);
            mMaxCollapsedLines = typedArray.getInt(R.styleable.ExpandableTextView_maxCollapsedLines, MAX_COLLAPSED_LINES);
            mAnimationDuration = typedArray.getInt(R.styleable.ExpandableTextView_animDuration, DEFAULT_ANIM_DURATION);
            mExpandDrawable = typedArray.getDrawable(R.styleable.ExpandableTextView_expandDrawable);
            mCollapseDrawable = typedArray.getDrawable(R.styleable.ExpandableTextView_collapseDrawable);
            textCollapse = typedArray.getString(R.styleable.ExpandableTextView_textCollapse);
            textExpand = typedArray.getString(R.styleable.ExpandableTextView_textExpand);

            if (mExpandDrawable == null) {
                mExpandDrawable = ContextCompat.getDrawable(getContext(), R.drawable.icon_green_arrow_up);
            }
            if (mCollapseDrawable == null) {
                mCollapseDrawable = ContextCompat.getDrawable(getContext(), R.drawable.icon_green_arrow_down);
            }

            if (TextUtils.isEmpty(textCollapse)) {
                textCollapse = getContext().getString(R.string.collapse);
            }
            if (TextUtils.isEmpty(textExpand)) {
                textExpand = getContext().getString(R.string.expand);
            }

            contentTextColor = typedArray.getColor(R.styleable.ExpandableTextView_contentTextColor, ContextCompat.getColor(getContext(), R.color.gray));
            contentTextSize = typedArray.getDimension(R.styleable.ExpandableTextView_contentTextSize, DensityUtil.sp2px(14));
            collapseExpandTextColor = typedArray.getColor(R.styleable.ExpandableTextView_collapseExpandTextColor, ContextCompat.getColor(getContext(), R.color.orange));
            collapseExpandTextSize = typedArray.getDimension(R.styleable.ExpandableTextView_collapseExpandTextSize, DensityUtil.sp2px(14));
            grarity = typedArray.getInt(R.styleable.ExpandableTextView_collapseExpandGrarity, Gravity.LEFT);
            drawableGrarity = typedArray.getInt(R.styleable.ExpandableTextView_drawableGrarity, Gravity.RIGHT);
            mClickAll = typedArray.getBoolean(R.styleable.ExpandableTextView_allClickable, false);
            mIsDisplayIcon = typedArray.getBoolean(R.styleable.ExpandableTextView_isDisplayIcon, true);

            typedArray.recycle();
            // 执行垂直方向
            setOrientation(LinearLayout.VERTICAL);
            // 默认不可见
            setVisibility(GONE);
        }
    }

    @Override
    public void setOrientation(int orientation) {
        if (LinearLayout.HORIZONTAL == orientation) {
            throw new IllegalArgumentException("ExpandableTextView only supports Vertical Orientation.");
        }
        super.setOrientation(orientation);
    }

    /**
     * @Description 渲染完成时初始化View
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initViews();
    }

    /**
     * @Description 初始化View
     */
    private void initViews() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.item_expand_collapse, this);
        mTvContent = (AlignTextView) findViewById(R.id.expandable_text);
        if (mClickAll) {
            mTvContent.setOnClickListener(this);
        }
        mLlExpand = (LinearLayout) findViewById(R.id.ll_expand);
        mTvExpand = (TextView) findViewById(R.id.tv_expand);
        mIvExpandLeft = (ImageView) findViewById(R.id.iv_expand_left);
        mIvExpandRight = (ImageView) findViewById(R.id.iv_expand_right);
        setDrawbleAndText();
        mLlExpand.setOnClickListener(this);

        mTvContent.setTextColor(contentTextColor);
        mTvContent.getPaint().setTextSize(contentTextSize);
        mTvExpand.setTextColor(collapseExpandTextColor);
        mTvExpand.getPaint().setTextSize(collapseExpandTextSize);

        //设置收起展开位置：左或者右
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.gravity = grarity;
        mTvExpand.setLayoutParams(lp);
    }

    /**
     * @Description 点击事件
     */
    @Override
    public void onClick(View view) {
        if (mLlExpand.getVisibility() != View.VISIBLE) {
            return;
        }

        mCollapsed = !mCollapsed;
        // 修改收起/展开图标、文字
        setDrawbleAndText();
        // 保存位置状态
        if (mCollapsedStatus != null) {
            mCollapsedStatus.put(mPosition, mCollapsed);
        }

        // 执行展开/收起动画
        mAnimating = true;
        ValueAnimator valueAnimator;
        if (mCollapsed) {
            valueAnimator = new ValueAnimator().ofInt(getHeight(), mCollapsedHeight);
        } else {
            mCollapsedHeight = getHeight();
            valueAnimator = new ValueAnimator().ofInt(getHeight(), getHeight() + mTextHeightWithMaxLines - mTvContent.getHeight());
        }

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int animatedValue = (int) valueAnimator.getAnimatedValue();
                mTvContent.setMaxHeight(animatedValue - mMarginBetweenTxtAndBottom);
                getLayoutParams().height = animatedValue;
                requestLayout();
            }
        });

        valueAnimator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                // 动画结束后发送结束的信号，清除动画标志
                mAnimating = false;
                // 通知监听
                if (mListener != null) {
                    mListener.onExpandStateChanged(mTvContent, !mCollapsed);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        valueAnimator.setDuration(mAnimationDuration);
        valueAnimator.start();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // 当动画还在执行状态时，拦截事件，不让child处理
        return mAnimating;
    }

    /**
     * @Description 重新测量
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 如果没有变化，测量并返回
        if (!mRelayout || getVisibility() == View.GONE) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        mRelayout = false;

        // Setup with optimistic case
        // i.e. Everything fits. No button needed
        mLlExpand.setVisibility(View.GONE);
        mTvContent.setMaxLines(Integer.MAX_VALUE);

        // Measure
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //如果内容真实行数小于等于最大行数，不处理
        if (mTvContent.getLineCount() <= mMaxCollapsedLines) {
            return;
        }
        // 获取内容tv真实高度（含padding）
        mTextHeightWithMaxLines = getRealTextViewHeight(mTvContent);

        // 如果是收起状态，重新设置最大行数
        if (mCollapsed) {
            mTvContent.setMaxLines(mMaxCollapsedLines);
        }
        mLlExpand.setVisibility(View.VISIBLE);

        // Re-measure with new setup
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (mCollapsed) {
            // Gets the margin between the TextView's bottom and the ViewGroup's bottom
            mTvContent.post(new Runnable() {
                @Override
                public void run() {
                    mMarginBetweenTxtAndBottom = getHeight() - mTvContent.getHeight();
                }
            });
            // 保存这个容器的测量高度
            mCollapsedHeight = getMeasuredHeight();
        }
    }

    /**
     * @Description 获取内容tv真实高度（含padding）
     */
    private static int getRealTextViewHeight(TextView textView) {
        int textHeight = textView.getLayout().getLineTop(textView.getLineCount());
        int padding = textView.getCompoundPaddingTop() + textView.getCompoundPaddingBottom();
        return textHeight + padding;
    }

    /**
     * @Description 设置收起展开图标位置和文字
     */
    private void setDrawbleAndText() {
        if (Gravity.LEFT == drawableGrarity && mIsDisplayIcon) {
            mIvExpandLeft.setImageDrawable(mCollapsed ? mCollapseDrawable : mExpandDrawable);
            mIvExpandLeft.setVisibility(View.VISIBLE);
            mIvExpandRight.setVisibility(View.GONE);
        } else if (mIsDisplayIcon) {
            mIvExpandRight.setImageDrawable(mCollapsed ? mCollapseDrawable : mExpandDrawable);
            mIvExpandRight.setVisibility(View.VISIBLE);
            mIvExpandLeft.setVisibility(View.GONE);
        }
        mTvExpand.setText(mCollapsed ? getResources().getString(R.string.expand) : getResources().getString(R.string.collapse));
    }

    /******************************************暴露给外部调用方法******************************************/

    /**
     * @Description 设置收起/展开监听
     */
    public void setOnExpandStateChangeListener(OnExpandStateChangeListener listener) {
        mListener = listener;
    }

    /**
     * @Description 设置显示内容
     */
    public void setText(CharSequence text) {
        mRelayout = true;
        mTvContent.setText(text);
        setVisibility(TextUtils.isEmpty(text) ? View.GONE : View.VISIBLE);
    }

    /**
     * @Description 设置内容区域点击，默认展开/收起区域点击
     */
    public void setListener() {
        mTvContent.setOnClickListener(this);
    }

    /**
     * @Description 设置内容，列表情况下，带有保存位置收起/展开状态
     */
    public void setText(CharSequence text, int position) {
        mPosition = position;
        //获取状态，如无，默认是true:收起
        mCollapsed = mCollapsedStatus.get(position, true);
        clearAnimation();
        //设置收起/展开图标和文字
        setDrawbleAndText();
        mTvExpand.setText(mCollapsed ? getResources().getString(R.string.expand) : getResources().getString(R.string.collapse));

        setText(text);
        getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        requestLayout();
    }

    /**
     * @Description 获取内容
     */
    public CharSequence getText() {
        if (mTvContent == null) {
            return "";
        }
        return mTvContent.getText();
    }

    /**
     * @Description 定义状态改变接口
     */
    public interface OnExpandStateChangeListener {
        /**
         * @param textView   - TextView being expanded/collapsed
         * @param isExpanded - true if the TextView has been expanded
         */
        void onExpandStateChanged(TextView textView, boolean isExpanded);
    }
}