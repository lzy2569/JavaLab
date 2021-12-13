import java.awt.*;
import	java.awt.font.FontRenderContext;
import	java.awt.geom.Ellipse2D;
import 	java.awt.geom.GeneralPath;
import	java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import	java.awt.geom.Point2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import	java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.EmptyStackException;
import java.util.Stack;
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
    private double scaleX;
    private double scaleY;
    private BasicStroke graphicsStroke;						// 不同风格的线条画
    private BasicStroke axisStroke;
    private BasicStroke markerStroke;
    private BasicStroke lineGraphics;
    private Font axisFont;// 用于显示标签的各种字体
    private DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance();
    private GraphPoint SMP;
    private Font captionFont;
    private Rectangle2D.Double rect;
    private boolean selMode = false;
    private boolean dragMode = false;
    private int mausePX = 0;
    private int mausePY = 0;
    private Stack<Zone> stack = new Stack<Zone>();
    private BasicStroke selStroke;
    private Zone zone = new Zone();
    private boolean zoom=false;
    private int[][] graphicsDataI;
    class GraphPoint {
        double xd;
        double yd;
        int x;
        int y;
        int n;
    }
    class Zone {
        double MAXY;
        double tmp;
        double MINY;
        double MAXX;
        double MINX;
        boolean use;
    }
    public GraphicsDisplay()
    {
        setBackground(Color.WHITE);							// 显示区域的背景色为白色
        // 构建绘图中使用的必要对象：

        //BAsicStroke.CAP_BUTT - 线作为“树桩”，在两侧
        //BasicStroke.JION_ROUND - 半圆完成线闭合, BAsicStorke.JOIN_MiTER - 用“尖刺”结束行

        // 图形笔 - 绘制形状的边界
        //线宽 2.0f - 两点，10.0f - 闭合角 10 度
        graphicsStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 10.0f, new float[] {5,2, 5,2, 5,2, 5,2, 5,2}, 0.0f);
        // 绘制坐标轴的笔
        axisStroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);
        // 用于绘制标记轮廓的笔
        markerStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);
        // 轴标签的字体
        axisFont = new Font("Serif", Font.BOLD, 36);

        selStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[] { 8, 8 }, 0.0f);


        captionFont = new Font("Serif", Font.BOLD, 10);

        MouseMotionHandler mouseMotionHandler = new MouseMotionHandler();
        addMouseMotionListener(mouseMotionHandler);
        addMouseListener(mouseMotionHandler);
        rect = new Rectangle2D.Double();
        zone.use = false;
    }
    //这个方法是从“用图表打开文件”菜单项的处理程序调用的
    // 成功加载数据时的主应用程序窗口
    public void showGraphics(Double[][] graphicsData)
    {
        // 在类的内部字段中存储点数组
        this.graphicsData = graphicsData;
        // 请求重新绘制组件，即隐式调用paintComponent()
        graphicsDataI = new int[graphicsData.length][2];
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
    public double getValue(int i, int j) {
        return graphicsData[i][j];
    }

    public int getDataLenght() {
        return graphicsData.length;
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
        if (zone.use) {
            minX = zone.MINX;
        }
        if (zone.use) {
            maxX = zone.MAXX;
        }
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
        if (zone.use) {
            minY = zone.MINY;
        }
        if (zone.use) {
            maxY = zone.MAXY;
        }
        scaleX = 1.0 / (maxX - minX);
        scaleY = 1.0 / (maxY - minY);
        scaleX *= getSize().getWidth();
        scaleY *= getSize().getHeight();

        /* 第 4 步 - 确定（基于窗口的大小）沿 X 和 Y 轴的比例 - X 和 Y 中每单位长度有多少像素（？）*/

        // 第 5 步 - 为了使图像不失真 - 比例必须相同
        // 我们选择最小比例因子作为基础
        scale = Math.min(scaleX, scaleY);

        if(!zoom){
            if (scale == scaleX) {
                double yIncrement = 0;

                yIncrement = (getSize().getHeight() / scale - (maxY - minY)) / 2;

                maxY += yIncrement;
                minY -= yIncrement;
            }
            if (scale == scaleY) {
                double xIncrement = 0;

                xIncrement = (getSize().getWidth() / scale - (maxX - minX)) / 2;
                maxX += xIncrement;
                minX -= xIncrement;

            }
        }
        // 步骤 6 - 根据选择的比例调整显示区域的边框

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

        if (SMP != null)
            paintHint(canvas);
        // 然后（如有必要）显示构建图形的点的标记。
        if (showMarkers)
            paintMarkers(canvas);

        if (selMode) {
            canvas.setColor(Color.BLACK);
            canvas.setStroke(selStroke);
            canvas.draw(rect);
        }

        // 步骤 9 - 恢复旧的画布设置
        canvas.setFont(oldFont);
        canvas.setPaint(oldPaint);
        canvas.setColor(oldColor);
        canvas.setStroke(oldStroke);
    }

    protected void paintHint(Graphics2D canvas) {
        Color oldColor = canvas.getColor();
        canvas.setColor(Color.PINK);
        StringBuffer label = new StringBuffer();
        label.append("X=");
        label.append(formatter.format((SMP.xd)));
        label.append(", Y=");
        label.append(formatter.format((SMP.yd)));
        FontRenderContext context = canvas.getFontRenderContext();
        Rectangle2D bounds = captionFont.getStringBounds(label.toString(),context);
        if (true) {
            int dy = -10;
            int dx = +7;
            if (SMP.y < bounds.getHeight())
                dy = +13;
            if (getWidth() < bounds.getWidth() + SMP.x + 20)
                dx = -(int) bounds.getWidth() - 15;
            canvas.drawString (label.toString(), SMP.x + dx, SMP.y + dy);
        } else {
            int dy = 10;
            int dx = -7;
            if (SMP.x < 10)
                dx = +13;
            if (SMP.y < bounds.getWidth() + 20)
                dy = -(int) bounds.getWidth() - 15;
            canvas.drawString (label.toString(), getHeight() - SMP.y + dy, SMP.x + dx);
        }
        canvas.setColor(oldColor);
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


            graphicsDataI[i][0] = (int) point.getX();
            graphicsDataI[i][1] = (int) point.getY();
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

        if (minY<=0.0 && maxY>=0.0&&minX<=0.0 && maxX>=0.0)//原点
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
    protected Point2D.Double pointToXY(int x, int y) {
        Point2D.Double p = new Point2D.Double();
        p.x = x / scale + minX;
        int q = (int) xyToPoint(0, 0).y;
        p.y = maxY - maxY * ((double) y / (double) q);

        return p;
    }

    public class MouseMotionHandler implements MouseMotionListener, MouseListener {
        private double comparePoint(Point p1, Point p2) {
            return Math.sqrt(Math.pow(p1.x - p2.x, 2)
                    + Math.pow(p1.y - p2.y, 2));
        }

        private GraphPoint find(int x, int y) {
            GraphPoint smp = new GraphPoint();
            GraphPoint smp2 = new GraphPoint();
            double r, r2 = 1000;
            for (int i = 0; i < graphicsData.length; i++) {
                Point p = new Point();
                p.x = x;
                p.y = y;
                Point p2 = new Point();
                p2.x = graphicsDataI[i][0];
                p2.y = graphicsDataI[i][1];
                r = comparePoint(p, p2);
                if (r < 7.0) {
                    smp.x = graphicsDataI[i][0];
                    smp.y = graphicsDataI[i][1];
                    smp.xd = graphicsData[i][0];
                    smp.yd = graphicsData[i][1];
                    smp.n = i;
                    if (r < r2) {
                        r2 = r;
                        smp2 = smp;
                    }
                    return smp2;
                }
            }
            return null;
        }

        //
        public void mouseMoved(MouseEvent ev) {
            GraphPoint smp;
            smp = find(ev.getX(), ev.getY());
            if (smp != null) {
                setCursor(Cursor.getPredefinedCursor(8));
                SMP = smp;
            } else {
                setCursor(Cursor.getPredefinedCursor(0));
                SMP = null;
            }
            repaint();
        }

        public void mouseDragged(MouseEvent e) {
            if (selMode) {
                rect.setFrame(mausePX, mausePY, e.getX() - rect.getX(),
                        e.getY() - rect.getY());

                repaint();
            }
            if (dragMode) {

                if(pointToXY(e.getX(), e.getY()).y<maxY && pointToXY(e.getX(), e.getY()).y>minY){
                    graphicsData[SMP.n][1] = pointToXY(e.getX(), e.getY()).y;
                    SMP.yd = pointToXY(e.getX(), e.getY()).y;
                    SMP.y = e.getY();
                }
                repaint();
            }
        }


        public void mouseClicked(MouseEvent e) {
            if (e.getButton() != 3)//右键重置
                return;
            try {
                zone = stack.pop();
            } catch (EmptyStackException err) {

            }
            if(stack.empty())
                zoom=false;
            repaint();
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (e.getButton() != 1)
                return;
            if (SMP != null) {
                selMode = false;
                dragMode = true;
            } else {
                dragMode = false;
                selMode = true;
                mausePX = e.getX();
                mausePY = e.getY();
                if (!false)
                    rect.setFrame(e.getX(), e.getY(), 0, 0);
                else
                    rect.setFrame(e.getX(), e.getY(), 0, 0);
            }
        }

        public void mouseEntered(MouseEvent arg0) {

        }

        public void mouseExited(MouseEvent arg0) {

        }



        //拖动
        public void mouseReleased(MouseEvent e) {
            rect.setFrame(0, 0, 0, 0);
            if (e.getButton() != 1) {
                repaint();
                return;
            }
            if (selMode) {

                if (e.getX() <= mausePX || e.getY() <= mausePY)
                    return;
                int eY = e.getY();
                int eX = e.getX();
                if (eY > getHeight())
                    eY = getHeight();
                if (eX > getWidth())
                    eX = getWidth();
                double MAXX = pointToXY(eX, 0).x;
                double MINX = pointToXY(mausePX, 0).x;
                double MAXY = pointToXY(0, mausePY).y;
                double MINY = pointToXY(0, eY).y;
                stack.push(zone);
                zone = new Zone();
                zone.use = true;
                zone.MAXX = MAXX;
                zone.MINX = MINX;
                zone.MINY = MINY;
                zone.MAXY = MAXY;
                selMode = false;
                zoom=true;

            }
            repaint();
        }
    }
}
