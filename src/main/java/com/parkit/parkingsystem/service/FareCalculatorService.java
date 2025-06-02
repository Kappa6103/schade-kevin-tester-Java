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
        
        if (duration >= 0.5) {
        	duration -= 0.5;			
		} else {
			duration = 0;
		}
        
        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
            	if (!discount) {
                    ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR);
            	} else if (discount) {
            		ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR * Fare.DISCOUNT);
            	}
                break;
            }
            case BIKE: {
            	if (!discount) {
                    ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR);
            	} else if (discount) {
            		ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR * Fare.DISCOUNT);
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