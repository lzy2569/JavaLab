import javax.swing.table.AbstractTableModel;
@SuppressWarnings("serial")

public class GornerTableModel extends AbstractTableModel//角表模型 抽象表模型
{
    private Double[] coefficients;//系数

    private Double from;
    private Double to;
    private Double step;

    public GornerTableModel(Double from, Double to, Double step, Double[] coefficients)
    {
        this.from = from;
        this.to = to;
        this.step = step;
        this.coefficients = coefficients;
    }
    public Double getFrom() {
        return from;
    }

    public Double getTo() {
        return to;
    }

    public Double getStep() {
        return step;
    }

    public int getColumnCount() {
        return 4;
    }//模型中有四列

    public int getRowCount() {
        return (int)((float)(to-from)/step)+1;
    }//从制表步骤计算线段开始和结束之间的点数

    private Double calcGorner(Double x)
    {
        Double result = 0.0;
        for(int i=0;i<coefficients.length;i++)
        {
            result=(result*x+coefficients[i]);
        }
        return result;
    }

    private Double calcnGorner(Double x)
    {
        double result=0.0;
        for(int i=0;i<coefficients.length;i++)
        {
            result+=coefficients[i]*Math.pow(x, coefficients.length-i - 1);
        }
        if(result == (int)result)
        {
            return 0.0;
        }else{
            return result;}
    }

    public Object getValueAt(int row, int col)
    {
        //计算x值作为线段的开始+间距*行号
        double x = from + step*row;
        switch(col){
            case 0:
                return x;
            case 1:
                return calcGorner(x);
            case 2:
                return calcnGorner(x);
            case 3:
                return Math.abs(calcGorner(x)-calcnGorner(x));
            default:
                break;
        }
        return null;
    }

    public String getColumnName(int col)
    {
        switch (col)
        {
            case 0:
                return "Значение X";//X值
            case 1:
                return "Значение многочлена";//多项式值
            case 2:
                return "Вычесленно не по схеме";//不按方案计算
            case 3:
                return "Разница столбцов 2 и 3";//第 2 列和第 3 列之间的差异
            default:
                return "error";//错误
        }
    }

    public Class<?> getColumnClass(int col)
    {
        //在第1列和第2列中都有类型为Double的值
        return Double.class;
    }
}