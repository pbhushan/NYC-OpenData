package bhushan.doc.pusp.nycaccidentmap;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;

import bhushan.doc.pusp.nycaccidentmap.entities.NYCAccidentDetails;

/**
 * Created by PBushan on 3/2/2015.
 */
public class NYCAccidentMapWindow implements GoogleMap.InfoWindowAdapter {
    TextView dateTime;
    TextView location;
    TextView casualty;
    TextView vehicleFactor;
    TextView vehicleType;
    HashMap<Marker,NYCAccidentDetails> mapMarkerDetail = new HashMap<>();
    MapsActivity activity;
    public NYCAccidentMapWindow(HashMap<Marker,NYCAccidentDetails> mapMarkerDetails,MapsActivity mapsActivity){
        this.mapMarkerDetail = mapMarkerDetails;
        this.activity = mapsActivity;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        NYCAccidentDetails nycAccidentDetails = mapMarkerDetail.get(marker);
        View view = activity.getLayoutInflater().inflate(R.layout.infomapwindow, null);
        dateTime = (TextView) view.findViewById(R.id.incident_date);
        location = (TextView) view.findViewById(R.id.area);
        casualty = (TextView) view.findViewById(R.id.casualty);
        vehicleFactor = (TextView) view.findViewById(R.id.vehicle_factor);
        vehicleType = (TextView) view.findViewById(R.id.vehicle_type);

        dateTime.setText("Collision happened on "+nycAccidentDetails.getDate()+" at "+nycAccidentDetails.getTime()+" EST");
        location.setText("Between On Street "+nycAccidentDetails.getStreetName()+" and Cross Street "+nycAccidentDetails.getCrossStreetName()+" ("+
                nycAccidentDetails.getBorough()+")");
        if(nycAccidentDetails.getFlagColor() == NYCAccidentDetails.FlagColor.GREEN){
            casualty.setText("No Casualty");
        }else if(nycAccidentDetails.getFlagColor() == NYCAccidentDetails.FlagColor.BLUE){
            int personInjured = nycAccidentDetails.getPersonInjured();
            int pedestrianInjured = nycAccidentDetails.getPedestrianInjured();
            int cyclistInjured = nycAccidentDetails.getCyclistInjured();
            int motoristInjured = nycAccidentDetails.getMotoristInjured();
            String casualtyText="";
            if(cyclistInjured==0&&pedestrianInjured>0&&motoristInjured==0) {
                casualtyText += "No. of Person injured - " + personInjured + "\n" + "Pedestrian Injured - " + pedestrianInjured;

            }
            else if(cyclistInjured>0&&pedestrianInjured==0 && motoristInjured==0){
                casualtyText += "No. of Person injured - " + personInjured
                        + "\n" + "Cyclist Injured - " + cyclistInjured;
            }
            else if(cyclistInjured>0&&pedestrianInjured>0&&motoristInjured==0 ){
                casualtyText += "No. of Person injured - " + personInjured
                        + "\n" + "Pedestrian Injured - " + pedestrianInjured + "\n" + "Cyclist Injured - " + cyclistInjured;
            }
            else if(cyclistInjured ==0&& pedestrianInjured==0 && motoristInjured>0){
                casualtyText += "No. of Person injured - " + personInjured
                        +  "\n" + "Motorist Injured - " + motoristInjured;
            }
            else if(cyclistInjured ==0&& pedestrianInjured>0 && motoristInjured>0){
                casualtyText += "No. of Person injured - " + personInjured
                        + "\n" + "Pedestrian Injured - " + pedestrianInjured + "\n" + "Motorist Injured - " + motoristInjured;
            }
            else{
                casualtyText += "No. of Person injured - " + personInjured + "\n" + "Pedestrian Injured - " + pedestrianInjured
                        + "\n" + "Cyclist Injured - " + cyclistInjured + "\n" + "Motorist Injured - " + motoristInjured;
            }
            casualty.setText(casualtyText);
        }
        else if(nycAccidentDetails.getFlagColor() == NYCAccidentDetails.FlagColor.RED){
            int personInjured = nycAccidentDetails.getPersonInjured();
            int pedestrianInjured = nycAccidentDetails.getPedestrianInjured();
            int cyclistInjured = nycAccidentDetails.getCyclistInjured();
            int motoristInjured = nycAccidentDetails.getMotoristInjured();
            int personKilled = nycAccidentDetails.getPersonKilled();
            int pedestrianKilled = nycAccidentDetails.getPedestrianKilled();
            int cyclistKilled = nycAccidentDetails.getCyclistKilled();
            int motoristKilled = nycAccidentDetails.getMotoristKilled();
            String casualtyText="";

            if(cyclistKilled==0&&pedestrianKilled>0&&motoristKilled==0) {
                casualtyText = "No. of Person killed - " + personKilled + "\n" + "Pedestrian killed - " + pedestrianKilled;

            }
            else if(cyclistKilled>0&&pedestrianKilled==0 && motoristKilled==0){
                casualtyText = "No. of Person killed - " + personKilled
                        + "\n" + "Cyclist killed - " + cyclistInjured;
            }
            else if(cyclistKilled>0&&pedestrianKilled>0&&motoristKilled==0 ){
                casualtyText = "No. of Person killed - " + personKilled
                        + "\n" + "Pedestrian killed - " + pedestrianInjured + "\n" + "Cyclist killed - " + cyclistKilled;
            }
            else if(cyclistKilled ==0&& pedestrianKilled==0 && motoristKilled>0){
                casualtyText = "No. of Person killed - " + personKilled
                        +  "\n" + "Motorist killed - " + motoristKilled;
            }
            else if(cyclistKilled ==0&& pedestrianKilled>0 && motoristKilled>0){
                casualtyText = "No. of Person killed - " + personKilled
                        + "\n" + "Pedestrian killed - " + pedestrianKilled + "\n" + "Motorist killed - " + motoristKilled;
            }
            else{
                casualtyText = "No. of Person killed - " + personKilled + "\n" + "Pedestrian killed - " + pedestrianInjured
                        + "\n" + "Cyclist killed - " + cyclistKilled + "\n" + "Motorist killed - " + motoristKilled;
            }



            if (personInjured>0){
                if(cyclistInjured==0&&pedestrianInjured>0&&motoristInjured==0) {
                    casualtyText += "No. of Person injured - " + personInjured + "\n" + "Pedestrian Injured - " + pedestrianInjured;

                }
                else if(cyclistInjured>0&&pedestrianInjured==0 && motoristInjured==0){
                    casualtyText += "No. of Person injured - " + personInjured
                            + "\n" + "Cyclist Injured - " + cyclistInjured;
                }
                else if(cyclistInjured>0&&pedestrianInjured>0&&motoristInjured==0 ){
                    casualtyText += "No. of Person injured - " + personInjured
                            + "\n" + "Pedestrian Injured - " + pedestrianInjured + "\n" + "Cyclist Injured - " + cyclistInjured;
                }
                else if(cyclistInjured ==0&& pedestrianInjured==0 && motoristInjured>0){
                    casualtyText += "No. of Person injured - " + personInjured
                            +  "\n" + "Motorist Injured - " + motoristInjured;
                }
                else if(cyclistInjured ==0&& pedestrianInjured>0 && motoristInjured>0){
                    casualtyText += "No. of Person injured - " + personInjured
                            + "\n" + "Pedestrian Injured - " + pedestrianInjured + "\n" + "Motorist Injured - " + motoristInjured;
                }
                else{
                    casualtyText += "No. of Person injured - " + personInjured + "\n" + "Pedestrian Injured - " + pedestrianInjured
                            + "\n" + "Cyclist Injured - " + cyclistInjured + "\n" + "Motorist Injured - " + motoristInjured;
                }
            }
            casualty.setText(casualtyText);
        }
        vehicleFactor.setText("Contributing Factor for Vehicle 1 - "+nycAccidentDetails.getFactorVehicle1()+"\n"
                +"Contributing Factor for Vehicle 2 - "+nycAccidentDetails.getFactorVehicle2());
        vehicleType.setText("Vehicle 1 Type Code - "+nycAccidentDetails.getVehicleType1()+"\n"+
                "Vehicle 2 Type Code - "+nycAccidentDetails.getVehicleType2());
        return view;
    }
}
