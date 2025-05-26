package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;
    
    final String vehicleRegNumber = "ABCDEF";

    @Mock
    public InputReaderUtil mockInputReaderUtil;
    
    @Mock
    public TicketDAO mockTicketDAO;

    @BeforeAll
    public static void setUp() throws Exception {
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    void setUpPerTest() throws Exception {
        when(mockInputReaderUtil.readSelection()).thenReturn(1);
        when(mockInputReaderUtil.readVehicleRegistrationNumber()).thenReturn(vehicleRegNumber);
        dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterAll
    public static void tearDown() {
        dataBasePrepareService.clearDataBaseEntries();
    }
    
    public void parkACar() {
        ParkingService parkingService = new ParkingService(mockInputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
    }

    @Test
    public void testParkingACar() {
    	//Arrange
        ParkingService parkingService = new ParkingService(mockInputReaderUtil, parkingSpotDAO, ticketDAO);

        //Act
        parkingService.processIncomingVehicle();
        Ticket ticket = ticketDAO.getTicket(vehicleRegNumber);
        ParkingSpot parkingSpot = ticket.getParkingSpot();

        //Assert
        assertNotNull(ticket);
        assertNotNull(parkingSpot);
        assertEquals(vehicleRegNumber, ticket.getVehicleRegNumber());
        assertFalse(parkingSpot.isAvailable());
    }

    @Test
    public void testParkingLotExit() {
        //Arrange
    	parkACar();
    	ParkingService parkingService = new ParkingService(mockInputReaderUtil, parkingSpotDAO, ticketDAO);
    	boolean farePopulated;
    	Ticket ticket = ticketDAO.getTicket(vehicleRegNumber);
    	ticket.setInTime(new Date(System.currentTimeMillis() - 60 * 60 * 1000));
    	ticketDAO.updateTicket(ticket);
    	
    	//Act
        parkingService.processExitingVehicle();
        ticket = ticketDAO.getTicket(vehicleRegNumber);
        farePopulated = ticket.getPrice() > 0;
        
        //Assert
        //TODO: check that the fare generated and out time are populated correctly in the database
        assertNotNull(ticket.getOutTime(), "ticket.getOutTime() asserted not nul");
        assertTrue(farePopulated, "fare asserted > 0");        
    }
        
    @Test
    public void testParkingLotExitRecurringUser() {
    	//Arrange
    	ParkingService parkingService = new ParkingService(mockInputReaderUtil, parkingSpotDAO, ticketDAO);
    	parkACar();
    	parkACar();
    	Ticket ticket = ticketDAO.getTicket(vehicleRegNumber);
    	ticket.setInTime(new Date(System.currentTimeMillis() - 60 * 60 * 1000));
    	ticketDAO.updateTicket(ticket);

    	//Act
    	parkingService.processExitingVehicle();
    	ticket = ticketDAO.getTicket(vehicleRegNumber);
    	
    	//Assert
    	BigDecimal ticketWithDiscount = BigDecimal.valueOf(Fare.CAR_RATE_PER_HOUR * Fare.DISCOUNT)
    			.setScale(2, RoundingMode.HALF_UP);
    	BigDecimal testTicket = BigDecimal.valueOf(ticket.getPrice())
    			.setScale(2, RoundingMode.HALF_UP);
    	
    	assertNotNull(ticket);
    	assertTrue(ticketDAO.getNbTicket(vehicleRegNumber) > 1, "asserted .getNbTicket > 1");
    	assertEquals(ticketWithDiscount, testTicket);
    }
}
