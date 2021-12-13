import	java.awt.BorderLayout;
import	java.awt.Toolkit;
import	java.awt.event.ActionEvent;
import java.io.*;
import	javax.swing.AbstractAction;
import	javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import	javax.swing.JMenu;
import	javax.swing.JMenuBar;
import	javax.swing.JOptionPane;
import	javax.swing.event.MenuEvent;
import	javax.swing.event.MenuListener;
@SuppressWarnings("serial")
public class MainFrame extends JFrame
{
    // 应用程序窗口的初始尺寸
    private static final int WIDTH = 600;
    private static final int HEIGHT =500;
    // 文件选择对话框对象
    private JFileChooser fileChooser = null;
    // 菜单项
    private JCheckBoxMenuItem showAxisMenuItem;
    private JCheckBoxMenuItem showMarkersMenuItem;
    private JCheckBoxMenuItem showGraphicsMenuItem;
    // 图形显示组件
    private GraphicsDisplay display = new GraphicsDisplay();
    // 指示图表数据已加载的标志
    private boolean fileLoaded = false;
    public MainFrame()
    {
        // 调用祖先帧的构造函数
        super("Построение графиков функций на основе заранее подготовленных файлов");
        // 设置窗口大小
        setSize(WIDTH, HEIGHT);
        Toolkit kit = Toolkit.getDefaultToolkit();
        // 将应用程序窗口置于屏幕中央
        setLocation((kit.getScreenSize().width - WIDTH)/2, (kit.getScreenSize().height - HEIGHT)/2);
        // *****************将窗口最大化到全屏***********************
        //setExtendedState(MAXIMIZED_BOTH);

        // 创建并安装菜单栏
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        fileChooser = new JFileChooser();
        // 添加菜单项“文件”
        JMenu fileMenu = new JMenu("Файл");
        menuBar.add(fileMenu);
        // 创建打开文件的动作
        Action openGraphicsAction = new AbstractAction("Открыть файл с графиком")
        {
            public void actionPerformed(ActionEvent event)
            {
                if (fileChooser==null)
                {

                    fileChooser.setCurrentDirectory(new File("~"));
                }
                if (fileChooser.showOpenDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION)
                    openGraphics(fileChooser.getSelectedFile());
            }
        };
        // 添加匹配的菜单项
        fileMenu.add(openGraphicsAction);

        Action saveGraphicsAction = new AbstractAction("Сохранить файл с графиком") {
            public void actionPerformed(ActionEvent event) {
                fileChooser.setCurrentDirectory(new File("~"));
                if (fileChooser.showSaveDialog(MainFrame.this)==JFileChooser.APPROVE_OPTION)
                    saveGraphics(fileChooser.getSelectedFile());
            }
        };
        fileMenu.add(saveGraphicsAction);


        // 创建菜单项“图形”
        JMenu graphicsMenu = new JMenu("График");
        menuBar.add(graphicsMenu);
        // 创建一个动作来响应“显示坐标轴”元素的激活
        Action showAxisAction = new AbstractAction("Показывать оси координат")
        {
            public void actionPerformed(ActionEvent event)
            {
                // 如果菜单项，GraphicsDisplay 类的 showAxis 属性为 true
                // showAxisMenuItem 被检查，否则为 false
                display.setShowAxis(showAxisMenuItem.isSelected());
            }
        };
        showAxisMenuItem = new JCheckBoxMenuItem(showAxisAction);
        // 将相应的项目添加到菜单中
        graphicsMenu.add(showAxisMenuItem);// 项目默认启用（选中）
        showAxisMenuItem.setSelected(true);

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // 重复显示点标记
        Action showMarkersAction = new AbstractAction("Показывать маркеры точек")
        {
            public void actionPerformed(ActionEvent event)
            {
                // 类似于 showAxisMenuItem
                display.setShowMarkers(showMarkersMenuItem.isSelected());
            }
        };
        showMarkersMenuItem = new JCheckBoxMenuItem(showMarkersAction);
        graphicsMenu.add(showMarkersMenuItem);
        showMarkersMenuItem.setSelected(true);				// 项目默认启用（选中）
        Action showGraphicsAction = new AbstractAction("Отображать график")
        {
            public void actionPerformed(ActionEvent event)
            {
                display.setShowGraphics(showGraphicsMenuItem.isSelected());
            }
        };
        showGraphicsMenuItem = new JCheckBoxMenuItem(showGraphicsAction);
        //graphicsMenu.add(showGraphicsMenuItem);
        showGraphicsMenuItem.setSelected(true);

        // 为与“Graph”菜单相关的事件注册一个处理程序
        graphicsMenu.addMenuListener(new GraphicsMenuListener());
        // 将 GraphicsDisplay 设置为边界框的中心
        getContentPane().add(display, BorderLayout.CENTER);

        openGraphics(new File("E:\\桌面\\Lab4-5\\X^2 new"));
    }


