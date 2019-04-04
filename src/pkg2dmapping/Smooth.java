/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg2dmapping;

/**
 *
 * @author dhuant
 */
public class Smooth {
    private static final int x = 0;
    private static final int y = 1;
    private double[] getMids(double [] a, double [] b){
        if(a.length == 3){
            double [] m = {((a[0]+b[0])/2),((a[1]+b[1])/2),((a[2]+b[2])/2)};
            return m;
        }
        double [] m = {((a[0]+b[0])/2),((a[1]+b[1])/2)};
        return m;
    }
    private double [][] getMidArr(double [][] Arr){
        double[][] MidArr = new double[Arr.length][Arr[0].length];
        for (int i = 0; i < Arr.length - 1; i++) {
            MidArr[i] = this.getMids(Arr[i], Arr[i+1]);
        }
        //this is a lazy fix, without this line I would need to make every array shifted over by 1;
        //however, with this line I connect a node to itself each time which is no harm past wasted time
        MidArr[MidArr.length-1] = this.getMids(Arr[Arr.length-1],Arr[Arr.length-1]);
        return  MidArr;
    }
    private double [][] MidAdded(double [][] Arr, double [][] MidArr){
        double[][] Comp = new double[Arr.length+MidArr.length][2];
        int counter = 0;
        for (int i = 0; i < Arr.length; i++) {
            Comp[counter] = Arr[i];
            counter+=2;
        }
        counter = 1;
        for (int i = 0; i < MidArr.length; i++) {
            Comp[counter] = MidArr[i];
            counter+=2;
        }
        return Comp;
    }
    public void Connect(double [][] Arr){
        //
    }
    public double[][] retSmooth(double [][] Shape){
        double[][] Dope = this.getMidArr(this.MidAdded(Shape, this.getMidArr(Shape)));
        return Dope;
    }
    public double[][] SmoothOut(double [][] Shape, int a){
        double[][] Dope = new double[a*Shape.length][2];
        Dope = Shape;
        for (int i = 0; i < a; i++) {
            Dope = this.retSmooth(Dope);
        }
        return Dope;
    }
    
    public void print(double [][] Arr){
        for (double[] ds : Arr) {
            System.out.print("{");
            for (double d : ds) {
                System.out.print(d+", ");
            }
            System.out.print("}, ");
        }
        System.out.println();
    }
    public void print(double [] Arr){
            System.out.print("{");
            for (double d : Arr) {
                System.out.print(d+", ");
            }
            System.out.println("}");
    }

}
