package edu.eci.arsw.math;

import java.util.ArrayList;
import java.util.List;

///  <summary>
///  An implementation of the Bailey-Borwein-Plouffe formula for calculating hexadecimal
///  digits of pi.
///  https://en.wikipedia.org/wiki/Bailey%E2%80%93Borwein%E2%80%93Plouffe_formula
///  *** Translated from C# code: https://github.com/mmoroney/DigitsOfPi ***
///  </summary>
public class PiDigits {

    private static int DigitsPerSum = 8;
    private static double Epsilon = 1e-17;

    
    /**
     * Returns a range of hexadecimal digits of pi.
     * @param start The starting location of the range.
     * @param count The number of digits to return
     * @return An array containing the hexadecimal digits.
     */
    public static byte[] getDigits(int start, int count, int numThreads) {
        List<PiThread> threads = new ArrayList<>();
        byte[] fResult = new byte[count];
        for(int i=start;i<numThreads;i++){
            int p_start = (count / numThreads) * i;
            int p_count = ((count / numThreads) * (i+1));;
            if(i!=start){
                p_start+=1;
            }
            if(i+1==numThreads){
                if (p_count<count){p_count+=1;}
            }

            System.out.println("p_start: "+ p_start + " p_count: "+p_count);
            PiThread p_thread = new PiThread(p_start, p_count);
            threads.add(p_thread);
        }

        for (PiThread t:threads){
            t.start();
            System.out.println("Starting thread");
            try{
                t.join();
            } catch (Exception e){}
            
        }
        for (PiThread t:threads){
            System.out.println("Looking in pidigits for the one that ends on "+t.count);
            byte[] pByte = t.result;
            System.out.println(pByte);

            for(int i = t.original_start;i<t.count;i++){
                // System.out.println("analyzing i="+i+" start "+t.original_start+ " count "+count);
                // System.out.println(pByte[i]);

                fResult[i]=pByte[i];
            }
        }
        return fResult;


    }

    /// <summary>
    /// Returns the sum of 16^(n - k)/(8 * k + m) from 0 to k.
    /// </summary>
    /// <param name="m"></param>
    /// <param name="n"></param>
    /// <returns></returns>
    private static double sum(int m, int n) {
        double sum = 0;
        int d = m;
        int power = n;

        while (true) {
            double term;

            if (power > 0) {
                term = (double) hexExponentModulo(power, d) / d;
            } else {
                term = Math.pow(16, power) / d;
                if (term < Epsilon) {
                    break;
                }
            }

            sum += term;
            power--;
            d += 8;
        }

        return sum;
    }

    /// <summary>
    /// Return 16^p mod m.
    /// </summary>
    /// <param name="p"></param>
    /// <param name="m"></param>
    /// <returns></returns>
    private static int hexExponentModulo(int p, int m) {
        int power = 1;
        while (power * 2 <= p) {
            power *= 2;
        }

        int result = 1;

        while (power > 0) {
            if (p >= power) {
                result *= 16;
                result %= m;
                p -= power;
            }

            power /= 2;

            if (power > 0) {
                result *= result;
                result %= m;
            }
        }

        return result;
    }

}
