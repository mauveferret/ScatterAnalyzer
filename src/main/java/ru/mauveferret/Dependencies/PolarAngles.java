package ru.mauveferret.Dependencies;

public class PolarAngles {

    //from 0 to 90 from normal to sufrace (-x Axis)
    private double polar;
    //from 0 to 360 from Z axis (in which the beam strikes)
    private double azimuth;

    public PolarAngles(double polarCos, double azimuthCos, double x,double y) {
       /// System.out.println(azimuthCos);
        azimuth = 57.2958*Math.acos(azimuthCos);
        polar = 57.2958*Math.acos(polarCos);
        //polar += (x>0 && (azimuth<=90)) ? 90 : 0;
        //polar += (x>0 && (azimuth>90)) ? 180 : 0;
        //polar += (x<0 && (azimuth>90)) ? 270 : 0;
        //FIXME
        //if (y<0) azimuth =360-azimuth;
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

    public boolean doesAzimuthAngleMatch(double phi, double dPhi){

        if ((phi>dPhi && phi<360-dPhi) || (phi>45))
            return (Math.abs(phi - azimuth)<dPhi);
        else return (azimuth < dPhi || azimuth>360-dPhi);
    }

    public boolean doesPolarAngleMatch(double theta, double dTheta){
        return (Math.abs(theta-polar)<dTheta);
    }

    private void cartesianToAngles(double cosx,double cosy,double cosz){


        // SDTrimSP style Cartesian system ( -x is a normal, projectiles are in ZX plane) TODO
        if ((cosx>0 & cosy>0) || (cosx<0 & cosy<0)) azimuth = Math.atan(cosy / cosx)*57.2958;
        else if ((cosx<0 && cosy>0) || (cosx>0 && cosy<0)) azimuth = 180-Math.atan(-1*(cosy/cosx))*57.2958;
        if (cosy<0) azimuth=360-azimuth;
        double absPolar = Math.atan(Math.sqrt(cosx * cosx + cosy * cosy)/Math.abs(cosz))*57.2958;;

        polar = absPolar;
        //if (azimuth<=90 && cosz>=0) polar = absPolar;
        //else  if (azimuth<=90 && cosz<0) polar=90+absPolar;
        //else  if (azimuth>90 && cosz<0) polar=180+absPolar;
        //else  polar=270+absPolar;

    }


}
