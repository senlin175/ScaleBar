# ScaleBar
这是一个自定义的刻度条,顾名思义,就是展示、标记、选中刻度的进度条;你可以通过属性定制控件的颜色,文字的大小,滑块和背景的比值等.下面是我使用的两种场景
![image](https://github.com/senlin175/ScaleBar/blob/main/resource/1.gif)
### 1. 属性
| 属性名      | 使用场景                                              | 传参类型 |
| -------------- | --------------------------------------------------------- | ---------- |
| bgColor        | 修改未选中区域的颜色     默认值为灰色(#ffF5F2F3)                       | 颜色资源id |
| selectedColor  | 选中的颜色(滑块左侧的颜色)          默认值为亮绿(#ff99cc00)           | 颜色资源id |
| sliderColor    | 滑块的颜色              默认值为白色                          | 颜色资源id |
| bar_proportion | 滑块半径和背景高度的比值,大于1则滑块超出滑槽滑动(默认1.0) | float      |
| lowText        | 左侧滑槽中的展示文字                            | String     |
| highText       | 右侧展示文字,不设置则不展示                  | String     |
| scales         | 刻度数组|String数组类型的资源Id(不会用的见下方图片)  默认值是{"OFF","ON"}  | String[]   |
| slideTextSize  | 滑块上的文字大小,不设置会自动计算(取值氛围12px~112px) | Int        |
| hintTextSize   | 滑槽中的文字大小,不设置会自动计算         | Int        |
| isShowScale    | 滑块上是否展示选中的刻度文字   刻度数组长度为2是默认为false  其他情况默认true               | Boolean    |
| slideProgress  | 选中刻度                                   | Int        |
| isDownToMove   | 触摸事件中的 DOWM事件  是否将滑块移动到触摸点   刻度数组长度为2是默认为true  其他情况默认false                                   | Boolean        |
### 2. 使用示例
```
        <com.guhe.scalebar.ScaleBar
        android:id="@+id/bar3"
        android:layout_width="300dp"
        android:layout_height="70dp"
        android:layout_marginTop="10dp"
        android:background="@color/colorAccent"
        android:paddingStart="20dp"
        android:paddingEnd="20dp"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/bar2"
        app:bar_proportion="1.5"
        app:bgColor="@android:color/darker_gray"
        app:highText="高"
        app:hintTextSize="20sp"
        app:isShowScale="true"
        app:lowText="低"
        app:selectedColor="@android:color/holo_red_dark"
        app:slideProgress="3"
        app:slideTextSize="13sp"
        app:sliderColor="@android:color/holo_orange_light"
        app:scales="@array/sizes" />

```
#### 刻度数组可通过如下方式添加到项目
![image](https://github.com/senlin175/ScaleBar/blob/main/resource/2.png)
### 3. set方法
#### set方法基本与属性对应,重点看一下监听方法即可
```
/**
     * 设置监听
     *
     * @param onScaleSlideListener 滑块滑动监听器
     */
    public void setOnScaleSlideListener(OnScaleSlideListener onScaleSlideListener) {
        this.onScaleSlideListener = onScaleSlideListener;
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
```
#### 设置进度值的方法有两个方法,推荐使用第二种
```
    /**
     * 设置刻度值
     * ps:   不推荐使用,因为要遍历数组,如果数组过大可能有性能问题
     *
     * @param scale 刻度值
     */
    public void SetProgress(String scale) {
        //具体实现
    }

    /**
     * 设置刻度值
     *
     * @param position 刻度值在刻度数组中的位置    0~scales.length-1
     */
    public void SetProgress(int position) {
       //具体实现
    }
```

