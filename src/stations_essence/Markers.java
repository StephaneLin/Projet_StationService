package stations_essence;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Markers
 */
@WebServlet("/Markers")
public class Markers extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Markers() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// on intialise DAO
		DAOAll dao = new DAOAll();
		
		// on recupere la latitude/longitude par le géolocalisation
		double latitude = Double.parseDouble(request.getParameter("lat")) * 100000;
		double longitude = Double.parseDouble(request.getParameter("lng")) * 100000;

		// On récupère l'ensemble des id des stations proches de la position dans la BDD
		ArrayList<String> listId = dao.foundStation(latitude, longitude);
		request.setAttribute("id", listId);
		
		// On initialise les variables qui seront envoyées dans la vue 
		ArrayList<ArrayList<Double>> position = new ArrayList<ArrayList<Double>>();
		ArrayList<ArrayList<Double>> positionSta = new ArrayList<ArrayList<Double>>();
		ArrayList<ArrayList<ArrayList<String>>> mainInfos = new ArrayList<ArrayList<ArrayList<String>>>();
		ArrayList<ArrayList<String>> plusInfos = new ArrayList<ArrayList<String>>();
		ArrayList<Float> distance = new ArrayList<Float>();
		
		for (int i = 0; i < listId.size(); i++) {
			position.add(dao.foundPosition(listId.get(i)));
			positionSta.add(dao.foundPosition(listId.get(i)));
			distance.add(dao.getDistance(latitude, longitude, positionSta.get(i).get(0), positionSta.get(i).get(1)));
			mainInfos.add(dao.getMainInfo(listId.get(i)));
			plusInfos.add(dao.getPlusInfo(listId.get(i)));	
		}
		
		request.setAttribute("pos", position);
		request.setAttribute("posSta", positionSta);
		request.setAttribute("infos", mainInfos);
		request.setAttribute("plusInf", plusInfos);
		request.setAttribute("dis", distance);
		RequestDispatcher rd = getServletContext().getRequestDispatcher("/Map.jsp");
		rd.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
