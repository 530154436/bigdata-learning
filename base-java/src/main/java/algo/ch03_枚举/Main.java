package algo.ch03_枚举;
import java.util.Scanner;


public class Main {
    public static int times(long x, int a){
        int times = 0;
        x = x / a;
        while(x > 0){
            times += 1;
            x = x / a;
        }
        return times;
    }
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        long x = sc.nextLong();

        int n3_times=times(x, 3);
        int n5_times=times(x, 5);
        int n7_times=times(x, 7);
        int answer = 0;
        for(int n3=0; n3 <= n3_times; n3++){
            for(int n5=0; n5 <= n5_times; n5++){
                for(int n7=0; n7 <= n7_times; n7++){
                    double res = Math.pow(3, n3)*Math.pow(5, n5)*Math.pow(7, n7);
                    if(res <= x && res >= 3){
                        answer += 1;
                    }
                }
            }
        }
        System.out.println(answer);
    }
}