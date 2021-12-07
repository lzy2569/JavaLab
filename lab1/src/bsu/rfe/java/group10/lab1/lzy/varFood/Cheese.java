package bsu.rfe.java.group10.lab1.lzy.varFood;

public class Cheese extends Food//通过关键字extends继承一个已有的类
{

    public Cheese()
    {
    super ("Сыр");
    }

    public void consume()
    {
        System.out.println(this + " съеден");
    }
}
