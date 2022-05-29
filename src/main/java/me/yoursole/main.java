package me.yoursole;

import java.util.Arrays;

public class main {
    public static void main(String[] args){

        QuadRegression r = new QuadRegression(new double[]{1,2,3,4}, new double[]{500,1000,1250,1375});
        Vector3 v = r.calcQuad();

        double vertex = (-1 * v.getY()) / (2 * v.getX());
        double apogee = (v.getX() * Math.pow(vertex, 2)) + (v.getY() * vertex) + v.getZ();

        System.out.println(apogee);


    }
}


class QuadRegression{
    private final double[] dataX;
    private final double[] dataY;

    protected QuadRegression(double[] dataX, double[] dataY){
        if(dataX.length != dataY.length) throw new IllegalArgumentException();

        this.dataX = dataX;
        this.dataY = dataY;
    }

    protected Vector3 calcQuad() {
        double x4 = Arrays.stream(dataX).map(operand -> Math.pow(operand, 4)).sum();
        double x3 = Arrays.stream(dataX).map(operand -> Math.pow(operand, 3)).sum();
        double x2 = Arrays.stream(dataX).map(operand -> Math.pow(operand, 2)).sum();
        double x1 = Arrays.stream(dataX).sum();
        double n = dataX.length;

        double x2y = 0;
        for (int i = 0; i < dataX.length; i++) {
            x2y += Math.pow(dataX[i], 2) * dataY[i];
        }

        double xy = 0;
        for (int i = 0; i < dataX.length; i++) {
            xy += dataX[i] * dataY[i];
        }

        double y1 = Arrays.stream(dataY).sum();

        Matrix3 m = new Matrix3(new double[][]{
                {x4, x3, x2},
                {x3, x2, x1},
                {x2, x1, n}
        });

        Matrix3 mInv = m.inverse();

        Vector3 v = new Vector3(x2y, xy, y1);

        return mInv.transform(v);
    }
}

class Matrix3{
     private final double[][] matx;

     protected Matrix3(double[][] matx){
        if(matx.length != 3 || matx[0].length != 3) throw new IllegalArgumentException();

        this.matx = matx;
    }

    protected Vector3 transform(Vector3 toMult){
        double[] out = new double[3];

        for(int i = 0; i < 3; i++) for(int j = 0; j < 3; j++) {
            out[i] += this.matx[i][j] * toMult.get(j);
        }
        return new Vector3(out[0], out[1], out[2]);
    }

    protected double det(){
         double det = 0;
         for (int i = 0; i < 3; i++) {
             double[][] subMatrix = getSubmatrix(0, i);
             det += this.matx[0][i] * (subMatrix[0][0] * subMatrix[1][1] - subMatrix[0][1] * subMatrix[1][0]) * (i % 2 == 0 ? 1 : -1) ;
         }
         return det;
    }

    protected Matrix3 inverse(){
        double det = this.det();
        double[][] inv = new double[3][3];

        for (int i = 0; i < 3; i++) for(int j = 0; j < 3; j++){
            double[][] subMatrix = getSubmatrix(i, j);

            double coFactor = subMatrix[0][0] * subMatrix[1][1] - subMatrix[0][1] * subMatrix[1][0];
            inv[j][i] = (coFactor * ((i + j) % 2 == 0 ? 1 : -1)) / det;
        }

        return new Matrix3(inv);
    }

    /**
     * note that x and y are the coordinates of the rejection set
     * @param x
     * @param y
     * @return
     */
    protected double[][] getSubmatrix(int x, int y){
        double[][] sub = new double[2][2];

        int xPass = 0;
        int yPass = 0;

        for(int i = 0; i < 3; i++) {
            if (i == x) {
                xPass++;
                continue;
            }
            for (int j = 0; j < 3; j++) {
                if (j == y) {
                    yPass++;
                    continue;
                }

                sub[i - xPass][j - yPass] = this.matx[i][j];
            }
            yPass = 0;
        }
        return sub;
    }

    protected double[][] getMatrix(){
        return this.matx;
    }

}

class Vector3{
    private double x;
    private double y;
    private double z;

    protected Vector3(double x, double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    protected double get(int index){
        return (index == 0) ? x : (index == 1) ? y : (index == 2) ? z : Double.MIN_VALUE;
    }


    protected double getX() {
        return x;
    }

    protected void setX(double x) {
        this.x = x;
    }

    protected double getY() {
        return y;
    }

    protected void setY(double y) {
        this.y = y;
    }

    protected double getZ() {
        return z;
    }

    protected void setZ(double z) {
        this.z = z;
    }

    @Override
    public String toString(){
        return x + "," + y + "," + z;
    }
}