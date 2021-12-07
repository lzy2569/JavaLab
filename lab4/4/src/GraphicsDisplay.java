import java.awt.*;
import	java.awt.font.FontRenderContext;
import	java.awt.geom.Ellipse2D;
import 	java.awt.geom.GeneralPath;
import	java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import	java.awt.geom.Point2D;
import	java.awt.geom.Rectangle2D;
import	javax.swing.JPanel;
@SuppressWarnings("serial")
public class GraphicsDisplay extends JPanel
{
    private Double[][] graphicsData;						// 用于绘图的点坐标列表
    private boolean showAxis = true;						// 标记设置图表显示规则的变量
    private boolean showMarkers = true;
    private boolean showGraphics = true;

    private double minX;									// 要显示的空间范围的边界
    private double maxX;
    private double minY;
    private double maxY;
    private double scale;									// 二手显示比例
    private BasicStroke graphicsStroke;						// 不同风格的线条画
    private BasicStroke axisStroke;
    private BasicStroke markerStroke;
    private BasicStroke lineGraphics;
    private Font axisFont;									// 用于显示标签的各种字体
    public GraphicsDisplay()
    {
        setBackground(Color.WHITE);							// 显示区域的背景色为白色
        // 构建绘图中使用的必要对象：

        //BAsicStroke.CAP_BUTT - 线作为“树桩”，在两侧
        //BasicStroke.JION_ROUND - 半圆完成线闭合, BAsicStorke.JOIN_MiTER - 用“尖刺”结束行

        // 图形笔 - 绘制形状的边界
        //线宽 2.0f - 两点，10.0f - 闭合角 10 度
        graphicsStroke = new BasicStroke(10.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 10.0f, new float[] {50,25, 50,25, 50,25, 50,25, 50,25}, 0.0f);
        // 绘制坐标轴的笔
        axisStroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);
        // 用于绘制标记轮廓的笔
        markerStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);
        // 轴标签的字体
        axisFont = new Font("Serif", Font.BOLD, 36);
    }
    //这个方法是从“用图表打开文件”菜单项的处理程序调用的
    // 成功加载数据时的主应用程序窗口
    public void showGraphics(Double[][] graphicsData)
    {
        // 在类的内部字段中存储点数组
        this.graphicsData = graphicsData;
        // 请求重新绘制组件，即隐式调用paintComponent()
        repaint();
    }

    // 更改图表显示参数的修改器方法
    // 更改任何参数都会导致重新绘制区域
    public void setShowAxis(boolean showAxis) 	//用于更改屏幕上图形显示的函数变量声明
    {
        this.showAxis = showAxis;				//坐标轴
        repaint();
    }
    public void setShowMarkers(boolean showMarkers)
    {
        this.showMarkers = showMarkers;			//点[私有类数据字段]
        repaint();
    }

    public void setShowGraphics(boolean showgraphics)
    {
        this.showGraphics = showGraphics;
        repaint();
    }

    /*要获取类的实例，需要将Graphics实例的Graphics2D类型强制转换，在paintComponent()方法中作为参数接收*/
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);


        //如果没有加载图表数据（当程序启动时显示组件时） - 什么都不做
        if (graphicsData==null || graphicsData.length==0)
            return;
        // 第 3 步 - 确定 X 和 Y 坐标的最小值和最大值
        // 这是定义要显示的空间区域所必需的。
        // 它的左上角是 (minX, maxY) - 右下角是 (maxX, minY)
        minX = graphicsData[0][0];
        maxX = graphicsData[graphicsData.length-1][0];
        minY = graphicsData[0][1];
        maxY = minY;
        // 查找函数的最小值和最大值
        for (int i = 1; i<graphicsData.length; i++)
        {
            if (graphicsData[i][1]<minY)
            {
                minY = graphicsData[i][1];
            }
            if (graphicsData[i][1]>maxY)
            {
                maxY = graphicsData[i][1];
            }
        }
        /* 第 4 步 - 确定（基于窗口的大小）沿 X 和 Y 轴的比例 - X 和 Y 中每单位长度有多少像素（？）*/
        double scaleX = getSize().getWidth() / (maxX - minX);
        double scaleY = getSize().getHeight() / (maxY - minY);
        // 第 5 步 - 为了使图像不失真 - 比例必须相同
        // 我们选择最小比例因子作为基础
        scale = Math.min(scaleX, scaleY);
        // 步骤 6 - 根据选择的比例调整显示区域的边框
        if (scale==scaleX)
        {
            /* 如果以沿 X 轴的刻度为基础，则沿 Y 轴的划分较少,
             * 那些。要渲染的 Y 范围将小于窗口高度。所以需要加分部，我们这样做：
             * 1) 让我们计算在所选比例下 Y 中适合多少个分区 - getSize().GetHeight()/比例
             * 2) 从中减去最初需要多少个部门
             * 3) 在 maxY 和 minY 处投入缺失距离的一半*/
            double yIncrement = (getSize().getHeight()/scale - (maxY - minY))/2;
            maxY += yIncrement;
            minY -= yIncrement;
        }
        if (scale==scaleY)
        {
            // 如果以沿Y轴的刻度为基础，以此类推
            double xIncrement = (getSize().getWidth()/scale - (maxX - minX))/2;
            maxX += xIncrement;
            minX -= xIncrement;
        }
        // 步骤 7 - 保存当前画布设置
        Graphics2D canvas = (Graphics2D) g;
        Stroke oldStroke = canvas.getStroke();	//返回画布的当前笔
        Color oldColor = canvas.getColor();		//颜色
        Paint oldPaint = canvas.getPaint();		//填
        Font oldFont = canvas.getFont();		//字体

        // 第 8 步 - 调用以所需顺序显示图表元素的方法
        // 调用方法的顺序很重要，因为上一张图将被下一张覆盖

        // 首先绘制坐标轴（如有必要）。
        if (showAxis)
            paintAxis(canvas);

        // 然后显示图形本身
        if (showGraphics)
            paintGraphics(canvas);

        // 然后（如有必要）显示构建图形的点的标记。
        if (showMarkers)
            paintMarkers(canvas);

        // 步骤 9 - 恢复旧的画布设置
        canvas.setFont(oldFont);
        canvas.setPaint(oldPaint);
        canvas.setColor(oldColor);
        canvas.setStroke(oldStroke);
    }

    // //通过读取坐标绘制图表
    protected void paintGraphics(Graphics2D canvas)
    {
        // 选择一条线来绘制图形
        canvas.setStroke(graphicsStroke);
        // 选择线条颜色
        canvas.setColor(Color.RED);
        /* 我们将图形线绘制为由许多段组成的路径（GeneralPath）
         * 路径的起点设置在图形的第一个点，之后直线连接到下一个点*/

        GeneralPath graphics = new GeneralPath();

        for (int i=0; i<graphicsData.length; i++)
        {
            // 将值（x，y）转换为屏幕点上的点
            Point2D.Double point = xyToPoint(graphicsData[i][0], graphicsData[i][1]);
            if (i>0)
            {
                // 不是循环的第一次迭代 - 画线到点
                graphics.lineTo(point.getX(), point.getY());
            }
            else
            {
                // 循环的第一次迭代是将路径的开头设置为点
                graphics.moveTo(point.getX(), point.getY());
            }

        }
        canvas.draw(graphics);		// 显示图表
    }

    private void graphics(BasicStroke basicStroke, boolean b) {
    }

    // 显示用于绘制图形的点标记
    protected void paintMarkers(Graphics2D canvas)
    {
        for (Double[] point: graphicsData) 		//在图形的所有点上组织一个循环
        {
            canvas.setStroke(markerStroke);			//设置一支特殊的笔来勾画标记的轮廓


            //canvas.setPaint(Color.GREEN);			//选择绿色填充内部标记
			/*
				// 将椭圆初始化为一个对象来表示标记
				//Ellipse2D.Double 标记 = 新的 Ellipse2D.Double();
				// 椭圆将通过指定其中心和矩形角的坐标来指定, в который он вписан
			Point2D.Double center = xyToPoint(point[0], point[1]);	// 中心 - 在点 (x, y)
			Point2D.Double corner = shiftPoint(center, 3, 3);		// 矩形的角 - 间隔 (3.3)
			marker.setFrameFromCenter(center, corner);				// 设置中心椭圆和对角椭圆
			canvas.draw(marker);									// 绘制标记轮廓
			canvas.fill(marker);									// 填充标记的内部区域
			*/

            GeneralPath path = new GeneralPath();
            Point2D.Double center = xyToPoint(point[0], point[1]);

            canvas.setColor(Color.BLUE);	//为标记轮廓选择绿色
            canvas.setPaint(Color.BLUE);
            if ( unorderedValues(point[1]) )
            {
                canvas.setColor(Color.GREEN);			//инеаче - синий
                canvas.setPaint(Color.GREEN);
            }

            path.moveTo(center.x,center.y - 5);
            path.lineTo(center.x,center.y + 5);
            path.moveTo(center.x-5,center.y );
            path.lineTo(center.x+5,center.y);
            canvas.draw(new Ellipse2D.Double(center.x-5,center.y-5,10,10));
            canvas.draw(path);
        }
    }

    private boolean unorderedValues(Double value)
    {
        if(value%2==0)
        {
            return true;
        }
        return false;
    }

    // 坐标轴的显示方法
    protected void paintAxis(Graphics2D canvas)
    {
        // 为轴设置特殊面
        canvas.setStroke(axisStroke);
        // 轴以黑色绘制
        canvas.setColor(Color.BLACK);
        // 箭头充满黑色
        canvas.setPaint(Color.BLACK);
        // 坐标轴标签采用特殊字体
        canvas.setFont(axisFont);

        //**************************************
        // 创建文本显示上下文对象——获取设备（屏幕）的特性
        FontRenderContext context = canvas.getFontRenderContext();
        // 确定 y 轴是否应在图形上可见
        if (minX<=0.0 && maxX>=0.0)
        {
            // 如果显示区域的左边框 (minX) <= 0.0，则它应该可见,// а правая (maxX) >= 0.0
            //轴本身是点 (0, maxY) 和 (0, minY) 之间的线
            canvas.draw(new Line2D.Double(xyToPoint(0, maxY), xyToPoint(0, minY)));
            // Y轴箭头
            GeneralPath arrow = new GeneralPath();
            //将折线的起点精确设置为 Y 轴的顶端
            Point2D.Double lineEnd = xyToPoint(0, maxY);
            arrow.moveTo(lineEnd.getX(), lineEnd.getY());
            // 将箭头的左侧“斜率”引导到具有相对坐标 (5,20) 的点
            arrow.lineTo(arrow.getCurrentPoint().getX()+5, arrow.getCurrentPoint().getY()+20);
            // 将箭头的底部带到具有相对坐标 (-10, 0) 的点
            arrow.lineTo(arrow.getCurrentPoint().getX()-10, arrow.getCurrentPoint().getY());
            // 关闭三角形箭头
            arrow.closePath();
            canvas.draw(arrow); // 画一个箭头
            canvas.fill(arrow);	// 涂在箭头上


            // 在 Y 轴上绘制标题
            // 确定“y”需要多少空间
            Rectangle2D bounds = axisFont.getStringBounds("y", context);
            Point2D.Double labelPos = xyToPoint(0, maxY);
            // 在具有计算坐标的点处显示标签
            canvas.drawString("y", (float)labelPos.getX() + 10, (float)(labelPos.getY() - bounds.getY()));
        }
        // 确定 X 轴是否应在图形上可见
        if (minY<=0.0 && maxY>=0.0)
        {
            // 如果显示区域的上边框 (maxX)> = 0.0，则它应该是可见的，
            // 和底部 (minY) <= 0.0
            canvas.draw(new Line2D.Double(xyToPoint(minX, 0), xyToPoint(maxX, 0)));
            // X 轴箭头
            GeneralPath arrow = new GeneralPath();
            // 将折线的起点精确设置为 X 轴的右端
            Point2D.Double lineEnd = xyToPoint(maxX, 0);
            arrow.moveTo(lineEnd.getX(), lineEnd.getY());
            // 将箭头的上方“斜率”引导到具有相对坐标 (-20, -5) 的点
            arrow.lineTo(arrow.getCurrentPoint().getX()-20, arrow.getCurrentPoint().getY()-5);
            // 将箭头左侧移动到具有相对坐标 (0, 10) 的点
            arrow.lineTo(arrow.getCurrentPoint().getX(), arrow.getCurrentPoint().getY()+10);
            // 关闭三角形箭头
            arrow.closePath();
            canvas.draw(arrow); // 画一个箭头
            canvas.fill(arrow);
            // 涂在箭头上
            // 在 X 轴上绘制标题
            // 确定“x”需要多少空间
            Rectangle2D bounds = axisFont.getStringBounds("x", context);
            Point2D.Double labelPos = xyToPoint(maxX, 0);
            // 在具有计算坐标的点处显示标签
            canvas.drawString("x", (float)(labelPos.getX() - bounds.getWidth() - 10), (float)(labelPos.getY() + bounds.getY()));
        }
        if (minY<=0.0 && maxY>=0.0&&minX<=0.0 && maxX>=0.0)
        {
            Rectangle2D bounds = axisFont.getStringBounds("0", context);
            Point2D.Double labelPos = xyToPoint(0, 0);
            canvas.drawString("0",(float)labelPos.getX() - 10 , (float)labelPos.getY() - 10);
        }
    }
    /*一个变换坐标的辅助方法。这是必须的，因为带有坐标的画布左上角
    (0.0, 0.0) 对应坐标为 (minX, maxY) 的图形点，其中 minX 是 X 的“最左边”值，maxY 是“最上端”值 Y.*/
    protected Point2D.Double xyToPoint(double x, double y)
    {
        // 计算距最左边点的 X 偏移量 (minX)
        double deltaX = x - minX;
        // 计算距顶点的 Y 偏移量 (maxY)
        double deltaY = maxY - y;
        return new Point2D.Double(deltaX*scale, deltaY*scale);
    }
    /* 返回 Point2D.Double 类实例的辅助方法，从初始实例偏移 deltaX, deltaY 不幸的是，没有执行此任务的标准方法。*/
    protected Point2D.Double shiftPoint(Point2D.Double src, double deltaX, double deltaY)
    {
        // 初始化一个新的点实例
        Point2D.Double dest = new Point2D.Double();
        // 将其坐标设置为现有点的坐标 + 指定的偏移量
        dest.setLocation(src.getX() + deltaX, src.getY() + deltaY);
        return dest;
    }
}
