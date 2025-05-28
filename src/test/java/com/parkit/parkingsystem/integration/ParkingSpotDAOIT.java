package com.parkit.parkingsystem.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

@ExtendWith(MockitoExtension.class)
public class ParkingSpotDAOIT {
	
	private static DataBasePrepareService dataBasePrepareService;
	private static ParkingSpotDAO parkingSpotDAO;
	private static TicketDAO ticketDAO;

	private ParkingSpotDAO parkingSpotDAOUnderTest;
    
    final String vehicleRegNumber = "ABCDEF";
    
    @Mock
    public InputReaderUtil mockInputReaderUtil;
    
    @Mock
    public ParkingSpot mockParkingSpot;
	
	@BeforeAll
	public static void setUp() {
		dataBasePrepareService = new DataBasePrepareService();		
    	parkingSpotDAO = new ParkingSpotDAO();
    	ticketDAO = new TicketDAO();
	}
	
	@BeforeEach
	public void setUpPerTest() {
		parkingSpotDAOUnderTest = new ParkingSpotDAO();
	}

	@AfterEach
	public void tearDownPerTest() {
		dataBasePrepareService.clearDataBaseEntries();
	}
	
    public void parkACar() {   	
		try {
			when(mockInputReaderUtil.readVehicleRegistrationNumber()).thenReturn(vehicleRegNumber);
			when(mockInputReaderUtil.readSelection()).thenReturn(1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		ParkingService parkingService = new ParkingService(mockInputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
    }
    
    public void parkABike() {
		try {
			when(mockInputReaderUtil.readVehicleRegistrationNumber()).thenReturn(vehicleRegNumber);
			when(mockInputReaderUtil.readSelection()).thenReturn(2);
		} catch (Exception e) {
			e.printStackTrace();
		}
        ParkingService parkingService = new ParkingService(mockInputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
    }
	
	@Test
	public void getNextAvailabeSlot_CarParkingTypeTest() {
		//Arrange
		int result;
		//Act
		result = parkingSpotDAOUnderTest.getNextAvailableSlot(ParkingType.CAR);
		//Assert
    	assertTrue(result >= 1 && result <= 3);
	}
	
	@Test
	public void getNextAvailabeSlot_CarParkingTypeTest_whenFull() {
		//Arrange
		parkACar();
		parkACar();
		parkACar();
		//Act
		int result = parkingSpotDAOUnderTest.getNextAvailableSlot(ParkingType.CAR);
		//Assert
		assertEquals(0, result);
	}
	
	@Test
	public void getNextAvailabeSlot_BikeParkingTypeTest() {
		//Arrange
		int result;
		//Act
		result = parkingSpotDAOUnderTest.getNextAvailableSlot(ParkingType.BIKE);
		//Assert
    	assertTrue(result >= 4 && result <= 5);
	}
	
	@Test
	public void getNextAvailabeSlot_BikeParkingTypeTest_whenFull() {
		//Arrange
		parkABike();
		parkABike();
		//Act
		int result = parkingSpotDAOUnderTest.getNextAvailableSlot(ParkingType.BIKE);
		//Assert
		assertEquals(0, result);
	}
	
	@Test
	public void updateParkingTest() {
		//Arrange
		boolean result;
		when(mockParkingSpot.isAvailable()).thenReturn(false);
		when(mockParkingSpot.getId()).thenReturn(1);
		//Act
		result = parkingSpotDAOUnderTest.updateParking(mockParkingSpot);
		//Assert
		assertTrue(result);
	}
}
