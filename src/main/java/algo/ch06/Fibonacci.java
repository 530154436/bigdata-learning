package algo.ch06;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/***
 * 斐波拉契数列
 */
public class Fibonacci {

    /***
     * 递归算法
     */
    public int recursion(int n){
        if(n <= 2)
            return 1;
        else
            return recursion(n-1) + recursion(n-2);
    }

    /***
     * 非递归算法
     */
    public int nonRecursion(int n){
        if(n<=2)
            return 1;
        int[] arr = new int[n+1];
        arr[1] = 1;
        arr[2] = 1;
        for(int i=3; i<=n; i++)
            arr[i] = arr[i-1] + arr[i-2];
        return arr[n];
    }

    public static void main(String[] args) {
        Fibonacci fib = new Fibonacci();

        int seq_size = 10;

        int[] array = new int[seq_size];
        for(int i=0; i<seq_size; i++)
            array[i] = fib.recursion(i+1);
        System.out.println(Arrays.toString(array));

        array = new int[seq_size];
        for(int i=0; i<seq_size; i++)
            array[i] = fib.nonRecursion(i+1);
        System.out.println(Arrays.toString(array));
    }
}
