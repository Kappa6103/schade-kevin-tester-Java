package com.parkit.parkingsystem;


import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
	
    private static ParkingService parkingServiceUnderTest; //adding "underTest" to clarify
    
    private Ticket ticket; //ticket should not be mock
    
    private ParkingSpot parkingSpot;
    
    //adding "mock%" to mocked variables
    @Mock
    private InputReaderUtil mockInputReaderUtil;
    @Mock
    private ParkingSpotDAO mockParkingSpotDAO;
    @Mock
    private TicketDAO mockTicketDAO;
    @Mock
    private FareCalculatorService mockFareCalculatorService; //adding mock farecalculatorservice
   
    
    @BeforeEach
    private void setUpPerTest() {
        try {
        	//setting up pojo objects
        	parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        	ticket = new Ticket();
        	ticket.setParkingSpot(parkingSpot);
        	ticket.setVehicleRegNumber("ABCDEF");
        	ticket.setInTime(new Date(System.currentTimeMillis() - 60 * 60 * 1000)); // 1 hour ago
        	
        	//setting up the mock objects behaviour 
            when(mockInputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
            when(mockTicketDAO.getTicket("ABCDEF")).thenReturn(ticket);

            parkingServiceUnderTest = new ParkingService(mockInputReaderUtil, mockParkingSpotDAO, mockTicketDAO);
            
        } catch (Exception e) {
            e.printStackTrace();
            throw  new RuntimeException("Failed to set up test mock objects");
        }
    }

    @Test
    public void processExitingVehicleTest_whenFirstTimeUser() {
    	// Arrange
    	String vehiculeRegNumber = "ABCDEF";
    	when(mockTicketDAO.getNbTicket(vehiculeRegNumber)).thenReturn(1); // first time user
        when(mockParkingSpotDAO.updateParking(any())).thenReturn(true);
        when(mockTicketDAO.updateTicket(ticket)).thenReturn(true);
        when(mockParkingSpotDAO.updateParking(any())).thenReturn(true);
        
        // Act
    	parkingServiceUnderTest.processExitingVehicle();
    	
        // Assert
    	
        // Verify interactions:  Check that the expected methods were called
        verify(mockTicketDAO, times(1)).getTicket(vehiculeRegNumber);
        verify(mockTicketDAO, times(1)).getNbTicket(vehiculeRegNumber);
        verify(mockTicketDAO, times(1)).updateTicket(ticket);
        verify(mockParkingSpotDAO, times(1)).updateParking(any());
        
     // Verify state changes:  Check the effects of the method
        assertNotNull(ticket.getOutTime());
        assertTrue(ticket.getPrice() > 0); // assuming price gets set after 1 hour
        }
    
       @Test
     public void processExitingVehicleTestUnableUpdate() {
     	// Arrange
       	String vehiculeRegNumber = "ABCDEF";
       	when(mockTicketDAO.getNbTicket(vehiculeRegNumber)).thenReturn(1); // first time user
        when(mockTicketDAO.updateTicket(ticket)).thenReturn(false); // simulating error while updating
        
        // Capture System.out output
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

     	// Act
        parkingServiceUnderTest.processExitingVehicle();
        
     	// Assert
        // Verify console output contains payment prompt
        String consoleOutput = outContent.toString();
        assertTrue(consoleOutput.contains("Unable to update ticket information. Error occurred"));
        
        // Restore original System.out
        System.setOut(originalOut);
        
        
     	
     }
    
    @Test
    public void testProcessIncomingVehicle() throws Exception {
    	// Arrange
    	when(mockInputReaderUtil.readSelection()).thenReturn(1);
    	when(mockParkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);
    	when(mockInputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
    	when(mockParkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
    	when(mockTicketDAO.getNbTicket("ABCDEF")).thenReturn(1);
    	
        // Capture console output (optional, good for asserting printed messages)
        ByteArrayOutputStream consoleContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(consoleContent));
    	
    	// Act
    	parkingServiceUnderTest.processIncomingVehicle();

    	// Assert
        verify(mockParkingSpotDAO, times(1)).getNextAvailableSlot(ParkingType.CAR);
        verify(mockParkingSpotDAO, times(1)).updateParking(
        		argThat(parkingSpot -> parkingSpot.getId() == 1 && !parkingSpot.isAvailable()
        		));
        
        // Verify state changes:  Check the effects of the method
        assertEquals(ticket.getVehicleRegNumber(), "ABCDEF");
        assertEquals(ticket.getParkingSpot(), 1);
        assertEquals(ticket.getPrice(), 0);
        assertNotNull(ticket.getInTime());
        assertNull(ticket.getOutTime());
        

    	
    }
    

    
  //  @Test
 //   public void testGetNextParkingNumberIfAvailable() {
    	/*
    	 * test de l’appel de la méthodegetNextParkingNumberIfAvailable()avec pour résultat 
    	 * l’obtention d’un spot dont l’ID est 1 et qui est disponible.
    	 */
    	// Arrange
    	// Act
    	// Assert
 //   }
    
  //  @Test
  //  public void testGetNextParkingNumberIfAvailableParkingNumberNotFound() {
    	/*
    	 * test de l’appel de la méthodegetNextParkingNumberIfAvailable()avec pour résultat
    	 * aucun spot disponible (la méthode renvoie null).
    	 */
    	// Arrange
    	// Act
    	// Assert
   // }
    
  //  @Test
    //public void testGetNextParkingNumberIfAvailableParkingNumberWrongArgument() {
    	/*
    	 * test de l’appel de la méthode getNextParkingNumberIfAvailable()avec pour résultat
    	 * aucun spot (la méthode renvoie null) car l’argument saisi par l’utilisateur concernant 
    	 * le type de véhicule est erroné (par exemple, l’utilisateur a saisi 3).
    	 */
    	// Arrange
    	// Act
    	// Assert
    	
   // }
    
}
