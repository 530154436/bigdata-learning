package algo;

import java.util.Arrays;
import java.util.Scanner;

public class InputOutput {
    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);

        int n = sc.nextInt();
        System.out.println("n=" + n);

        int[] array = new int[n];
        for(int i=0; i<n; i++){
            array[i] = sc.nextInt();
        }
        System.out.println(Arrays.toString(array));
    }
}
