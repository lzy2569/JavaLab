package bsu.rfe.java.group10.lab1.lzy.varFood;

public class Pie extends Food{
    @Override // 显式方法覆盖
public boolean equals(Object arg0)
    {
    if (!(arg0 instanceof Pie))//判断其左边对象是否为其右边类的实例，返回的是boolean类型的数据
    {
        return false;
    }
    if (fill == null || ((Pie)arg0).fill == null)
    {
        return false;
    }
    if(!(fill.equals(((Pie) arg0).fill)))
    {
        return false;
    }

    return super.equals(arg0);//调用main中的构造函数
}

    private String fill;

    public String getFilling()
    {
        return fill;
    }

    public void setFilling(String fill)
    {
        this.fill = fill;
    }

    public Pie(String fill)
    {
        super ("Пирог");
        this.fill = fill;
    }

    public void consume()
    {
        System.out.println(this + " съеден");
    }

    @Override
    public String toString()
    {
        return super.toString() + " c начинкой '" + fill.toUpperCase() + "'";
    }
}
