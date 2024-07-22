package algo.ch05_动态规划;

/**
 * 矩阵连乘问题
 */
public class MatrixChain {

public long solution(long[] dim){
    int matrixNum = dim.length - 1;
    long[][] memoTable = new long[matrixNum + 1][matrixNum + 1];

    int i, j, len, k;
    // 单个矩阵的情形，定义数乘次数为0
    for(i=1; i<=matrixNum; i++)
        memoTable[i][i] = 0;

    // 计算长度为len的矩阵链最优值
    for(len=2; len<=matrixNum; len++){
        for(i=1; i<=matrixNum-len+1; i++) {     //矩阵链的开始矩阵下标
            j = i+len-1;                        //矩阵链的结束矩阵下标
            memoTable[i][j] = 100000000;        //预定义的一个充分大数
            for(k=i; k<j; k++) { //枚举划分位置
                long ans = memoTable[i][k] + memoTable[k+1][j] + dim[i-1]*dim[k]*dim[j];
                if (ans < memoTable[i][j]){ // 更新最优信息
                    memoTable[i][j] = ans;
                    System.out.println("i=" + i + ", j=" + j + ", k=" + k + ": " + ans);
                }
            }
        }
    }
    return memoTable[1][matrixNum];
}

    public static void main(String[] args) {
        // long[] dim = {30, 35, 15, 5, 10, 20, 25};
        // System.out.println(new MatrixChain().solution(dim));

        long[] dim = {50, 10, 40, 30, 5};
        System.out.println(new MatrixChain().solution(dim));
    }
}
