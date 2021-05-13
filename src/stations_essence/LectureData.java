package stations_essence;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class LectureData {


	/**
	 * M�thode permettant de cr�er la liaison avec le fichier en retournant le document
	 * @return
	 */
	public Element liaisonFichier() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Element root = null;
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			File fileXML = new File("PrixCarburants_instantane_Mini.xml");
			Document xml = builder.parse(fileXML);
			root = xml.getDocumentElement();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return root;
	}

	/**
	 * M�thode permettant de r�cup�rer le path
	 * @return
	 */
	public XPath getPath() {
		XPathFactory xpf = XPathFactory.newInstance();
		XPath path = xpf.newXPath(); 
		return path;
	}


	
	/**
	 * M�thode permettant de r�cuperer des donn�es "simples" en fonction du nom de la donn�e (parametre) et de l'id de la station
	 * @param parametre
	 * @param valeurId
	 * @return
	 */
	public String getAttribut(String parametre, String valeurId) {
		//Initialisation pour la lecture du fichier de donn�es
		Element root=liaisonFichier();
		XPath path = getPath();

		//Initialisation de l'expression pour la recherche et du param�tre � retourner
		String expression = "/pdv_liste[1]/pdv[@id='"+valeurId+"']/"+parametre+"[1]";
		String lAdresse=null;
		try {
			lAdresse = path.evaluate(expression, root);
		}catch(XPathExpressionException e) {
			e.printStackTrace();
		}

		return lAdresse;
	}


	/**
	 * Methode permettant de retourner la ville d'une station en fonction de son Id
	 * @param id
	 * @return
	 */
	public String getVille(String id) {
		return getAttribut("ville", id);
	}

	/**
	 * Methode permettant de retourner l'adresse d'une station en fonction de son Id
	 * @param id
	 * @return
	 */
	public String getAdresse (String id) {
		return getAttribut("adresse", id);
	}

	/**
	 * M�thode permettant de r�cuperer les horaires d'ouverture/fermeture d'une station en fonction de son id et le jour souhait�
	 * @param id
	 * @param idJour
	 * @return	On retourne une liste de taille 2 si il y a des horaires et de taille 1 sinon indiquant si c'est un automate ou si il n'y a aucun horaire d'indiqu�
	 */
	public String getHorairesJour (String id, int idJour){
		//Initialisation pour la lecture du fichier de donn�es
		Element root=liaisonFichier();
		XPath path = getPath();

		//Variable permettant de tester si la station est ouverte ou ferm� ce jour l� ouverte="" ou ferme ="1"
		String ferme= null;

		//Variables qui sera renvoy� � la fin de la m�thode
		String horaires = null;

		//Expression pour la recherche de l'ouverture ou non de a station
		String expression = "/pdv_liste[1]/pdv[@id='"+id+"']/horaires/jour[@id='"+idJour+"']";
		NodeList strList;
		try {
			strList = (NodeList)path.evaluate(expression, root, XPathConstants.NODESET);

			ferme = strList.item(0).getAttributes().getNamedItem("ferme").getTextContent();
			if (ferme.equals("1")) {
				horaires="Ferm�";
			}else {
				String newExpression = "/pdv_liste[1]/pdv[@id='"+id+"']/horaires/jour[@id='"+idJour+"']/horaire[1]";
				strList = (NodeList)path.evaluate(newExpression, root, XPathConstants.NODESET);
				if (strList.getLength()>0) {
					horaires=strList.item(0).getAttributes().getNamedItem("ouverture").getTextContent()+" - "+strList.item(0).getAttributes().getNamedItem("fermeture").getTextContent();
				}else {
					horaires="Automate 24-24"; //A MODIFIER SELON CE QU'ON VEUT AJOUTER
				}
			}
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}catch(NullPointerException e) {
			horaires="Aucune information"; //A MODIFIER SELON CE QU'ON VEUT AJOUTER
		}

		return horaires;
	}

	/**
	 * M�thode permettant de r�cup�rer sous forme d'une liste de liste, les horaires d'une stations sur la semaine
	 * Une sous-liste correspondant � un jour
	 * @param id
	 * @return
	 */
	public ArrayList<String> getHoraires (String id) {
		//Cr�ation de la liste des horaires
		ArrayList <String> lesHoraires = new ArrayList <String>();

		//on appel 7 fois la m�thode getHorairesJour en mettant le parametre du jour (1�7)
		for(int i=1; i<=7;i++) {
			lesHoraires.add(getHorairesJour(id, i));

		}

		return lesHoraires;
	}



	/**
	 * Methode permettant de retourner les services d'une station en fonction de son Id
	 * @param id
	 * @return
	 */
	public String getServices (String id) {
		//Initialisation pour la lecture du fichier de donn�es
		Element root=liaisonFichier();
		XPath path = getPath();

		//Initialisation de l'expression pour la recherche et du param�tre � retourner
		String expression = "/pdv_liste[1]/pdv[@id='"+id+"']/services[1]";
		String services=null;
		try {
			services = path.evaluate(expression, root);
			services = services.replaceAll("[\r\n]+", ",");
		}catch(XPathExpressionException e) {
			e.printStackTrace();
		}
		if (services.length()==0) {
			return "Aucun service transmis";
		}else {
			return services;
		}
	}

	/**
	 * m�thode permettant de r�cup�rer un carburant en fonction de l'id du carburant et de la station
	 * @param id
	 * @param idCarburant
	 * @return
	 */
	public String getUnCarburant(String id, int idCarburant){
		//Initialisation pour la lecture du fichier de donn�es
		Element root=liaisonFichier();
		XPath path = getPath();

		String leCarburant = null;;
		NodeList strList;
		String newExpression = "/pdv_liste[1]/pdv[@id='"+id+"']/prix[@id='"+idCarburant+"']";
		try {
			strList = (NodeList)path.evaluate(newExpression, root, XPathConstants.NODESET);
			if (strList.getLength()>0) {
				//leCarburant.add(strList.item(0).getAttributes().getNamedItem("nom").getTextContent());
				//leCarburant.add(strList.item(0).getAttributes().getNamedItem("valeur").getTextContent());
				//leCarburant.add(strList.item(0).getAttributes().getNamedItem("maj").getTextContent());
				leCarburant = strList.item(0).getAttributes().getNamedItem("valeur").getTextContent();
			}
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}

		return leCarburant;
	}

	/**
	 * Cette m�thode permet de r�cup�rer une liste de 7 listes qui sont vides ou non et qui contiennent le nom des carburants avec le prix de celui-ci et la date de mise � jour
	 * @param id
	 * @return
	 */
	public ArrayList <String> getCarburant(String id){
		//initialisation de la liste qui contiendra l'ensemble des carburants de la station avec le prix associ�
		ArrayList<String> carburants = new ArrayList<String>();

		for(int i=1; i<8; i++) {
			carburants.add(getUnCarburant(id, i));
		}
		return carburants;
	}

	/**
	 * M�thode permettant de r�cuperer les donn�es secondaires � afficher
	 * @param id
	 * @return 
	 */
	public ArrayList <String> getPlusInfo(String id){


		ArrayList <String> plusInfo = new ArrayList<String>();



		plusInfo.add(getServices(id));
		plusInfo.addAll(getCarburant(id));

		return plusInfo;
	}

}



