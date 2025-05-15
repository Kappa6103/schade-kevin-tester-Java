package com.parkit.parkingsystem.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

public class DataBaseConfig {

    private static final Logger logger = LogManager.getLogger("DataBaseConfig");
// java.sql.SQLException: The server time zone value 'Paris, Madrid (heure dt)' is unrecognized or represents more than one time zone. You must configure either the server or JDBC driver (via the serverTimezone configuration property) to use a more specifc time zone value if you want to utilize time zone support.
    public Connection getConnection() throws ClassNotFoundException, SQLException {
        logger.info("Create DB connection");
        Class.forName("com.mysql.cj.jdbc.Driver"); //loading the Driver class with the right init calls
        return DriverManager.getConnection( //connecting with the DB address and login
                "jdbc:mysql://localhost:3306/test?serverTimezone=Europe/Paris", // changing prod to test and adding ?serverTimezone=Europe/Paris
                "root",
                "SADFDSAsd234@#$"); //modif du mot de passe
    }

    public void closeConnection(Connection con){
        if(con!=null){
            try {
                con.close(); // ?? commit before close maybe ??
                logger.info("Closing DB connection");
            } catch (SQLException e) {
                logger.error("Error while closing connection",e);
            }
        }
    }

    public void closePreparedStatement(PreparedStatement ps) {
        if(ps!=null){
            try {
                ps.close();
                logger.info("Closing Prepared Statement");
            } catch (SQLException e) {
                logger.error("Error while closing prepared statement",e);
            }
        }
    }

    public void closeResultSet(ResultSet rs) {
        if(rs!=null){
            try {
                rs.close();
                logger.info("Closing Result Set");
            } catch (SQLException e) {
                logger.error("Error while closing result set",e);
            }
        }
    }
}
