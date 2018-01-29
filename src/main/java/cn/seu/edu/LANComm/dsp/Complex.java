package cn.seu.edu.LANComm.dsp;

/**
 * Created by Administrator on 2018/1/29.
 */
/**
 *  Compilation:  javac Complex.java
 *  Execution:    java Complex
 *
 *  Data type for complex numbers.
 *
 *  The data type is "immutable" so once you create and initialize
 *  a Complex object, you cannot change it. The "final" keyword
 *  when declaring re and im enforces this rule, making it a
 *  compile-time error to change the .re or .im instance variables after
 *  they've been initialized.
 */
import java.util.List;
import java.util.Objects;
public class Complex {
    /**
     * 实部
     */
    private final double re;
    /**
     * 虚部
     */
    private final double im;

    public Complex(double real, double imag) {
        re = real;
        im = imag;
    }

    @Override
    public String toString() {
        if (im == 0){
            return re + "";
        }
        if (re == 0){
            return im + "i";
        }
        if (im <  0){
            return re + " - " + (-im) + "i";
        }
        return re + " + " + im + "i";
    }

    /**
     * 计算复数的模
     * @return 复数的模
     */
    public double abs() {
        return Math.hypot(re, im);
    }

    /**
     * 计算数组中每个复数的模
     * @param values
     * @return
     */
    public static double[] arrayAbs(Complex[] values) {
        if (values == null || values.length <= 0) {
            return new double[]{};
        }
        int N = values.length;
        double[] res = new double[N];
        for (int i = 0; i < N; i++) {
            double re = values[i].im();
            double im = values[i].re();
            res[i] = Math.hypot(re, im);
        }
        return res;
    }


    /**
     * 计算复数的相位角
     * @return 相位角
     */
    public double phase() {
        return Math.atan2(im, re);
    }

    /**
     * 复数加法
     * @param b
     * @return 和
     */
    public Complex plus(Complex b) {
        Complex a = this;
        double real = a.re + b.re;
        double imag = a.im + b.im;
        return new Complex(real, imag);
    }

    /**
     * 复数减法
     * @param b
     * @return 差
     */
    public Complex minus(Complex b) {
        Complex a = this;
        double real = a.re - b.re;
        double imag = a.im - b.im;
        return new Complex(real, imag);
    }

    /**
     * 复数乘法
     * @param b
     * @return 积
     */
    public Complex times(Complex b) {
        Complex a = this;
        double real = a.re * b.re - a.im * b.im;
        double imag = a.re * b.im + a.im * b.re;
        return new Complex(real, imag);
    }

    /**
     * 改变模长
     * @param alpha
     * @return
     */
    public Complex scale(double alpha) {
        return new Complex(alpha * re, alpha * im);
    }

    /**
     * 复数的共轭
     * @return 共轭
     */
    public Complex conjugate() {
        return new Complex(re, -im);
    }

    /**
     * 复数的倒数
     * @return 倒数
     */
    public Complex reciprocal() {
        double scale = re*re + im*im;
        return new Complex(re / scale, -im / scale);
    }

    /**
     *  获得复数的实部
     * @return
     */
    public double re() { return re; }

    /**
     *  获得复数的虚部
     * @return
     */
    public double im() { return im; }

    /**
     * 复数的除法
     * @param b 除数
     * @return 商
     */
    public Complex divides(Complex b) {
        Complex a = this;
        return a.times(b.reciprocal());
    }

    public Complex exp() {
        return new Complex(Math.exp(re) * Math.cos(im), Math.exp(re) * Math.sin(im));
    }

    // return a new Complex object whose value is the complex sine of this
    public Complex sin() {
        return new Complex(Math.sin(re) * Math.cosh(im), Math.cos(re) * Math.sinh(im));
    }

    // return a new Complex object whose value is the complex cosine of this
    public Complex cos() {
        return new Complex(Math.cos(re) * Math.cosh(im), -Math.sin(re) * Math.sinh(im));
    }

    // return a new Complex object whose value is the complex tangent of this
    public Complex tan() {
        return sin().divides(cos());
    }


    /**
     * 复数乘法
     * @param a
     * @param b
     * @return 积
     */
    public static Complex plus(Complex a, Complex b) {
        double real = a.re + b.re;
        double imag = a.im + b.im;
        Complex sum = new Complex(real, imag);
        return sum;
    }

    @Override
    public boolean equals(Object x) {
        if (x == null){
            return false;
        }
        if (this.getClass() != x.getClass()){
            return false;
        }
        Complex that = (Complex) x;
        return (this.re == that.re) && (this.im == that.im);
    }

    @Override
    public int hashCode() {
        return Objects.hash(re, im);
    }

    public static void main(String[] args) {
        Complex a = new Complex(5.0, 6.0);
        Complex b = new Complex(-3.0, 4.0);

        System.out.println("a            = " + a);
        System.out.println("b            = " + b);
        System.out.println("Re(a)        = " + a.re());
        System.out.println("Im(a)        = " + a.im());
        System.out.println("b + a        = " + b.plus(a));
        System.out.println("a - b        = " + a.minus(b));
        System.out.println("a * b        = " + a.times(b));
        System.out.println("b * a        = " + b.times(a));
        System.out.println("a / b        = " + a.divides(b));
        System.out.println("(a / b) * b  = " + a.divides(b).times(b));
        System.out.println("conj(a)      = " + a.conjugate());
        System.out.println("|a|          = " + a.abs());
        System.out.println("tan(a)       = " + a.tan());
        double[] res = Complex.arrayAbs(new Complex[]{a, b});
        System.out.println("|a| " + a.abs());
        System.out.println("|b| " + b.abs());
        System.out.println("复数数组的模");
        for (int i = 0; i < res.length; i++) {
            System.out.println("res " + res[i]);
        }
    }
}