    protected void saveGraphics(File selectedFile) {
        File file=fileChooser.getSelectedFile();
        try {
            DataOutputStream out = new DataOutputStream(new FileOutputStream(file));
            for (int i = 0; i<display.getDataLenght(); i++) {
                out.writeDouble(display.getValue(i,0));
                out.writeDouble(display.getValue(i,1));
            }
            out.close();
        } catch (Exception e) {
            System.out.println("Не удалость создать файл");
        }
    }



    // 从现有文件中读取图形数据
    protected void openGraphics(File selectedFile)
    {
        try
        {
            // 步骤 1 - 打开与输入文件流关联的读取数据流
            DataInputStream in = new DataInputStream(new FileInputStream(selectedFile));
            /* 第 2 步 - 知道输入流中的数据量，您可以计算,
             * 应该在数组中保留多少内存：
             * 流中的总字节数 - in.available() 字节；
             * 一个Double数字的大小为Double.SIZE位，即Double.SIZE/8字节；
             * 由于数字是成对写的，所以对数少了 2 倍 */
            Double[][] graphicsData = new Double[in.available()/(Double.SIZE/8)/2][];
            // 第 3 步 - 循环读取数据（只要流中有数据
            int i = 0;
            while (in.available()>0)
            {
                // 首先从流中读取 X 坐标
                Double x = in.readDouble();
                // 然后 - Y 图在 X 点的值
                Double y = in.readDouble();
                // 读取的坐标对被添加到数组中
                graphicsData[i++] = new Double[] {x, y};
            }
            // 第 4 步 - 检查列表是否包含至少一对作为读取结果的坐标
            if (graphicsData!=null && graphicsData.length>0)
            {
                // 是 - 设置数据加载标志
                fileLoaded = true;
                // 调用图表显示方法
                display.showGraphics(graphicsData);
            }
            // 第 5 步 - 关闭输入流
            in.close();
        }
        catch (FileNotFoundException ex)
        {
            //如果出现“找不到文件”类型的异常，则显示错误消息
            JOptionPane.showMessageDialog(MainFrame.this, "Указанный файл не найден", "Ошибка загрузки данных",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        catch (IOException ex)
        {
            // 如果来自文件流的输入错误，则显示错误消息
            JOptionPane.showMessageDialog(MainFrame.this, "Ошибка чтения координат точек из файла",
                    "Ошибка загрузки данных", JOptionPane.WARNING_MESSAGE);
            return;
        }
    }


    public static void main(String[] args)
    {
        // 创建并显示主应用程序窗口的实例
        MainFrame frame = new MainFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    // 与显示菜单相关的事件的侦听器类
    private class GraphicsMenuListener implements MenuListener
    {
        // 在显示菜单之前调用的处理程序
        public void menuSelected(MenuEvent e)
        {
            // “图形”菜单项的可用性或不可访问性由数据加载决定
            showAxisMenuItem.setEnabled(fileLoaded);
            showMarkersMenuItem.setEnabled(fileLoaded);
            showGraphicsMenuItem.setEnabled(fileLoaded);

        }
        // 菜单从屏幕上消失后要调用的处理程序
        public void menuDeselected(MenuEvent e)
        {
        }

        // 取消菜单项时调用的处理程序（非常罕见的情况）
        public void menuCanceled(MenuEvent e)
        {
        }
    }
}