package com.parkit.parkingsystem.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Calendar;
import java.util.Date;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

@ExtendWith(MockitoExtension.class)
public class TicketDAOIT {
	
	private static DataBasePrepareService dataBasePrepareService;
	private static ParkingSpotDAO parkingSpotDAO;
	
	private TicketDAO ticketDAOUnderTest;
	
    final String vehicleRegNumber = "ABCDEF";
	
    @Mock
    public InputReaderUtil mockInputReaderUtil;
    
    
	@BeforeAll
	public static void setUp() {
		dataBasePrepareService = new DataBasePrepareService();
		parkingSpotDAO = new ParkingSpotDAO();
	}
		
	@BeforeEach
	public void setUpPerTest() {
		ticketDAOUnderTest = new TicketDAO();
		dataBasePrepareService.clearDataBaseEntries();
	}
	
	@AfterEach
	public void tearDownPerTest() {
		dataBasePrepareService.clearDataBaseEntries();
	}
	
    public void parkACar() {   	
		try {
			when(mockInputReaderUtil.readSelection()).thenReturn(1);
			when(mockInputReaderUtil.readVehicleRegistrationNumber()).thenReturn(vehicleRegNumber);
		} catch (Exception e) {
			e.printStackTrace();
		}
		ParkingService parkingService = new ParkingService(mockInputReaderUtil, parkingSpotDAO, ticketDAOUnderTest);
        parkingService.processIncomingVehicle();
    }
    
    public void parkABike() {   	
		try {
			when(mockInputReaderUtil.readSelection()).thenReturn(2);
			when(mockInputReaderUtil.readVehicleRegistrationNumber()).thenReturn(vehicleRegNumber);
		} catch (Exception e) {
			e.printStackTrace();
		}
		ParkingService parkingService = new ParkingService(mockInputReaderUtil, parkingSpotDAO, ticketDAOUnderTest);
        parkingService.processIncomingVehicle();
    }
    
    public Date getInTimeForTest() {
    	Calendar calendar = Calendar.getInstance();
    	calendar.set(Calendar.YEAR, 2025);
    	calendar.set(Calendar.MONTH, Calendar.MAY);
    	calendar.set(Calendar.DAY_OF_MONTH, 26);
    	calendar.set(Calendar.HOUR_OF_DAY, 15);
    	calendar.set(Calendar.MINUTE, 45);
    	calendar.set(Calendar.SECOND, 0);
    	calendar.set(Calendar.MILLISECOND, 0);
    	return calendar.getTime();
    }
    
    public Date getOutTimeForTest() {
    	Calendar calendar = Calendar.getInstance();
    	calendar.set(Calendar.YEAR, 2025);
    	calendar.set(Calendar.MONTH, Calendar.MAY);
    	calendar.set(Calendar.DAY_OF_MONTH, 27);
    	calendar.set(Calendar.HOUR_OF_DAY, 14);
    	calendar.set(Calendar.MINUTE, 30);
    	calendar.set(Calendar.SECOND, 0);
    	calendar.set(Calendar.MILLISECOND, 0);
    	return calendar.getTime();
    }
	
    @Test
    public void getNbTicket_whenFirstTimeUser() {
    	//Arrange
    	parkACar();
    	int result;
    	//Act
    	result = ticketDAOUnderTest.getNbTicket(vehicleRegNumber);
    	//Assert
    	assertEquals(1, result);	
    }

    @Test
    public void getNbTicket_whenSecondTimeUser() {
    	//Arrange
    	parkACar();
    	parkACar();
    	int result;
    	//Act
    	result = ticketDAOUnderTest.getNbTicket(vehicleRegNumber);
    	//Assert
    	assertEquals(2, result);
    }
    
    @Test
    public void saveTicketTest() {
    	//Arrange
    	parkACar();
    	boolean result;
    	//Act
    	result = ticketDAOUnderTest.saveTicket(ticketDAOUnderTest.getTicket(vehicleRegNumber));
    	//Assert
    	assertTrue(result);
    }
    
    @Test
    public void getTicketTest_AsACar() {
    	//Arrange
    	parkACar();
    	Ticket result;
    	//Act
    	result = ticketDAOUnderTest.getTicket(vehicleRegNumber);
    	//Assert
    	assertNotNull(result);
    	assertNotNull(result.getParkingSpot());
    	assertEquals(1, result.getId());
    	assertTrue(result.getParkingSpot().getId() >= 1 && result.getParkingSpot().getId() <= 3);
    	assertEquals(vehicleRegNumber, result.getVehicleRegNumber());
    	assertNotNull(result.getPrice());
    	assertNotNull(result.getInTime());
    	assertNull(result.getOutTime());
    }
    
    @Test
    public void getTicketTest_AsABike() {
    	//Arrange
    	parkABike();
    	Ticket result;
    	//Act
    	result = ticketDAOUnderTest.getTicket(vehicleRegNumber);
    	//Assert
    	assertNotNull(result);
    	assertNotNull(result.getParkingSpot());
    	assertEquals(1, result.getId());
    	assertTrue(result.getParkingSpot().getId() >= 4 && result.getParkingSpot().getId() <= 5);
    	assertEquals(vehicleRegNumber, result.getVehicleRegNumber());
    	assertNotNull(result.getPrice());
    	assertNotNull(result.getInTime());
    	assertNull(result.getOutTime());
    }
    
    @Test
    public void updateTicketTest() {
    	//Arrange
    	parkACar();
    	Ticket ticket = ticketDAOUnderTest.getTicket(vehicleRegNumber);
    	ticket.setPrice(1000);
    	ticket.setOutTime(getOutTimeForTest());
    	ticket.setInTime(getInTimeForTest());
    	boolean result;
    	//Act
    	result = ticketDAOUnderTest.updateTicket(ticket);
    	ticket = ticketDAOUnderTest.getTicket(vehicleRegNumber);
    	//Assert
    	assertTrue(result);
    	assertEquals(1000, ticket.getPrice());
    	assertEquals(getOutTimeForTest().getTime(), ticket.getOutTime().getTime());
    	assertEquals(getInTimeForTest().getTime(), ticket.getInTime().getTime());
    	
    }

}
