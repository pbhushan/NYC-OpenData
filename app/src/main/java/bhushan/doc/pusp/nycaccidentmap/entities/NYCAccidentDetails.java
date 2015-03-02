package bhushan.doc.pusp.nycaccidentmap.entities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * Created by PBhushan on 2/28/2015.
 */
public class NYCAccidentDetails {
    private String mDate;
    private String mTime;
    private String mBorough;
    private String mZipCode;
    private double mLatitude;
    private double mLongitude;
    private String mStreetName;
    private String mCrossStreetName;
    private int mPersonInjured;
    private int mPersonKilled;
    private int mPedestrianInjured;
    private int mPedestrianKilled;
    private int mCyclistInjured;
    private int mCyclistKilled;
    private int mMotoristInjured;
    private int mMotoristKilled;
    private String mFactorVehicle1;
    private String mFactorVehicle2;
    private String mUniqueKey;
    private String mVehicleType1;
    private String mVehicleType2;


    public String getDate() {
        return mDate;
    }

    public void setDate(String mDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat stringFormat = new SimpleDateFormat("dd-MMM-yyyy");
        String dateJson = mDate.substring(0, 10);
        try {
            Date date = dateFormat.parse(dateJson);
            this.mDate = stringFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String mTime) {
        this.mTime = mTime;
    }

    public String getBorough() {
        return mBorough;
    }

    public void setBorough(String mBorough) {
        this.mBorough = mBorough;
    }

    public String getZipCode() {
        return mZipCode;
    }

    public void setZipCode(String mZipCode) {
        this.mZipCode = mZipCode;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double mLatitude) {
        this.mLatitude = mLatitude;
        /* To avoid overlapping/clustering offset is added
         * Best way to use Google Map Utility Library or clusterkraf for clustering
         */
        Random random = new Random();
        int itOff = random.nextInt(55) + 5;
        double offSet = 0.0d;
        if (itOff < 20)
            offSet = itOff / 200000d;
        else if (itOff >= 20 && itOff < 40)
            offSet = itOff / 400000d;
        else
            offSet = itOff / 600000d;

        if (getFlagColor() == FlagColor.RED) {

            this.mLatitude = this.mLatitude + offSet;
        }
        if (getFlagColor() == FlagColor.BLUE) {

            this.mLatitude = this.mLatitude + offSet;
        }
        if (getFlagColor() == FlagColor.GREEN) {

            this.mLatitude = this.mLatitude + offSet;
        }

    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double mLongitude) {
        this.mLongitude = mLongitude;
        Random random = new Random();
        int itOff = random.nextInt(55) + 5;
        double offSet = 0.0d;
        if (itOff < 20)
            offSet = itOff / 200000d;
        else if (itOff >= 20 && itOff < 40)
            offSet = itOff / 400000d;
        else
            offSet = itOff / 600000d;

        if (getFlagColor() == FlagColor.RED) {

            this.mLongitude = this.mLongitude + offSet;
        }
        if (getFlagColor() == FlagColor.BLUE) {

            this.mLongitude = this.mLongitude + offSet;
        }
        if (getFlagColor() == FlagColor.GREEN) {

            this.mLongitude = this.mLongitude + offSet;
        }

    }


    public String getStreetName() {
        return mStreetName;
    }

    public void setStreetName(String mStreetName) {
        this.mStreetName = mStreetName;
    }

    public String getCrossStreetName() {
        return mCrossStreetName;
    }

    public void setCrossStreetName(String mCrossStreetName) {
        this.mCrossStreetName = mCrossStreetName;
    }

    public int getPersonInjured() {
        return mPersonInjured;
    }

    public void setPersonInjured(int mPersonInjured) {
        this.mPersonInjured = mPersonInjured;
    }

    public int getPersonKilled() {
        return mPersonKilled;
    }

    public void setPersonKilled(int mPersonKilled) {
        this.mPersonKilled = mPersonKilled;
    }

    public int getPedestrianInjured() {
        return mPedestrianInjured;
    }

    public void setPedestrianInjured(int mPedestrianInjured) {
        this.mPedestrianInjured = mPedestrianInjured;
    }

    public int getPedestrianKilled() {
        return mPedestrianKilled;
    }

    public void setPedestrianKilled(int mPedestrianKilled) {
        this.mPedestrianKilled = mPedestrianKilled;
    }

    public int getCyclistInjured() {
        return mCyclistInjured;
    }

    public void setCyclistInjured(int mCyclistInjured) {
        this.mCyclistInjured = mCyclistInjured;
    }

    public int getCyclistKilled() {
        return mCyclistKilled;
    }

    public void setCyclistKilled(int mCyclistKilled) {
        this.mCyclistKilled = mCyclistKilled;
    }

    public int getMotoristInjured() {
        return mMotoristInjured;
    }

    public void setMotoristInjured(int mMotoristInjured) {
        this.mMotoristInjured = mMotoristInjured;
    }

    public int getMotoristKilled() {
        return mMotoristKilled;
    }

    public void setMotoristKilled(int mMotoristKilled) {
        this.mMotoristKilled = mMotoristKilled;
    }

    public String getFactorVehicle1() {
        return mFactorVehicle1;
    }

    public void setFactorVehicle1(String mFactorVehicle1) {
        this.mFactorVehicle1 = mFactorVehicle1;
    }

    public String getFactorVehicle2() {
        return mFactorVehicle2;
    }

    public void setFactorVehicle2(String mFactorVehicle2) {
        this.mFactorVehicle2 = mFactorVehicle2;
    }


    public String getUniqueKey() {
        return mUniqueKey;
    }

    public void setUniqueKey(String mUniqueKey) {
        this.mUniqueKey = mUniqueKey;
    }

    public String getVehicleType1() {
        return mVehicleType1;
    }

    public void setVehicleType1(String mVehicleType1) {
        this.mVehicleType1 = mVehicleType1;
    }

    public String getVehicleType2() {
        return mVehicleType2;
    }

    public void setVehicleType2(String mVehicleType2) {
        this.mVehicleType2 = mVehicleType2;
    }


    public FlagColor getFlagColor() {
        if (mPersonInjured == 0 && mPersonKilled == 0)
            return FlagColor.GREEN;
        else if (mPersonInjured != 0 && mPersonKilled == 0)
            return FlagColor.BLUE;
        else
            return FlagColor.RED;
    }

    public enum FlagColor {
        RED, BLUE, GREEN
    }


}