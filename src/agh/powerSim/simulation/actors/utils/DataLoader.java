package agh.powerSim.simulation.actors.utils;

import agh.powerSim.simulation.actors.ClockActor;
import agh.powerSim.simulation.actors.DataRecorderActor;
import akka.actor.ActorRef;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.LocalDateTime;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Properties;

public class DataLoader {
	private long simulationId;
	private Connection connect = null;
	private Statement statement = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;
	protected String propertiesFile = "database.properties";
	protected Properties properties;
	private static final DateTimeFormatter FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd' 'HH:mm:ss.SSS"); // YYYY-MM-DD
																												// HH:MM:SS.SSS

	public DataLoader() {
		this.properties = new Properties();
		try {
			this.properties.load(new FileInputStream(this.propertiesFile));

			String driver = this.properties.getProperty("driver");
			String database_url = this.properties.getProperty("database_url");

			// This will load the MySQL driver, each DB has its own driver
			Class.forName(driver);

			// Setup the connection with the DB
			connect = DriverManager.getConnection(database_url);

			// Statements allow to issue SQL queries to the database
			statement = connect.createStatement();

			simulationId = System.currentTimeMillis() / 1000;

		} catch (SQLException e) {
			System.err.println("Problem z otwarciem polaczenia");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.err.println("Brak sterownika JDBC");
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		createTables();
	}

	public boolean createTables() {
		String createSimulationLog = "CREATE  TABLE IF NOT EXISTS tsimulation_log (\n" + //
				"  id INTEGER PRIMARY KEY ASC , " + //
				"  simulation_id INT NOT NULL, " + //
				"  actor_name VARCHAR(100) NOT NULL, " + //
				"  actor_type VARCHAR(100) NOT NULL, " + //
				"  submit_date VARCHAR(100) NOT NULL, " + //
				"  event_type VARCHAR(100) NOT NULL, " + //
				"  event_value VARCHAR(100)" + " )";
		try {
			statement.execute(createSimulationLog);
		} catch (SQLException e) {
			System.err.println("Blad przy tworzeniu tabeli");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public ResultSet getSimulations() {

		String query = "select distinct simulation_id from tsimulation_log;";

		ResultSet rs = null;

		try {
			PreparedStatement prepStmt = connect.prepareStatement(query);
			rs = prepStmt.executeQuery();
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			System.err.println("Blad przy zapisie logu: " + query);
		}

		return rs;

	}

	public ResultSet getSimulationData(String simulationId, int page) {
		
		int offset = 100 * page;

		String query = "select simulation_id, actor_name, actor_type, submit_date, event_type, event_value from tsimulation_log where simulation_id = "+simulationId+" limit 100 offset "+offset+";";

		ResultSet rs = null;

		try {
			PreparedStatement prepStmt = connect.prepareStatement(query);
			rs = prepStmt.executeQuery();
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			System.err.println("Blad przy zapisie logu: " + query);
		}

		return rs;
	}

	public void closeConnection() {
		try {
			connect.close();
		} catch (SQLException e) {
			System.err.println("Problem z zamknieciem polaczenia");
			e.printStackTrace();
		}
	}
}
