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

import agh.powerSim.simulation.actors.utils.DataLoader;
import agh.powerSim.simulation.db_model.SimulationLog;

import com.google.gson.Gson;

public class SimulationsServlet extends HttpServlet{

    @Override
    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException
    {
    	DataLoader loader = new DataLoader();
    	ResultSet result = loader.getSimulations();
    	

        httpServletResponse.setContentType("applicationjson");
        PrintWriter out = httpServletResponse.getWriter();

		Gson gson = new Gson();
        
    	if(result==null){
    		out.println(gson.toJson(new agh.powerSim.simulation.db_model.Error("database connection error")));
    	} else {
    		try {
    			List<SimulationLog> list = new ArrayList<>();
				while(result.next()){
					Integer simulationId = result.getInt(1);
					list.add(new SimulationLog(simulationId));				
				}
				out.println(gson.toJson(list));
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				out.println(gson.toJson(new Error("couldn't load data")));
			}
    	}


    	loader.closeConnection();
        out.close();
    }
}
