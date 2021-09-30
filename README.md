# ScaleBar
这是一个自定义的刻度条,顾名思义,就是展示、标记、选中刻度的进度条;你可以通过属性定制控件的颜色,文字的大小,滑块和背景的比值等.下面是我使用的两种场景
![image](https://github.com/FishInWater-1999/GithubUseTest/blob/master/bac_3.jpg)
### 1. 属性
| 属性名      | 使用场景                                              | 传参类型 |
| -------------- | --------------------------------------------------------- | ---------- |
| bgColor        | 修改未选中区域的颜色                            | 颜色资源id |
| selectedColor  | 选中的颜色(滑块左侧的颜色)                    | 颜色资源id |
| sliderColor    | 滑块的颜色                                           | 颜色资源id |
| bar_proportion | 滑块半径和背景高度的比值,大于1则滑块超出滑槽滑动(默认1.2) | float      |
| lowText        | 左侧滑槽中的展示文字                            | String     |
| highText       | 右侧展示文字,不设置则不展示                  | String     |
| values         | 刻度数组|String数组类型的资源Id(不会用的见下方图片) | String[]   |
| slideTextSize  | 滑块上的文字大小,不设置会自动计算(取值氛围12px~112px) | Int        |
| hintTextSize   | 滑槽中的文字大小,不设置会自动计算         | Int        |
| isShowScale    | 滑块上是否展示选中的刻度文字                | Boolean    |
| slideProgress  | 选中刻度                                   | Int        |
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
        app:highText="高"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/bar2"
        app:lowText="低"
        app:values="@array/sizes" />
```
#### 刻度数组可通过如下方式添加到项目
![image](https://github.com/FishInWater-1999/GithubUseTest/blob/master/bac_3.jpg)
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

