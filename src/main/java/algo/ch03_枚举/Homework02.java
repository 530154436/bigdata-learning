package algo.ch03_枚举;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

/**
 * 有N条绳子，它们的长度分别为𝐿_𝑖。如果从它们中切割出K条长度相同的绳子的话，
 * 这K条绳子每条最长能有多长？答案保留到小数点后2位。
 * 输入：
 *  N = 4
 *  K = 11
 *  L =  {8.02,  7.43,  4.57,  5.39}
 * 输出：
 *  2.00 (每条绳子分别可以得到4条、3条、2条、2条，共计11条)
 */
public class Homework02 {

    boolean isOk(int N, int K, double[] L, double length){
        int total = 0;
        for(int j=0; j<N; j++){
            total += (int) (L[j] / length);
            if(total >= K)
                return true;
        }
        return false;
    }

    public double maxKLength(int N, int K, double[] L){
        double low=0.0, high=0, mid;
        for (double v : L)
            if (v > high)
                high = v;

        double maxLen = 0;
        while(low <= high){
            mid = (low + high) / 2;

            // 向下保留2位小数
            BigDecimal bg = new BigDecimal(mid);
            mid = bg.setScale(2, RoundingMode.HALF_DOWN).doubleValue();

            if(isOk(N, K, L, mid)){
                if(mid > maxLen)
                    maxLen = mid;
                low = mid + 0.01;
            }else{
                high = mid - 0.01;
            }
        }
        return maxLen;
    }

    public static void main(String[] args){
        Homework02 h = new Homework02();
        int[] Ns = new int[]{4, 1, 3, 1, 5};
        int[] Ks = new int[]{11, 1, 1, 3, 4};
        double[][] Ls = new double[][]{
                {8.02, 7.43, 4.57, 5.39},
                {4},
                {8.02, 7.43, 4.57},
                {9},
                {4.1, 3.1, 4, 3.2, 1}
        };

        for(int i=0; i<Ns.length; i++){
            double maxLen = h.maxKLength(Ns[i], Ks[i], Ls[i]);
            System.out.println("序号："+ (i+1) + ", N="+Ns[i] + ", K="+Ks[i]
                    + ", L="+ Arrays.toString(Ls[i])+ ", 结果="+ maxLen);
        }
    }
}
