package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

public class FareCalculatorServiceTest {

    private static FareCalculatorService fareCalculatorService;
    private Ticket ticket;

    @BeforeAll
    private static void setUp() {
        fareCalculatorService = new FareCalculatorService();
    }

    @BeforeEach
    private void setUpPerTest() {
        ticket = new Ticket();
    }

    @Test
    public void calculateFareCarForOneHourParkingTime() {
        //Arrange
    	Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        //Act
        fareCalculatorService.calculateFare(ticket);
        //Assert
        assertEquals(Fare.CAR_RATE_PER_HALF_HOUR, ticket.getPrice());
    }

    @Test
    public void calculateFareBikeForOneHourParkingTime() {
        //Arrange
    	Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        //Act
        fareCalculatorService.calculateFare(ticket);
        //Assert
        assertEquals(Fare.BIKE_RATE_PER_HALF_HOUR, ticket.getPrice());
    }

    @Test
    public void calculateFareUnkownType() {
        //Arrange
    	Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, null,false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        //Act & Assert
        assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
    }
    
    @Test
    public void calculateFareCarWithFutureInTime() {
    	//Arrange
    	Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() + (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        //Act & Assert
        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    public void calculateFareBikeWithFutureInTime() {
        //Arrange
    	Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() + (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        //Act & Assert
        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    public void calculateFareBikeWithFortyFiveMinutesParkingTime() {
        //Arrange
    	Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  45 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        //Act
        fareCalculatorService.calculateFare(ticket);
        //Assert
        assertEquals(Fare.BIKE_RATE_PER_QUARTER_HOUR, ticket.getPrice());
    }

    @Test
    public void calculateFareCarWithFortyFiveMinutesParkingTime() {
    	//Arrange
    	Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  45 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        //Act
        fareCalculatorService.calculateFare(ticket);
        //Assert
        assertEquals(Fare.CAR_RATE_PER_QUARTER_HOUR , ticket.getPrice());
    }

    @Test
    public void calculateFareCarWithMoreThanADayParkingTime() {
        //Arrange
    	Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  24 * 60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        //Act
        fareCalculatorService.calculateFare(ticket);
        //Assert
        assertEquals( (23.5 * Fare.CAR_RATE_PER_HOUR) , ticket.getPrice());
    }
    
    @Test
    public void calculateFareBikeWithMoreThanADayParkingTime() {
        //Arrange
    	Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  24 * 60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        //Act
        fareCalculatorService.calculateFare(ticket);
        //Assert
        assertEquals( (23.5 * Fare.BIKE_RATE_PER_HOUR) , ticket.getPrice());
    }
    
    @Test
    public void calculateFareCarWithLessThan30minutesParkingTime() {
		// Arrange
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (15 * 60 * 1000));
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
		// Act
        fareCalculatorService.calculateFare(ticket);
		// Assert
		assertEquals(0, ticket.getPrice());
    }
    
    @Test
    public void calculateFareBikeWithLessThan30minutesParkingTime() {
		// Arrange
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (15 * 60 * 1000));
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
		// Act
        fareCalculatorService.calculateFare(ticket);
		// Assert
		assertEquals(0, ticket.getPrice());
    }
    
    @Test
    public void calculateFareCarForOneHourParkingTimeWithDiscount() {
		// Arrange
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
		// Act
        fareCalculatorService.calculateFare(ticket, true);
		// Assert
		assertEquals(Fare.CAR_RATE_PER_HALF_HOUR * Fare.DISCOUNT , ticket.getPrice());
    }
    
    @Test
    public void calculateFareBikeForOneHourParkingTimeWithDiscount() {
		// Arrange
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
		// Act
        fareCalculatorService.calculateFare(ticket, true);
		// Assert
		assertEquals(Fare.BIKE_RATE_PER_HALF_HOUR * Fare.DISCOUNT , ticket.getPrice());
    }
}
