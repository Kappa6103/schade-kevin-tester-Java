package com.parkit.parkingsystem.service;


import java.math.BigDecimal;
import java.math.RoundingMode;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket, boolean discount){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException(
            		"Out time provided is incorrect:" + ticket.getOutTime().toString()
            		);
        }
        long inHour = ticket.getInTime().getTime();
        long outHour = ticket.getOutTime().getTime();
        long diffInMilliseconds = outHour - inHour;
        double formatingDuration = (double) ((diffInMilliseconds) / (1000.0 * 60)) / 60;
        double duration = BigDecimal
        		.valueOf(formatingDuration)
        		.setScale(2, RoundingMode.HALF_UP)
        		.doubleValue();
        
        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
            	if (duration > 0.5 && !discount) {
                    ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR);
            	} else if (duration > 0.5 && discount) {
            		ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR * Fare.DISCOUNT);
            	} else {
            		ticket.setPrice(0);
            	}
                break;
            }
            case BIKE: {
            	if (duration > 0.5 && !discount) {
                    ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR);
            	} else if (duration > 0.5 && discount) {
            		ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR * Fare.DISCOUNT);
            	} else {
            		ticket.setPrice(0);
            	}
                break;
            }
            default: throw new IllegalArgumentException("Unkown Parking Type");
        }
    }
    
    public void calculateFare(Ticket ticket){
    	calculateFare(ticket, false);
    }
}