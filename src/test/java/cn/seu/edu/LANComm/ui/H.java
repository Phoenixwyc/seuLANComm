package cn.seu.edu.LANComm.ui;

/**
 * Created by Administrator on 2018/3/16.
 */
public class H {

    public static void main(String[] args) {
        float[] data = new float[]{1F,2F,3F,4F,5F,6F};
        for (int index = 0; index < data.length; ) {
            System.out.println("I " + data[index] + " Q " + data[++index]);
            index++;
        }

        double a = Double.POSITIVE_INFINITY;
        System.out.println(a == Double.POSITIVE_INFINITY);
    }
}
