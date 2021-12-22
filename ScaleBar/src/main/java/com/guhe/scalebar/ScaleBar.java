package com.guhe.scalebar;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class ScaleBar extends View {
    private int width;
    private int height;
    private Paint paint;
    //滑块直径和背景高度的比值
    private float proportion = 1.0f;
    //最底层的背景色        lowText的默认字体颜色
    private int bgColor;
    //选中区域的背景色      highText的默认字体颜色
    private int selectedColor;
    //滑块的颜色
    private int slideColor;
    //左侧文字
    private String lowText;
    //右侧文字
    private String highText;
    private float slideRadius;
    private float bgRadius;
    //未选中区域的path
    private Path bgPath;
    private Point point;
    private Path slidePath;
    private RectF slideRectF;
    private Region region;
    private float startX;
    private boolean isSlide = false;
    private int minx;
    private int maxX;
    private Rect textRect;
    private Paint textPaint;
    // Default minimum size for auto-sizing text in scaled pixels.
    private static final int DEFAULT_AUTO_SIZE_MIN_TEXT_SIZE_IN_SP = 12;
    // Default maximum size for auto-sizing text in scaled pixels.
    private static final int DEFAULT_AUTO_SIZE_MAX_TEXT_SIZE_IN_SP = 112;
    private float baseLineY;
    //刻度值数组
    private String[] scales;
    private final String[] DEFAULT_SCALES = new String[]{"OFF", "ON"};
    //选中的刻度值
    private String selectedText;
    //选中的刻度值在刻度数组中的位置
    private int selectedPosition;
    //总位移       相对于原始点的位移值
    private int shift = 0;
    private PaintFlagsDrawFilter paintFlagsDrawFilter;
    //lowText/highText的文字大小          文字的大小会根据最小到最大的值区间寻找最合适的大小
    private int hintTextSize;
    //最小文字大小    默认为12px
    private int minTextSize = DEFAULT_AUTO_SIZE_MIN_TEXT_SIZE_IN_SP;
    //最小文字大小    默认为112px
    private int maxTextSize = DEFAULT_AUTO_SIZE_MAX_TEXT_SIZE_IN_SP;
    //是否展示滑块上的选中刻度  默认为展示
    private boolean isShowScale = true;
    private float slideTextY;
    private OnScaleSlideListener onScaleSlideListener;
    //滑块上的文字大小          文字的大小会根据最小到最大的值区间寻找最合适的大小
    private float slideTextSize;
    //两个刻度之间的间距
    private int spacing = 0;
    //用来处理点击事件的参数
    private int mTouchSlop;
    private float downX;
    private float downY;
    private RectF pathRectF;
    //选中区域的path
    private Path selectedPath;
    private int pdTop;
    private int pdBottom;
    private int pdLeft;
    private int pdRight;
    private int[] sizes;
    private boolean isDownToMove = false;

    public ScaleBar(Context context) {
        this(context, null);
    }

    public ScaleBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScaleBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initValue(context, attrs);
    }

    private void initValue(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ScaleBar);
        bgColor = typedArray.getResourceId(R.styleable.ScaleBar_bgColor, -1);
        bgColor = bgColor == -1 ? Color.parseColor("#ffF5F2F3") : ContextCompat.getColor(getContext(), bgColor);
        slideColor = typedArray.getResourceId(R.styleable.ScaleBar_sliderColor, -1);
        slideColor = slideColor == -1 ? Color.WHITE : ContextCompat.getColor(getContext(), slideColor);
        selectedColor = typedArray.getResourceId(R.styleable.ScaleBar_selectedColor, -1);
        selectedColor = selectedColor == -1 ? Color.parseColor("#ff99cc00") : ContextCompat.getColor(getContext(), selectedColor);
        String scaleLow = typedArray.getString(R.styleable.ScaleBar_lowText);
        String scaleHigh = typedArray.getString(R.styleable.ScaleBar_highText);
        lowText = scaleLow == null ? "" : scaleLow;
        highText = scaleHigh == null ? "" : scaleHigh;
        proportion = typedArray.getFloat(R.styleable.ScaleBar_bar_proportion, 1.0f);
        slideTextSize = typedArray.getDimensionPixelSize(R.styleable.ScaleBar_slideTextSize, -1);
        hintTextSize = typedArray.getDimensionPixelSize(R.styleable.ScaleBar_hintTextSize, -1);
        isShowScale = typedArray.getBoolean(R.styleable.ScaleBar_isShowScale, true);
        selectedPosition = typedArray.getInteger(R.styleable.ScaleBar_slideProgress, 0);
        isDownToMove = typedArray.getBoolean(R.styleable.ScaleBar_isDownToMove, false);

        //获取刻度值数组
        final int values = typedArray.getResourceId(R.styleable.ScaleBar_scales, 0);
        if (values > 0) {
            scales = typedArray.getResources().getStringArray(values);
        } else {
            scales = DEFAULT_SCALES;
        }
        if (scales.length == 2) {
            isDownToMove = true;
            isShowScale = false;
        }

        typedArray.recycle();

        slidePath = new Path();
        slideRectF = new RectF();
        region = new Region();
        bgPath = new Path();
        point = new Point();
        textRect = new Rect();
        textPaint = new TextPaint();
        pathRectF = new RectF();
        selectedPath = new Path();
        paintFlagsDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();

        int size = maxTextSize - minTextSize + 1;
        sizes = new int[size];
        int tmp = minTextSize;
        for (int i = 0; i < size; i++) {
            sizes[i] = tmp++;
        }

        shift = 0;
        //默认选中第一个刻度
        if (scales != null && scales.length > 0) {
            selectedText = scales[0];
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.width = w;
        this.height = h;

        if (width != 0 && height != 0) {
            width -= 10;
            height -= 10;
            initAll();
        }
    }

    private void initAll() {
        initPaint();
        initPathAndSize();
        postInvalidate();
    }

    private void initPadding() {
        pdTop = getPaddingTop();
        pdBottom = getPaddingBottom();
        int pdStart = getPaddingStart();
        pdLeft = getPaddingLeft();
        pdRight = getPaddingRight();
        int pdEnd = getPaddingEnd();
        pdLeft = Math.max(pdLeft, pdStart);
        pdRight = Math.max(pdEnd, pdRight);
    }

    private void initPathAndSize() {
        initPadding();

        if (width == 0 || height == 0) return;

        if (proportion > 1) {
            slideRadius = (height - pdTop - pdBottom) / 2f;
            bgRadius = slideRadius / proportion;
        } else {
            bgRadius = (height - pdTop - pdBottom) / 2f;
            slideRadius = bgRadius * proportion;
        }

        minx = (int) (Math.max(slideRadius, bgRadius)) + pdLeft;
        maxX = width - minx - pdRight + pdLeft;

        point.x = minx;
        point.y = (height + pdTop - pdBottom) / 2;

        if (hintTextSize == -1) {
            hintTextSize = getTextSize("字", bgRadius, 0.7f, 0.8f);
        }
        if (hintTextSize != -1) {
            textPaint.setTextSize(hintTextSize);
            baseLineY = point.y + Math.abs(textPaint.ascent() + textPaint.descent()) / 2;
        }

        if (scales != null && scales.length < 2) {
            Log.e("ScaleBar", "IllegalArgumentException: The scale value array must be greater than or equal to 2");
        }

        if (scales != null && selectedPosition > scales.length - 1) {
            Log.e("ScaleBar", "IllegalArgumentException: The selected position cannot exceed the range of the scale array");
        }

        if (scales != null && scales.length > 1) {
            spacing = Math.round((maxX - minx) / (float) (scales.length - 1));
            if (slideTextSize == -1) {
                String tmpText = "字";
                for (String scale : scales) {
                    if (scale != null && scale.length() > tmpText.length()) {
                        tmpText = scale;
                    }
                }
                slideTextSize = getTextSize(tmpText, slideRadius, 1.4f, 1.5f);
            }

            if (selectedPosition >= 0 && selectedPosition <= scales.length - 1) {
                shift = selectedPosition * spacing;
                point.x = minx + shift;
                selectedText = scales[selectedPosition];
            }
        }

        if (slideTextSize != -1) {
            textPaint.setTextSize(slideTextSize);
            slideTextY = point.y + Math.abs(textPaint.ascent() + textPaint.descent()) / 2;
        }

        initPath();
    }

    private void initPath() {
        int midX = minx + shift;
        int midY = point.y;

        bgPath.reset();
        bgPath.moveTo(maxX, midY - bgRadius);
        pathRectF.setEmpty();
        pathRectF.set(maxX - bgRadius, midY - bgRadius, maxX + bgRadius, midY + bgRadius);
        bgPath.arcTo(pathRectF, 270, 180);
        bgPath.lineTo(midX, midY + bgRadius);
        pathRectF.setEmpty();
        pathRectF.set(midX - bgRadius, midY - bgRadius, midX + bgRadius, midY + bgRadius);
        bgPath.arcTo(pathRectF, 90, -180);
        bgPath.close();


        selectedPath.reset();
        selectedPath.moveTo(midX, midY - bgRadius);
        pathRectF.setEmpty();
        pathRectF.set(midX - bgRadius, midY - bgRadius, midX + bgRadius, midY + bgRadius);
        selectedPath.arcTo(pathRectF, 270, 180);
        selectedPath.lineTo(minx, midY + bgRadius);
        pathRectF.setEmpty();
        pathRectF.set(minx - bgRadius, midY - bgRadius, minx + bgRadius, midY + bgRadius);
        selectedPath.arcTo(pathRectF, 90, 180);
        selectedPath.close();
    }

    private boolean isArea(float x, float y) {
        slideRectF.setEmpty();
        slidePath.reset();
        slidePath.addCircle(point.x, point.y, slideRadius, Path.Direction.CCW);
        slidePath.computeBounds(slideRectF, true);
        region.setEmpty();
        region.setPath(slidePath, new Region((int) slideRectF.left, (int) slideRectF.top, (int) slideRectF.right, (int) slideRectF.bottom));
        return region.contains((int) x, (int) y);
    }

    private void initPaint() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(bgColor);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStrokeWidth(1);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.BEVEL);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);

        textPaint.setAntiAlias(true);
        textPaint.setDither(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int wSize = MeasureSpec.getSize(widthMeasureSpec);
        int hSize = MeasureSpec.getSize(heightMeasureSpec);
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);

        int h = 0;
        int w = 0;

        switch (mode) {
            case MeasureSpec.EXACTLY:
            case MeasureSpec.AT_MOST:
                w = wSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                w = 900;
                break;
            default:
                break;
        }
        switch (hMode) {
            case MeasureSpec.EXACTLY:
            case MeasureSpec.AT_MOST:
                h = hSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                h = 90;
                break;
            default:
                break;
        }

        setMeasuredDimension(w, h);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();

                if (isDownToMove) {
                    if (onScaleSlideListener != null) {
                        onScaleSlideListener.onBeforeSliding(selectedPosition, selectedText);
                    }
                    startX = point.x;
                    moveToChangeView(downX);
                    isSlide = true;
                } else {
                    startX = event.getX();
                    isSlide = isArea(startX, downY);
                }

                if (!isDownToMove && isSlide && onScaleSlideListener != null) {
                    onScaleSlideListener.onBeforeSliding(selectedPosition, selectedText);
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                if (!isSlide) return super.onTouchEvent(event);
                float endX = event.getX();

                moveToChangeView(endX);
                return true;
            case MotionEvent.ACTION_UP:
                if (isSlide) endSlide();

                float upX = event.getX();
                float upY = event.getY();

                if (Math.abs(upX - downX) < mTouchSlop && Math.abs(upY - downY) < mTouchSlop) {
                    return performClick();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                if (isSlide) endSlide();
                break;
        }
        return super.onTouchEvent(event);
    }

    private void moveToChangeView(float endX) {
        float distance = endX - startX;
        point.x += distance;
        if (point.x < minx) {
            point.x = minx;
            shift = 0;
        } else if (point.x > maxX) {
            point.x = maxX;
            shift = maxX - minx;
        } else {
            shift += distance;
        }
        initPath();

        if (scales != null && scales.length > 0) {
            int position = Math.round(shift / (float) spacing);
            String text = scales[position];
            if (!text.equals(selectedText)) {
                selectedPosition = position;
                selectedText = text;
                if (onScaleSlideListener != null) {
                    onScaleSlideListener.onSliding(selectedPosition, selectedText);
                }
            }
        }
        startX = endX;

        postInvalidate();
        getParent().requestDisallowInterceptTouchEvent(true);
    }

    private void endSlide() {
        if (scales != null && scales.length > 0) {
            float offset = shift % spacing;

            if (offset > spacing >> 1) {
                point.x += spacing - offset;
            } else {
                point.x -= offset;
            }
            shift = point.x - minx;
            initPath();
            selectedPosition = Math.round(shift / (float) spacing);
            selectedText = scales[selectedPosition];
            if (onScaleSlideListener != null) {
                onScaleSlideListener.onEndSliding(selectedPosition, selectedText);
            }
            postInvalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(5, 5);

        //抗锯齿
        canvas.setDrawFilter(paintFlagsDrawFilter);

        //重置画笔
        paint.reset();
        paint.setDither(true);//添加抖动
        paint.setStrokeWidth(1);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.BEVEL);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);

        //绘制背景
        paint.setColor(bgColor);
        canvas.drawPath(bgPath, paint);

        //绘制已选择区域
        paint.setColor(selectedColor);
        canvas.drawPath(selectedPath, paint);

        //绘制文字(lowText&HighText)
        drawText(canvas);

        //绘制按钮
        paint.setColor(slideColor);
        paint.setShadowLayer(5, 0, 0, Color.GRAY);
        canvas.drawCircle(point.x, point.y, slideRadius, paint);

        //绘制按钮上的选中文字
        if (selectedText != null && !selectedText.isEmpty() && isShowScale && slideTextY != 0) {
            textPaint.setColor(selectedColor);
            textPaint.setTextSize(slideTextSize);
            canvas.drawText(selectedText, point.x - textPaint.measureText(selectedText) / 2, slideTextY, textPaint);
        }

        canvas.restore();
    }

    private void drawText(Canvas canvas) {
        if (lowText.isEmpty() && highText.isEmpty()) return;

        textPaint.setTextSize(hintTextSize);
        if (!lowText.isEmpty()) {
            textPaint.setColor(bgColor);
            canvas.drawText(lowText, minx - bgRadius * 0.3f, baseLineY, textPaint);
        }
        if (!highText.isEmpty()) {
            textPaint.setColor(selectedColor);
            canvas.drawText(highText, maxX - textPaint.measureText(highText) + bgRadius * 0.3f, baseLineY, textPaint);
        }
    }

    /**
     * @param size The desired size in the given units.
     * @return 返回px
     */
    private float getTextSizePx(float size) {
        Context c = getContext();
        Resources r;

        if (c == null) {
            r = Resources.getSystem();
        } else {
            r = c.getResources();
        }

        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, size, r.getDisplayMetrics());
    }

    /**
     * 使用二分算法从TextSize数组选中合适的文字大小
     *
     * @param redius 文字可展示的宽高
     * @return 返回合适的文字大小
     */
    private int getTextSize(String text, float redius, float low, float high) {
        if (text.isEmpty()) {
            text = "字";
        }
        textRect.setEmpty();

        int left = 0;
        int right = sizes.length - 1;

        while (left <= right) {
            int mid = (right + left) / 2;
            int textSize = sizes[mid];
            textPaint.setTextSize(textSize);
            textRect.setEmpty();
            textPaint.getTextBounds(text, 0, text.length(), textRect);
            int maxIndex = Math.max(textRect.height(), textRect.width());
            if (maxIndex < high * redius && maxIndex > low * redius) {
                return textSize;
            } else if (maxIndex < high * redius) {
                if (mid == sizes.length - 1) return textSize;
                left = mid + 1;
            } else if (maxIndex > low * redius) {
                if (mid == 0) return textSize;
                right = mid - 1;
            }
        }
        return -1;
    }

    /**
     * 设置提示文字大小
     *
     * @param hintTextSize 单位sp
     */
    public void setHintTextSize(int hintTextSize) {
        this.hintTextSize = Math.round(getTextSizePx(hintTextSize));
        initPathAndSize();
        postInvalidate();
    }

    /**
     * 设置滑块文字大小
     *
     * @param slideTextSize 单位sp
     */
    public void setSlideTextSize(int slideTextSize) {
        this.slideTextSize = Math.round(getTextSizePx(slideTextSize));
        initPathAndSize();
        postInvalidate();
    }

    /**
     * 是否展示滑块上的选中刻度  默认为展示
     *
     * @param showScale 默认为展示
     */
    public void setShowScale(boolean showScale) {
        isShowScale = showScale;
        postInvalidate();
    }

    /**
     * 滑块直径和背景高度的比值
     *
     * @param proportion 默认1.2
     */
    public void setProportion(float proportion) {
        this.proportion = proportion;
        initPathAndSize();
        postInvalidate();
    }

    /**
     * 设置刻度数组
     *
     * @param scales 刻度数组
     */
    public void setScales(String[] scales) {
        if (scales == null) return;
        if (scales.length < 2) {
            Log.e("ScaleBar", "IllegalArgumentException: The scale value array must be greater than or equal to 2");
            return;
        }
        this.scales = scales;
        if (scales.length == 2) {
            isDownToMove = true;
            isShowScale = false;
        }
        //默认选中第一个刻度
        selectedText = scales[0];
        selectedPosition = 0;
        initPathAndSize();
        postInvalidate();
    }

    /**
     * 设置监听
     *
     * @param onScaleSlideListener 滑块滑动监听器
     */
    public void setOnScaleSlideListener(OnScaleSlideListener onScaleSlideListener) {
        this.onScaleSlideListener = onScaleSlideListener;
    }

    public void setLowText(String lowText) {
        this.lowText = lowText;
    }

    public void setHighText(String highText) {
        this.highText = highText;
    }

    /**
     * 设置刻度值
     * ps:   不推荐使用,因为要遍历数组,如果数组过大可能有性能问题
     *
     * @param scale 刻度值
     */
    public void setSlideProgress(String scale) {
        if (scales != null && scales.length > 0) {
            for (int i = 0; i < scales.length; i++) {
                String tmpText = scales[i];
                if (scale.equals(tmpText)) {
                    selectedPosition = i;
                    initPathAndSize();
                    postInvalidate();
                    return;
                }
            }
        }
    }

    /**
     * 设置刻度值
     *
     * @param position 刻度值在刻度数组中的位置    0~scales.length-1
     */
    public void setSlideProgress(int position) {
        if (scales == null || scales.length < 1) return;
        if (selectedPosition > scales.length - 1) {
            Log.e("ScaleBar", "IllegalArgumentException: The selected position cannot exceed the range of the scale array");
            return;
        }
        selectedPosition = position;
        initPathAndSize();
        postInvalidate();
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom);
        initPathAndSize();
        postInvalidate();
    }

    @Override
    public void setPaddingRelative(int start, int top, int end, int bottom) {
        super.setPaddingRelative(start, top, end, bottom);
        initPathAndSize();
        postInvalidate();
    }

    public void setDownToMove(boolean downToMove) {
        isDownToMove = downToMove;
    }

    public interface OnScaleSlideListener {
        /**
         * 开始滑动前
         *
         * @param position      当前刻度在数组中的位置     取值范围    0~scales.length-1
         * @param selectedScale 当前的刻度
         */
        void onBeforeSliding(int position, String selectedScale);

        /**
         * 滑动中
         *
         * @param position      当前刻度在数组中的位置     取值范围    0~scales.length-1
         * @param selectedScale 当前的刻度
         */
        void onSliding(int position, String selectedScale);

        /**
         * 结束滑动
         *
         * @param position      当前刻度在数组中的位置     取值范围    0~scales.length-1
         * @param selectedScale 当前的刻度
         */
        void onEndSliding(int position, String selectedScale);
    }
}
