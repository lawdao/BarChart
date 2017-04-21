# BarChart
条形图及加入动画
#闲话

Android 图表的绘制，无非是view的绘制，只有掌握了view的绘制流程，那么一个条形图，也没有什么可难绘制的，那为什么会有人觉得比较难呢，其实是自己内心里惧怕他，于是每次出现图表需求，想都不用想就选择第三方的，也就是别人写好的，当然，这也没有什么，大家都会用的，也不只是你一个人在用，目前在github上star最多的可能就是MPAndroidChart了，做的的确很强大，但是再强大，有时候也会有那么一丢丢的不符合我们自己的需求，这个时候，真是让人哭笑不得啊，用的话，又不能达到产品的需求，不用吧，自己好像又没有什么思路，于是僵住了。

我花了点时间大致的看了下MPAndroidChart源码，的确内容很多，今天，就用自己的思想以及MPAndroidChart的思想相结合来写一个比较简单的条形图，这样也是对绘制view的一次学习吧

#效果

![效果](http://ww3.sinaimg.cn/large/006tKfTcgy1feuhdgxuufg30940gctau.gif)


图片看起来有点失真，还伴随点卡顿，这是由于录像是电脑和手机连接不稳定造成的，真实的动画是不会有任何卡顿的。

#理清思路

首先我们得理清思路，如何才能绘制出一个条形图，不能连思路都没有就去做，这样真的很难下手。

##1. 初始化操作

首先，图表是自定义view，继承自view，所以该重写的方法都要重写。

1. 当图表创建时，我们都需要准备什么，第一个当然是画笔Paint了，这个时候我们只需要对画笔进行初始化，什么颜色啊，填充方式啊等等。
2. 除了初始化画笔Paint外，我们还需要初始化的，就是动画了，因为我们需要给条形图加入动画，这个时候，问题来了，动画和条形图如何结合起来？
3. 能让条形图动起来的动画当然是ValueAnimator或者ObjectAnimator了，这两个动画其实是一个东西，最终都是ValueAnimator，所以用哪个其实都可以实现，我用的是ObjectAnimator，因为MPAndroidChart也用的是ObjectAnimator。
4. 究竟动画怎么才能和条形图结合呢？


 

```
 private void init() {

        //初始化动画
        mAnimator = new ChartAnimator(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                postInvalidate();
            }
        });

        mBound = new Rect();


        //柱子画笔
        mChartPaint = new Paint();
        mChartPaint.setAntiAlias(true);
        mChartPaint.setColor(Color.parseColor(chartColor));


        //线画笔
        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setColor(Color.parseColor(lineColor));

        //x纵坐标 画笔
        textXpaint = new Paint();
        textXpaint.setAntiAlias(true);
        textXpaint.setTextSize(27f);
        textXpaint.setTextAlign(Paint.Align.CENTER);
        textXpaint.setColor(Color.parseColor(textColor));

        //Y纵坐标 画笔
        textYpaint = new Paint();
        textYpaint.setAntiAlias(true);
        textYpaint.setTextSize(28f);
        textYpaint.setTextAlign(Paint.Align.LEFT);
        textYpaint.setColor(Color.parseColor(textColor));

        //无数据时的画笔
        noDataPaint = new Paint();
        noDataPaint.setAntiAlias(true);
        noDataPaint.setColor(Color.parseColor(noDataColor));
        noDataPaint.setStyle(Paint.Style.FILL);
    }
```


##2. 动画和条形图的结合

大家都知道ValueAnimator动画，是将两个数经过计算之后，会得到一系列的数值，这些数值都会在这两个值得区间，那么这个时候，就能将生成的这些值和条形图的高度联系到一起，ValueAnimator的值是从0到1的，生成的全是Float类型的值，可以理解为百分数，也就是从0%到100%，这个时候条形图的动画也就能实现了，每个条形图的高度都从0开始绘制，一直绘制到他的真实高度，绘制完成后，动画自然也就结束了，条形图的高度占这个view的高度的百分比，也就是占整个图表控件的高度的百分比是可以算出来的。

##3. 测量高度


1. 在onLayout方法里，可以得到view的高度和宽度，因为我们需要在后面计算每个条形图占整个控件高度的百分比

2. 在onLayout方法里也需要计算条形图的宽度和间隙，以及横纵坐标起始位置坐标


```
 @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mWidth = getWidth();
        mHeight = getHeight() - paddingTop;

        chartWidth = mWidth - outSpace;

        //每个柱子宽度
        barWidth = (int) (chartWidth * barPercent);
        interval = (int) (chartWidth * intevalPercent);

        //所有柱子宽度 之和
        allBarwidth = horizontalList.size() * barWidth;
        //所有间隔宽 之和
        allInteval = (horizontalList.size() - 1) * interval;

        //所有柱子和间隔的宽度
        allChartWidth = allBarwidth + allInteval;

        //柱子开始的横坐标

        startChart = outSpace + (chartWidth / 2f - allChartWidth / 2f);
        //横坐标

        textStart = startChart + (barWidth / 2f);

    }
```


##5. 开始绘制

1. view的绘制全在onDraw方法里，这个时候就用到onDraw方法中的canvas了，条形图其实也就是矩形，所以也很好绘制，这个时候，我们需要了解的就是canvas的方法，他都能绘制些什么，方法中的参数又是什么意思并且代表着什么，只有搞清楚了这些，我们才能正式开始下手绘制，否则，我们还是无从下手。
2. canvas.drawRect()，这个方法其实才是我们重点要掌握的，他就是绘制矩形的方法，首先，你需要知道，他的参数的意义
3. 他需要的参数为：(float left, float top, float right, float bottom,Paint paint ),乍一看，参数太多，看都不想看了，别着急，他很简单的，只不过就是坐标而已，上下左右嘛，这有什么难的，那么这又分别代表着什么呢，搞清楚这些我们才好下手呀。
4. left表示矩形的左边到view最左边的距离，而不是屏幕的左边，这个要搞清楚，top也就表示着矩形的上边到view的顶部距离，而不是屏幕顶部，right是矩形的右边到view的左边距离，bottom是矩形的下边到view的顶部的距离
5. 只要你搞清楚了上面这些，绘制图表不再是什么难题了，剩下的只是坐标的计算和柱子宽度，高度的计算了
6. 下面是ondraw方法


 

```
 @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float lineInterval = (mHeight - bottomHeight) / 4f; //横线之间的间距  纵向
        float textHeight = mHeight + paddingTop - bottomHeight;//横坐标高度

        //画线
        drawLine(canvas, lineInterval, textHeight);

        //画纵坐标
        drawYtext(canvas, lineInterval, textHeight);

        //画横坐标
        float textTempStart = textStart;

        drawXtext(canvas, textTempStart);


        float chartTempStart = startChart;

        float size = (mHeight - bottomHeight) / 100f; //比例

        //画柱子
        drawBar(canvas, chartTempStart, size);

    }
```


#需要理解的地方

1. 在view进行初始化的时候，我们初始化了一个ChartAnimator，其实是ObjectAnimator将0-1内生成的所有数字给了他，方便在view里得到这些变化的值。
2. 在初始化的时候，我们对动画做了监听，当他更新的时候，我们就调用了一下postInvalidate()，这句话就是让view执行ondraw方法，通过变量来增加条形图的高度。
3. 最后还是条形图宽度的问题，我是这样设计的，当条形图的数量大于6时，那么所有的条形图的宽度将平分整个view的宽度，然后将计算后的值得30%作为条形图的间隔，剩下的70%的宽度就是条形图的宽度，绘制是从左向右绘制，当条形图的数量小于6或者等于6时，我将所有的条形图绘制到了整个表的中间，宽度怎么平分的大家可以查看源码，具体的就不说了

#关注我

1. 扫描下面的二维码，即可关注AppCode公众号


![AppCode](https://ww2.sinaimg.cn/large/006tNbRwgy1fdj5g7wwl8j3076076aaj.jpg)
