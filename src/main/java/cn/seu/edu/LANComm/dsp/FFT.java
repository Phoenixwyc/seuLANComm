package cn.seu.edu.LANComm.dsp;

/*************************************************************************
 *  Compilation:  javac FFT.java
 *  Execution:    java FFT N
 *  Dependencies: Complex.java
 *
 *  Compute the FFT and inverse FFT of a length N complex sequence.
 *  Bare bones implementation that runs in O(N log N) time. Our goal
 *  is to optimize the clarity of the code, rather than performance.
 *
 *  Limitations
 *  -----------
 *   -  assumes N is a power of 2
 *
 *   -  not the most memory efficient algorithm (because it uses
 *      an object type for representing complex numbers and because
 *      it re-allocates memory for the subarray, instead of doing
 *      in-place or reusing a single temporary array)
 *
 *************************************************************************/
public class FFT {
    // compute the FFT of x[], assuming its length is a power of 2
    public static Complex[] fft(Complex[] x) {
        int N = x.length;

        // base case
        if (N == 1){
            return new Complex[] { x[0] };
        }

        // radix 2 Cooley-Tukey FFT
        if (N % 2 != 0){
            { throw new RuntimeException("N is not a power of 2"); }
        }

        // fft of even terms
        Complex[] even = new Complex[N/2];
        for (int k = 0; k < N/2; k++) {
            even[k] = x[2*k];
        }
        Complex[] q = fft(even);

        // fft of odd terms
        Complex[] odd  = even;
        for (int k = 0; k < N/2; k++) {
            odd[k] = x[2*k + 1];
        }
        Complex[] r = fft(odd);

        // combine
        Complex[] y = new Complex[N];
        for (int k = 0; k < N/2; k++) {
            double kth = -2 * k * Math.PI / N;
            Complex wk = new Complex(Math.cos(kth), Math.sin(kth));
            y[k]       = q[k].plus(wk.times(r[k]));
            y[k + N/2] = q[k].minus(wk.times(r[k]));
        }
        return y;
    }


    public static Complex[] ifft(Complex[] x) {
        int N = x.length;
        Complex[] y = new Complex[N];

        // take conjugate
        for (int i = 0; i < N; i++) {
            y[i] = x[i].conjugate();
        }

        // compute forward FFT
        y = fft(y);

        // take conjugate again
        for (int i = 0; i < N; i++) {
            y[i] = y[i].conjugate();
        }

        // divide by N
        for (int i = 0; i < N; i++) {
            y[i] = y[i].scale(1.0 / N);
        }

        return y;

    }

    /**
     * 循环卷积
     * @param x
     * @param y
     * @return
     */
    public static Complex[] cconvolve(Complex[] x, Complex[] y) {

        // should probably pad x and y with 0s so that they have same length
        // and are powers of 2
        if (x.length != y.length) { throw new RuntimeException("Dimensions don't agree"); }

        int N = x.length;

        // compute FFT of each sequence，求值
        Complex[] a = fft(x);
        Complex[] b = fft(y);

        // point-wise multiply，点值乘法
        Complex[] c = new Complex[N];
        for (int i = 0; i < N; i++) {
            c[i] = a[i].times(b[i]);
        }

        // compute inverse FFT，插值
        return ifft(c);
    }



    public static Complex[] convolve(Complex[] x, Complex[] y) {
        Complex ZERO = new Complex(0, 0);

        //2n次数界，高阶系数为0.
        Complex[] a = new Complex[2*x.length];
        for (int i = 0; i < x.length; i++){
            a[i] = x[i];
        }
        for (int i = x.length; i < 2*x.length; i++){
            a[i] = ZERO;
        }

        Complex[] b = new Complex[2*y.length];
        for (int i = 0; i < y.length; i++){
            b[i] = y[i];
        }
        for (int i = y.length; i < 2*y.length; i++){
            b[i] = ZERO;
        }

        return cconvolve(a, b);
    }

    public static void show(Complex[] x, String title) {
        System.out.println(title);
        System.out.println("-------------------");
        for (int i = 0; i < x.length; i++) {
            System.out.println(x[i]);
        }
        System.out.println();
    }

    public static void main(String[] args) {
        int N=8;
        Complex[] x = new Complex[N];

        // original data
        for (int i = 0; i < N; i++) {
            x[i] = new Complex(i, 0);
            x[i] = new Complex(-2*Math.random() + 1, 0);
        }
        show(x, "x");

        // FFT of original data
        Complex[] y = fft(x);
        show(y, "y = fft(x)");

        // take inverse FFT
        Complex[] z = ifft(y);
        show(z, "z = ifft(y)");

        // circular convolution of x with itself
        Complex[] c = cconvolve(x, x);
        show(c, "c = cconvolve(x, x)");

        // linear convolution of x with itself
        Complex[] d = convolve(x, x);
        show(d, "d = convolve(x, x)");
    }
}
/*********************************************************************
 % java FFT 8
 x
 -------------------
 -0.35668879080953375
 -0.6118094913035987
 0.8534269560320435
 -0.6699697478438837
 0.35425500561437717
 0.8910250650549392
 -0.025718699518642918
 0.07649691490732002

 y = fft(x)
 -------------------
 0.5110172121330208
 -1.245776663065442 + 0.7113504894129803i
 -0.8301420417085572 - 0.8726884066879042i
 -0.17611092978238008 + 2.4696418005143532i
 1.1395317305034673
 -0.17611092978237974 - 2.4696418005143532i
 -0.8301420417085572 + 0.8726884066879042i
 -1.2457766630654419 - 0.7113504894129803i

 z = ifft(y)
 -------------------
 -0.35668879080953375
 -0.6118094913035987 + 4.2151962932466006E-17i
 0.8534269560320435 - 2.691607282636124E-17i
 -0.6699697478438837 + 4.1114763914420734E-17i
 0.35425500561437717
 0.8910250650549392 - 6.887033953004965E-17i
 -0.025718699518642918 + 2.691607282636124E-17i
 0.07649691490732002 - 1.4396387316837096E-17i

 c = cconvolve(x, x)
 -------------------
 -1.0786973139009466 - 2.636779683484747E-16i
 1.2327819138980782 + 2.2180047699856214E-17i
 0.4386976685553382 - 1.3815636262919812E-17i
 -0.5579612069781844 + 1.9986455722517509E-16i
 1.432390480003344 + 2.636779683484747E-16i
 -2.2165857430333684 + 2.2180047699856214E-17i
 -0.01255525669751989 + 1.3815636262919812E-17i
 1.0230680492494633 - 2.4422465262488753E-16i

 d = convolve(x, x)
 -------------------
 0.12722689348916738 + 3.469446951953614E-17i
 0.43645117531775324 - 2.78776395788635E-18i
 -0.2345048043334932 - 6.907818131459906E-18i
 -0.5663280251946803 + 5.829891518914417E-17i
 1.2954076913348198 + 1.518836016779236E-16i
 -2.212650940696159 + 1.1090023849928107E-17i
 -0.018407034687857718 - 1.1306778366296569E-17i
 1.023068049249463 - 9.435675069681485E-17i
 -1.205924207390114 - 2.983724378680108E-16i
 0.796330738580325 + 2.4967811657742562E-17i
 0.6732024728888314 - 6.907818131459906E-18i
 0.00836681821649593 + 1.4156564203603091E-16i
 0.1369827886685242 + 1.1179436667055108E-16i
 -0.00393480233720922 + 1.1090023849928107E-17i
 0.005851777990337828 + 2.512241462921638E-17i
 1.1102230246251565E-16 - 1.4986790192807268E-16i

 *********************************************************************/
