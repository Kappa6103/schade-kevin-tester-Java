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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {
	
    private static ParkingService parkingServiceUnderTest; //adding "underTest" to clarify
    
    //adding "mock%" to mocked variables
    @Mock
    private static InputReaderUtil mockInputReaderUtil;
    @Mock
    private static ParkingSpotDAO mockParkingSpotDAO;
    @Mock
    private static TicketDAO mockTicketDAO;
    @Mock
    private static Ticket mockTicket; //adding mock ticket
    @Mock
    private static FareCalculatorService mockFareCalculatorService; //adding mock farecalculatorservice
    @Mock
    private static ParkingSpot mockParkingSpot; // adding mock parkingSpot
    
    
    @BeforeEach
    private void setUpPerTest() {
        try {
            when(mockInputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF"); //ok
            when(mockTicket.getParkingSpot()).thenReturn(mockParkingSpot);
            when(mockTicketDAO.getTicket("ABSDEF")).thenReturn(mockTicket);
            when(mockTicketDAO.updateTicket(mockTicket)).thenReturn(true);
                        
            when(mockParkingSpotDAO.updateParking(mockParkingSpot)).thenReturn(true);

            parkingServiceUnderTest = new ParkingService(mockInputReaderUtil, mockParkingSpotDAO, mockTicketDAO);
            
        } catch (Exception e) {
            e.printStackTrace();
            throw  new RuntimeException("Failed to set up test mock objects");
        }
    }

    @Test
    public void processExitingVehicleTest() {
    	// Arrange
    	when(mockTicketDAO.getNbTicket(anyString())).thenReturn(1);
        // Act
    	parkingServiceUnderTest.processExitingVehicle();
    	
        // Assert
        verify(mockParkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
        verify(mockTicketDAO, Mockito.times(1)).getNbTicket(anyString());
    }
    
    @Test
    public void testProcessIncomingVehicle() {
    	/*
    	 * test de l’appel de la méthodeprocessIncomingVehicle()où tout se déroule comme attendu.
    	 */
    	// Arrange
    	
    	// Act
    	parkingServiceUnderTest.processIncomingVehicle();
    	// Assert
    	verify(mockParkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
    	
    }
    
    @Test
    public void processExitingVehicleTestUnableUpdate() {
    	/*test de l’appel de la méthodeprocessIncomingVehicle()où tout se déroule comme attendu.
    	 * processExitingVehicleTestUnableUpdate:
    	 * exécution du test dans le cas où la méthode 
    	 * updateTicket()deticketDAOrenvoie false lors de l’appel de processExitingVehicle()
    	 */
    	// Arrange
    	// Act
    	// Assert
    	
    }
    
    @Test
    public void testGetNextParkingNumberIfAvailable() {
    	/*
    	 * test de l’appel de la méthodegetNextParkingNumberIfAvailable()avec pour résultat 
    	 * l’obtention d’un spot dont l’ID est 1 et qui est disponible.
    	 */
    	// Arrange
    	// Act
    	// Assert
    }
    
    @Test
    public void testGetNextParkingNumberIfAvailableParkingNumberNotFound() {
    	/*
    	 * test de l’appel de la méthodegetNextParkingNumberIfAvailable()avec pour résultat
    	 * aucun spot disponible (la méthode renvoie null).
    	 */
    	// Arrange
    	// Act
    	// Assert
    }
    
    @Test
    public void testGetNextParkingNumberIfAvailableParkingNumberWrongArgument() {
    	/*
    	 * test de l’appel de la méthode getNextParkingNumberIfAvailable()avec pour résultat
    	 * aucun spot (la méthode renvoie null) car l’argument saisi par l’utilisateur concernant 
    	 * le type de véhicule est erroné (par exemple, l’utilisateur a saisi 3).
    	 */
    	// Arrange
    	// Act
    	// Assert
    	
    }
    
}
