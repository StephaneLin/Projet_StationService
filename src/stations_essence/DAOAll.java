package stations_essence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Cette classe DAOAll est la classe liée à la base de donnée qui contient toutes les informations sur 
 * toutes les stations essences
 * Elle permet donc de pouvoir récupérer chaque information pour l'afficher dans Map.jsp
 * @author Julie Fillatre & Stephane Lin
 * 
 *
 */
public class DAOAll {
	//Definition des paramètres permettant la connexion à la base de données
		static {
			try { 
				Class.forName("com.mysql.cj.jdbc.Driver");
			} catch (ClassNotFoundException ex) {
				ex.printStackTrace();
			}
		}

				
		private final String url = "jdbc:mysql://localhost:3306/stations_essence?serverTimezone=Europe/Paris"; //la base de données
		private final String user = "root";
		private final String password="root";
		
		private final String table = "allstationdata";
			
		Connection connection = null;
			
		/**
		 * Création de la connection à la base de donnéee
		 * @return boolean		Si la connexion est effectuée : return true
		 */
		private boolean connect() {
			try {
				connection = DriverManager.getConnection(url, user, password);

			}catch(SQLException e) {
				e.printStackTrace();
				return false;
			}
			return true;

		}

		/**
		 * Ferme la connexion à la base de donnée
		 */
		private void close() {
			if(connection!=null) {
				try{
					connection.close();			
				}catch(SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		//Définition du rayon pour le rectangle de recherche des stations
		private int rayon = 10000;
		
		/**
		 * Cette méthode permet de récupérer l'ensemble des id des stations se trouvant dans le rectangle de centre les paramètres d'entrée
		 * et de  rayon celui défini ci-dessus
		 * @param latitudePos
		 * @param longitudePos
		 * @return la liste des id de type String
		 */
		
		public ArrayList<String> foundStation(double latitudePos, double longitudePos) {
			double latitudeMin = latitudePos - rayon/2;
			double longitudeMin = longitudePos -rayon;
			double latitudeMax = latitudePos + rayon/2;
			double longitudeMax = longitudePos + rayon;
			//Création d'une liste de type String contenant l'id des 20 premières stations
			ArrayList<String> listStations = new ArrayList <String>();
			
			if (connect()) {
				String query= "select id from "+table+" where latitude between "+ latitudeMin +" and "+latitudeMax+ " and longitude between "+ longitudeMin + " and "+longitudeMax;
			
				try {
					PreparedStatement ps = connection.prepareStatement(query);
					ResultSet rs=ps.executeQuery();
					while (rs.next()) {
						listStations.add(rs.getString(1));
					}
					
				}catch(SQLException e){
					e.printStackTrace();
				}finally {
					close();
				}
			}
			
			return listStations;
		}
		
		/**
		 * Cette méthode permet de récupérer la liste des latitudes et longitudes des stations se trouvant dans le rectangle
		 * autour de la position Initiale (latitudeInit, longitudeInit)
		 * @param latitudeInit
		 * @param longitudeInit
		 * @return
		 */
		public ArrayList<String> foundLatitudeLongitude(double latitudeInit, double longitudeInit) {
			//Création d'une liste de type String contenant l'id des 20 premières stations
			ArrayList<String> listStations = new ArrayList <String>();
			double latitudeMin = latitudeInit - rayon/2;
			double longitudeMin = longitudeInit -rayon;
			double latitudeMax = latitudeInit + rayon/2;
			double longitudeMax = longitudeInit + rayon;
			
			if (connect()) {
				String query= "select latitude,longitude from "+table+" latitude between "+ latitudeMin +" and "+latitudeMax+ " and longitude between "+ longitudeMin + " and "+longitudeMax;
				try {
					PreparedStatement ps = connection.prepareStatement(query);
					ResultSet rs=ps.executeQuery();
					while (rs.next()) {
						listStations.add(rs.getString(1));
						listStations.add(rs.getString(2));
					}
					
				}catch(SQLException e){
					e.printStackTrace();
				}finally {
					close();
				}
			}
			
			return listStations;
		}
		
		/**
		 * Cette méthode permet de récupérer la latitude et la longitude correspondant à l'id d'une station
		 * @param id
		 * @return
		 */
		public ArrayList<Double> foundPosition (String id){
			ArrayList <Double> position = new ArrayList<Double>();
			
			if (connect()) {
				String query= "select latitude,longitude from "+table+" where id =" + id;
				try {
					PreparedStatement ps = connection.prepareStatement(query);
					ResultSet rs=ps.executeQuery();
					while (rs.next()) {
						position.add(rs.getDouble(1));
						position.add(rs.getDouble(2));
					}
					
				}catch(SQLException e){
					e.printStackTrace();
				}finally {
					close();
				}
			}
			
			return position;
		}
		
		/**
		 * méthode permettant de convertir des degrés en radian (utile pour afficher la distance d'une station)
		 * @param input
		 * @return
		 */
		public double convertRad(double input) {
			return (double) ((Math.PI*input)/180);
		}

		/**
		 * Méthode permettant de calculer la distance entre 2 point en fonction de leur latitude et longitude
		 * @param latitude1
		 * @param longitude1
		 * @param latitude2
		 * @param longitude2
		 * @return
		 */
		public float getDistance(double latitude1, double longitude1, double latitude2, double longitude2) {
			int R = 6378000; //Rayon de la terre en mètre

			double latitude1R = convertRad(latitude1);
			double longitude1R = convertRad(longitude1);
			double latitude2R = convertRad(latitude2);
			double longitude2R = convertRad(longitude2);

			//Distance en kilometres
			float d = (float) (R * (Math.PI/2 - Math.asin( Math.sin(latitude2R) * Math.sin(latitude1R) + Math.cos(longitude2R - longitude1R) * Math.cos(latitude2R) * Math.cos(latitude1R))))/1000;
			return d;
		}
		
		/**
	 * Méthode permettant de récuperer des données "simples" en fonction du nom de la donnée (parametre) et de l'id de la station
	 * @param parametre
	 * @param valeurId
	 * @return
	 */
	public String getAttribut(String parametre, String valeurId) {
		
		String attribut=null;
		
		if (connect()){
			String query = "select "+parametre+" from "+table+" where id="+valeurId;
			try{
				PreparedStatement ps = connection.prepareStatement(query);
				ResultSet rs=ps.executeQuery();
				while (rs.next()) {
					attribut = rs.getString(1);
				}
			}catch(SQLException e){
				e.printStackTrace();
			}finally {
				close();
			}
		}

		return attribut;
	}
		
	public String getVille(String id) {
		return getAttribut("ville",id);
	}
		
	public String getAdresse(String id) {
		return getAttribut("adresse",id);
	}	
	
	public ArrayList<String> getHoraires(String id) {
		ArrayList <String> horaires = new ArrayList <String>();
		horaires.add(getAttribut("lundi",id));
		horaires.add(getAttribut("mardi",id));
		horaires.add(getAttribut("mercredi",id));
		horaires.add(getAttribut("jeudi",id));
		horaires.add(getAttribut("vendredi",id));
		horaires.add(getAttribut("samedi",id));
		horaires.add(getAttribut("dimanche",id));
		return horaires;
	}
	
	public String getServices(String id) {
		return getAttribut("services",id);
	}
	
	public ArrayList<String> getCarburants(String id) {
		ArrayList <String> carburants = new ArrayList <String>();
		carburants.add(getAttribut("Gazole",id));
		carburants.add(getAttribut("sp95",id));
		carburants.add(getAttribut("e85",id));
		carburants.add(getAttribut("gplc",id));
		carburants.add(getAttribut("e10",id));
		carburants.add(getAttribut("sp98",id));
		return carburants;
	}
	
	/**
	 * Méthode permettant de récuperer les données principales à afficher
	 * @param id
	 * @return
	 */
	public ArrayList <ArrayList<String>> getMainInfo(String id){


		ArrayList <ArrayList<String>> mainInfo = new ArrayList <ArrayList<String>>();
		ArrayList <ArrayList<String>> info = new ArrayList <ArrayList<String>>();
		ArrayList <String> villeAdresse = new ArrayList <String>();
		villeAdresse.add(getVille(id));
		villeAdresse.add(getAdresse(id));
		info.add(villeAdresse);

		mainInfo.addAll(info);
		mainInfo.add(getHoraires(id));

		return mainInfo;
	}
	
	
	
	
	
	/**
	 * Méthode permettant de récuperer les données secondaires à afficher
	 * @param id
	 * @return
	 */
	public ArrayList <String> getPlusInfo(String id){

		ArrayList <String> plusInfo = new ArrayList<String>();

		plusInfo.add(getServices(id));
		plusInfo.addAll(getCarburants(id));

		return plusInfo;
	}

	
		/**
		 * Méthode permettant d'ajouter l'ensemble des données du fichier xml dans la base de donnée
		 * On peut modifer la valeur de la boucle for pour ajouter uniquement quelques éléments
		 * @return la méthode retourne true si les données on était ajouté à la BDD et false si un problème a été rencontré
		 */
		public boolean ajoutDataDAO() {
			if (connect()) {
				LectureData lecture = new LectureData();
				//Initialisation pour la lecture du fichier de données
				Element root=lecture.liaisonFichier();
				XPath path = lecture.getPath();
				
				//Initialisation de l'expression pour récupérer dans le fichier xml
				String expression = "/pdv_liste[1]/pdv";
				
				//Initialisation des paramètres à ajouter dans la BDD
				String id, latitude, longitude, ville, adresse, lundi, mardi, mercredi, jeudi, vendredi, samedi, dimanche, services;
				String Gazole, SP95, E85, GPLc, E10, SP98;
				
				
				try {
					NodeList nodeList = (NodeList)path.evaluate(expression, root, XPathConstants.NODESET);
					for (int i = 0; i<nodeList.getLength(); i++) {
						//Récupération des différentes valeurs pour une station
						id = nodeList.item(i).getAttributes().getNamedItem("id").getTextContent();
						latitude = nodeList.item(i).getAttributes().getNamedItem("latitude").getTextContent();
						longitude = nodeList.item(i).getAttributes().getNamedItem("longitude").getTextContent();
						ville = lecture.getVille(id);
						adresse = lecture.getAdresse(id);
						lundi = lecture.getHorairesJour(id, 1);
						mardi = lecture.getHorairesJour(id, 2);
						mercredi = lecture.getHorairesJour(id, 3);
						jeudi = lecture.getHorairesJour(id, 4);
						vendredi = lecture.getHorairesJour(id, 5);
						samedi = lecture.getHorairesJour(id, 6);
						dimanche = lecture.getHorairesJour(id, 7);
						services = lecture.getServices(id);
						Gazole = lecture.getUnCarburant(id, 1);
						SP95 = lecture.getUnCarburant(id, 2);
						E85 = lecture.getUnCarburant(id, 3);
						GPLc = lecture.getUnCarburant(id, 4);
						E10 = lecture.getUnCarburant(id, 5);
						SP98 = lecture.getUnCarburant(id, 6);
						
						//Requête SQL pour l'ajout à la base de données
						String query = "insert into "+table+"(id, latitude, longitude, ville, adresse, "
								+ "lundi, mardi, mercredi, jeudi, vendredi, samedi, dimanche, services,Gazole, SP95, E85, GPLc, E10, SP98)"
								+ " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
						PreparedStatement ps  = connection.prepareStatement(query);
						ps.setString(1, id);
						ps.setString(2, latitude);
						ps.setString(3, longitude);
						ps.setString(4, ville);
						ps.setString(5, adresse);
						ps.setString(6, lundi);
						ps.setString(7, mardi);
						ps.setString(8, mercredi);
						ps.setString(9, jeudi);
						ps.setString(10, vendredi);
						ps.setString(11, samedi);
						ps.setString(12, dimanche);
						ps.setString(13, services);
						ps.setString(14, Gazole);
						ps.setString(15, SP95);
						ps.setString(16, E85);
						ps.setString(17, GPLc);
						ps.setString(18, E10);
						ps.setString(19, SP98);
						ps.execute();
						
					}
					return true;
				}catch(XPathExpressionException e) {
					e.printStackTrace();
					return false;
				}catch (SQLException e) {
					e.printStackTrace();
					return false;
				}finally {
					close();
				}
			}else {
				return false;
			}
		}
	
}
