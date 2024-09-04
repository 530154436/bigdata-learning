package udf;


import org.apache.commons.math3.distribution.BetaDistribution;
import org.apache.hadoop.hive.ql.exec.UDF;

/***
 * 汤普森采样： 基于Beta分布
 */
public class Beta extends UDF {
    double alpha0 = 0.5D;
    double beta0 = 0.5D;

    public Beta() {
    }

    public Double evaluate(double alpha, double beta) {
        alpha = Math.max(alpha, 0.0D);
        beta = Math.max(beta, 0.0D);
        return betasSampler(this.alpha0 + alpha, this.beta0 + beta);
    }

    public static double betasSampler(double alpha, double beta) {
        BetaDistribution beta2 = new BetaDistribution(alpha, beta);
        return beta2.sample();
    }

    public static void main(String[] args) {
        for(int i = 0; i < 10000; ++i) {
            System.out.println(betasSampler(3.0D, 2.0D));
        }
    }
}