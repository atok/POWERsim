package agh.powerSim.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import agh.powerSim.simulation.actors.utils.DataLoader;
import agh.powerSim.simulation.db_model.SimulationLog;

public class SimulationGetDataServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {

		httpServletResponse.setContentType("applicationjson");

		String stringId = httpServletRequest.getParameter("simulationId");
		String page = httpServletRequest.getParameter("page");

		Gson gson = new Gson();

		PrintWriter out = httpServletResponse.getWriter();

		if (stringId == null || page == null) {
			out.println(gson.toJson(new agh.powerSim.simulation.db_model.Error("missing params: simulationId, page")));

		} else {

			DataLoader loader = new DataLoader();
			ResultSet result = loader.getSimulationData(stringId, Integer.parseInt(page));
			if (result == null) {
				out.println(gson.toJson(new agh.powerSim.simulation.db_model.Error("database connection error")));
			} else {
				try {
					List<SimulationLog> list = new ArrayList<>();
					while (result.next()) {
						list.add(new SimulationLog(result.getInt(1), result.getString(2), result.getString(3), result.getString(4), result.getString(5), result.getString(6)));
					}
					out.println(gson.toJson(list));
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					out.println(gson.toJson(new Error("couldn't load data")));
				}
			}

			loader.closeConnection();

		}

		out.close();
	}
}
