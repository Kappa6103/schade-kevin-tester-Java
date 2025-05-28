package com.parkit.parkingsystem;


import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {
	
    private ParkingService parkingServiceUnderTest;
    private Ticket ticket;
    private ParkingSpot parkingSpot;
    private final String vehiculeRegNumber = "ABCDEF";
    
    @Mock
    private InputReaderUtil mockInputReaderUtil;
    @Mock
    private ParkingSpotDAO mockParkingSpotDAO;
    @Mock
    private TicketDAO mockTicketDAO;   
    
    @BeforeEach
    public void setUpPerTest() {
        try {        	        	
            parkingServiceUnderTest = new ParkingService(mockInputReaderUtil, mockParkingSpotDAO, mockTicketDAO);
        } catch (Exception e) {
            e.printStackTrace();
            throw  new RuntimeException("Failed to set up test mock objects - BeforeEach method");
        }
    }

    @Test
    public void processExitingVehicleTest() {
    	// Arrange
    	parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
    	ticket = new Ticket();
    	ticket.setParkingSpot(parkingSpot);
    	ticket.setVehicleRegNumber(vehiculeRegNumber);
    	ticket.setInTime(new Date(System.currentTimeMillis() - 60 * 60 * 1000));
		try {
			when(mockTicketDAO.getNbTicket(vehiculeRegNumber)).thenReturn(1);
			when(mockParkingSpotDAO.updateParking(any())).thenReturn(true);
			when(mockTicketDAO.updateTicket(ticket)).thenReturn(true);			
			when(mockInputReaderUtil.readVehicleRegistrationNumber()).thenReturn(vehiculeRegNumber);
			when(mockTicketDAO.getTicket(vehiculeRegNumber)).thenReturn(ticket);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to set up test mock objects - "
					+ "processExitingVehicleTest method");
		}
        // Act
    	parkingServiceUnderTest.processExitingVehicle();
        // Assert
        verify(mockTicketDAO, times(1)).getTicket(vehiculeRegNumber);
        verify(mockTicketDAO, times(1)).getNbTicket(vehiculeRegNumber);
        verify(mockTicketDAO, times(1)).updateTicket(ticket);
        verify(mockParkingSpotDAO, times(1)).updateParking(any());
        assertNotNull(ticket.getOutTime());
        assertTrue(ticket.getPrice() > 0);
        }
    
       @Test
     public void processExitingVehicleTestUnableUpdate() {
     	// Arrange
       	parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
       	ticket = new Ticket();
       	ticket.setParkingSpot(parkingSpot);
       	ticket.setVehicleRegNumber(vehiculeRegNumber);
       	ticket.setInTime(new Date(System.currentTimeMillis() - 60 * 60 * 1000));
       	try {
			when(mockTicketDAO.getNbTicket(vehiculeRegNumber)).thenReturn(1);
			when(mockTicketDAO.updateTicket(ticket)).thenReturn(false);
			when(mockInputReaderUtil.readVehicleRegistrationNumber()).thenReturn(vehiculeRegNumber);
			when(mockTicketDAO.getTicket(vehiculeRegNumber)).thenReturn(ticket);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to set up test mock objects - "
					+ "processExitingVehicleTestUnableUpdate");
		}
        ByteArrayOutputStream consoleContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(consoleContent));
     	// Act
        parkingServiceUnderTest.processExitingVehicle();
        System.setOut(originalOut);
     	// Assert
        String consoleOutput = consoleContent.toString();
        assertTrue(consoleOutput.contains("Unable to update ticket information. Error occurred"),
        		"incorrect console output");
     }
    
    @Test
    public void testProcessIncomingVehicle() {
    	//Arrange
    	try {
			when(mockInputReaderUtil.readSelection()).thenReturn(1); // car
			when(mockParkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);
			when(mockInputReaderUtil.readVehicleRegistrationNumber()).thenReturn(vehiculeRegNumber);
			when(mockParkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
			when(mockTicketDAO.getNbTicket(vehiculeRegNumber)).thenReturn(1);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to set up test mock objects - "
					+ "testProcessIncomingVehicle");
		}
        ByteArrayOutputStream consoleContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(consoleContent));
    	// Act
    	parkingServiceUnderTest.processIncomingVehicle();
    	System.setOut(originalOut);
    	// Assert
        verify(mockParkingSpotDAO, times(1)).getNextAvailableSlot(ParkingType.CAR);
        verify(mockParkingSpotDAO, times(1)).updateParking(
        		argThat(parkingSpot -> parkingSpot.getId() == 1 && !parkingSpot.isAvailable()
        		));     
        // Capture the ticket saved to verify its content
        ArgumentCaptor<Ticket> ticketCaptor = ArgumentCaptor.forClass(Ticket.class);
        verify(mockTicketDAO).saveTicket(ticketCaptor.capture()); 
        Ticket savedTicket = ticketCaptor.getValue();
        assertEquals(vehiculeRegNumber, savedTicket.getVehicleRegNumber());
        assertEquals(1, savedTicket.getParkingSpot().getId());
        assertEquals(ParkingType.CAR, savedTicket.getParkingSpot().getParkingType());
        assertEquals(0, savedTicket.getPrice());
        assertNotNull(savedTicket.getInTime());
        assertNull(savedTicket.getOutTime());
   
        String consoleOutput = consoleContent.toString();
        assertTrue(consoleOutput.contains("Generated Ticket and saved in DB"),
        		"incorrect console output");
    }

    @Test
    public void testGetNextParkingNumberIfAvailable() {
    	// Arrange
		when(mockInputReaderUtil.readSelection()).thenReturn(1);
    	when(mockParkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);
    	// Act
    	parkingSpot = parkingServiceUnderTest.getNextParkingNumberIfAvailable();
    	// Assert
    	assertNotNull(parkingSpot);
    	assertEquals(1, parkingSpot.getId());
    	assertEquals(ParkingType.CAR, parkingSpot.getParkingType());
    	assertTrue(parkingSpot.isAvailable(), "checking if the spot is free");
    }
    
    @Test
    public void testGetNextParkingNumberIfAvailableParkingNumberNotFound() {
    	// Arrange
    	when(mockInputReaderUtil.readSelection()).thenReturn(1);
    	when(mockParkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(-1);
    	// Act
    	parkingSpot = parkingServiceUnderTest.getNextParkingNumberIfAvailable();
    	// Assert
    	assertNull(parkingSpot);
    }
    
    @Test
    public void testGetNextParkingNumberIfAvailableParkingNumberWrongArgument() {
    	// Arrange
    	when(mockInputReaderUtil.readSelection()).thenReturn(3);
    	// Act
    	parkingSpot = parkingServiceUnderTest.getNextParkingNumberIfAvailable();
    	// Assert
    	assertNull(parkingSpot);
    }
}
