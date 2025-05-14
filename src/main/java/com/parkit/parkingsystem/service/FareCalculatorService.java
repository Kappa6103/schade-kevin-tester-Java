package com.parkit.parkingsystem.service;


import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }
        //changing the type from int to long & removing the .getHours() methodes to put .getTime();
        long inHour = ticket.getInTime().getTime();
        System.out.printf("inHour : %d\n", inHour);
        long outHour = ticket.getOutTime().getTime();
        System.out.printf("outHour : %d\n", outHour);
        //TODO: Some tests are failing here. Need to check if this logic is correct
        //minutes = milliseconds / 60000
        long duration = (outHour - inHour) / (1000 * 60);
        System.out.printf("duration : %d\n", duration);

        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
                ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR);
                System.out.println("Car : " + duration * Fare.CAR_RATE_PER_HOUR);
                break;
            }
            case BIKE: {
                ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR);
                System.out.println("Bike : " + duration * Fare.BIKE_RATE_PER_HOUR);
                break;
            }
            default: throw new IllegalArgumentException("Unkown Parking Type");
        }
    }
}