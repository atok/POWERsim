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

public class DataRecorder {
	private long simulationId;
	private Connection connect = null;
	private Statement statement = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;
	protected String propertiesFile = "database.properties";
	protected Properties properties;
	private static final DateTimeFormatter FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd' 'HH:mm:ss.SSS"); // YYYY-MM-DD
																												// HH:MM:SS.SSS

	public DataRecorder() {
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

	public long getSimulationId() {
		return simulationId;
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

	public void store(DataRecorderActor.DataReport report) {

		String[] fragments = report.sender.path().toString().split("/");
		String[] actorNameAndType = fragments[fragments.length - 1].split(":");

		String actorName = "UNKNOWN";
		String actorType = "UNKNOWN";
		if (actorNameAndType.length == 2) {
			actorName = actorNameAndType[1];
			actorType = actorNameAndType[0];
		} else {
			actorName = actorNameAndType[0];
		}
		String date = FORMATTER.print(report.time);

		String query = "insert into tsimulation_log (simulation_id, actor_name, actor_type, submit_date, event_type, event_value) values " + "(?, ?, ?, ?, ?, ?); ";

		if (report instanceof PowerUsageRecord) {
			PowerUsageRecord r = (PowerUsageRecord) report;

			Double powerUsed = r.powerUsedInThisStep;

			try {
				PreparedStatement prepStmt = connect.prepareStatement(query);
				prepStmt.setLong(1, simulationId);
				prepStmt.setString(2, actorName);
				prepStmt.setString(3, actorType);
				prepStmt.setString(4, date);
				prepStmt.setString(5, "power_usage");
				prepStmt.setString(6, String.format("%1$.7f", powerUsed));
				prepStmt.execute();
				// System.out.println("Power used and saved: " +
				// powerUsed.toString());
			} catch (SQLException e) {
				System.err.println(e.getMessage());
				System.err.println("Blad przy zapisie logu: " + query);
			}
		} else if (report instanceof StatusRecord) {
			StatusRecord r = (StatusRecord) report;

			try {
				PreparedStatement prepStmt = connect.prepareStatement(query);
				prepStmt.setLong(1, simulationId);
				prepStmt.setString(2, actorName);
				prepStmt.setString(3, actorType);
				prepStmt.setString(4, date);
				prepStmt.setString(5, "status_report");
				prepStmt.setString(6, r.status);
				prepStmt.execute();
			} catch (SQLException e) {
				System.err.println(e.getMessage());
				System.err.println("Blad przy zapisie logu: " + query);
			}

		}
	}

	public static class PowerUsageRecord extends DataRecorderActor.DataReport {
		final double powerUsedInThisStep;
		final double lightProvidedInThisStep;

		public PowerUsageRecord(double powerUsedInThisStep, double lightProvidedInThisStep, LocalDateTime time, ActorRef sender) {
			super(time, sender);
			this.powerUsedInThisStep = powerUsedInThisStep;
			this.lightProvidedInThisStep = lightProvidedInThisStep;
		}
	}

	public static class StatusRecord extends DataRecorderActor.DataReport {
		final String status;

		public StatusRecord(String status, LocalDateTime time, ActorRef sender) {
			super(time, sender);
			this.status = status;
		}
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
