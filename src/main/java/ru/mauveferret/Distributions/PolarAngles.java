package ru.mauveferret.Distributions;

public class PolarAngles {

    //from 0 to 360 from normal to sufrace (-x Axis)
    private double polar;
    //from 0 to 360 from Z axis (in which the beam strikes)
    private double azimuth;


    public PolarAngles(double polarCos, double azimuthCos, double x,double y) {
        azimuth = 57.2958*Math.acos(azimuthCos);
        polar = 57.2958*Math.acos(polarCos);
        polar += (x>0 && (azimuth<=90)) ? 90 : 0;
        polar += (x>0 && (azimuth>90)) ? 180 : 0;
        polar += (x<0 && (azimuth>90)) ? 270 : 0;
        azimuth += ((y>0) ? 0 : 180);
        //System.out.println(azimuth+" "+polar);
    }

    public PolarAngles(double cosx, double cosy, double cosz) {
        cartesianToAngles(cosx,cosy,cosz);
    }

    public double getPolar() {
        return polar;
    }

    public double getAzimuth() {
        return azimuth;
    }

    public boolean doesAngleMatch(double angle, double delta, boolean isPolar){

        double toCheck = (isPolar)? polar : azimuth;
        if (angle>delta && angle<360-delta) return (Math.abs(angle - toCheck)<delta);
        else return (toCheck < delta || toCheck>360-delta);
    }

    private void cartesianToAngles(double cosx,double cosy,double cosz){


        // TRIM style Cartesian system ( -x is a normal, projectiles are in ZX plane) TODO
        if ((cosx>0 & cosy>0) || (cosx<0 & cosy<0)) azimuth = Math.atan(cosy / cosx)*57.2958;
        else if ((cosx<0 && cosy>0) || (cosx>0 && cosy<0)) azimuth = 180-Math.atan(-1*(cosy/cosx))*57.2958;

        double absPolar = Math.atan(Math.sqrt(cosx * cosx + cosy * cosy)/Math.abs(cosz))*57.2958;;

        if (azimuth<=90 && cosz>=0) polar = absPolar;
        else  if (azimuth<=90 && cosz<0) polar=90+absPolar;
        else  if (azimuth>90 && cosz<0) polar=180+absPolar;
        else  polar=270+absPolar;

    }


}
