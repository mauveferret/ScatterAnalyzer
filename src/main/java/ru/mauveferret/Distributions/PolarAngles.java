package ru.mauveferret.Distributions;

public class PolarAngles {

    //from 0 to 360 from normal to sufrace
    private double polar;
    //from 0 to 180 from X axis (in which the beam strikes)
    private double azimuth;

    public PolarAngles(double polar, double azimuth) {
        this.polar = polar;
        this.azimuth = azimuth;
    }

    public PolarAngles(double cosx, double cosy, double cosz) {
        cartesianToAngles(cosx,cosy,cosz);
    }

    public boolean doesAngleMatch(double angle, double delta, boolean isPolar){

        if (isPolar){
            if (angle>delta && angle<360-delta) return (Math.abs(angle - polar)<delta);
            else return (polar < delta || polar>360-delta);
        }
        else {
            if (angle>delta && angle<180-delta) return (Math.abs(angle - azimuth)<delta);
            else return (azimuth<delta || azimuth>180-delta);
        }
    }

    private void cartesianToAngles(double cosx,double cosy,double cosz){


        //z is  directed normally from the surface
        if ((cosx>0 & cosy>0) || (cosx<0 & cosy<0)) azimuth = Math.atan(cosy / cosx)*57.2958;
        else if ((cosx<0 && cosy>0) || (cosx>0 && cosy<0)) azimuth = Math.atan(-1*(cosy/cosx))*57.2958;

        double absPolar = Math.atan(Math.sqrt(cosx * cosx + cosy * cosy) /Math.abs(cosz));

        if (azimuth<=90 && cosz>=0) polar = absPolar;
        else  if (azimuth<=90 && cosz<0) polar=90+absPolar;
        else  if (azimuth>90 && cosz<0) polar=180+absPolar;
        else  polar=270+absPolar;
    }


}
