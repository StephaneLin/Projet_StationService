 
 <%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>        
<%@ page import="stations_essence.DAOAll"%>   
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.Arrays" %>
<!DOCTYPE html>
<html>
<head>
<meta name="viewport" content="initial-scale=1.0">
<meta charset="ISO-8859-1">
<title>Station Service </title>
	<style>
    	/* Always set the map height explicitly to define the size of the div
    	* element that contains the map. */
   		#map {
   			text-align: left;
   			margin-left: 10px;
       		height: 75%;
       		width: 100%;
     	}
   		/* Optional: Makes the sample page fill the window. */
      	html, body {
        	height: 100%;
        	margin: 0;
        	padding: 0;
      	}
      	.center{
      		line-height: 200px
      		padding-top : 4px;
      		text-align:center;
      	}
      	img{
      		width: 3%;
  			height: 3%;	
      	}
      	
	</style>
</head>

<body>
	<header class="center">
		<h1>
			<img src="https://image.freepik.com/icones-gratuites/logo-station-carburant_318-9138.jpg" alt="fuel station"> 
			Stations services <%=request.getParameter("id")%>
		</h1>
	</header>
	
    	<div id="map"></div>
    	<marquee behavior="scroll">Sélectionnez la station de votre choix pour obtenir des informations sur celle-ci @copyright Fillatre - Lin</marquee>
    		<script>
    function initMap() {
    	var position = {lat: 49.19772905, lng: -0.46096408564131};
    	var map = new google.maps.Map(document.getElementById('map'), {
            center: position, zoom: 12 });
    	
		
		
    	var infoWindow = new google.maps.InfoWindow;	

    	if (navigator.geolocation) {
    		navigator.geolocation.getCurrentPosition(function(position){
    			var pos = {
    					lat: position.coords.latitude,
    					lng: position.coords.longitude
    			};
				
    			<%
    			ArrayList <Double> position = new ArrayList<Double>();

    			ArrayList<String> listId = new ArrayList<String>();
    			listId = (ArrayList<String>) request.getAttribute("id");
    			
    			ArrayList<ArrayList<Double>> pos = new ArrayList<ArrayList<Double>>();
    			pos = (ArrayList<ArrayList<Double>>) request.getAttribute("pos");
    			
    			ArrayList<ArrayList<Double>> posSta = (ArrayList<ArrayList<Double>>) request.getAttribute("posSta");
    			
    			ArrayList<ArrayList<ArrayList<String>>> infos = (ArrayList<ArrayList<ArrayList<String>>>) request.getAttribute("infos");
    			
    			ArrayList<ArrayList<String>> moreInfos = (ArrayList<ArrayList<String>>) request.getAttribute("plusInf");
    			
    			ArrayList<Float> dist = (ArrayList<Float>) request.getAttribute("dis");
    			//On parcourt la liste des id pour créer les markers configurer au clique sur chaque marker, l'affichage des données correspondantes
    			for (int i =0 ; i<listId.size(); i++){
    					position = pos.get(i);
    					ArrayList <Double> positionSta = posSta.get(i);
    					float distance = dist.get(i); 
   						
    					ArrayList<ArrayList<String>> mainInfos =  infos.get(i);
    					ArrayList <String> plusInfos = moreInfos.get(i);
    					if (position.size()==2){
    						//On récupère la position d'une station
	    					double latitudeS = position.get(0)/100000;
	    					double longitudeS = position.get(1)/100000;
	    					
	    					//On créé la liste des carburants pour l'affichage
	    					ArrayList <String> listeCarburants = new ArrayList<String>();
	    					listeCarburants.add("Gazole");
	    					listeCarburants.add("SP95");
	    					listeCarburants.add("E85");
	    					listeCarburants.add("GPLc");
	    					listeCarburants.add("E10");
	    					listeCarburants.add("SP98");
	    					
    					%>
    					<!-- On paramètre le contenu qui sera dans le marker après le clique-->
    					var contentString = "<h2>Distance : <%=distance/1000%> km</h2>"
    							+"<h2>Ville : <%=mainInfos.get(0).get(0)%></h2>"
    							+"<h2>Adresse : <%=mainInfos.get(0).get(1)%></h2>"
    							+"<h2>Horaires</h2>"
    							+"<form><select size=\"1\">"
    							+"<option selected>Lundi 		: <%=mainInfos.get(1).get(0)%> </option>"
    							+"<option>Mardi 		: <%=mainInfos.get(1).get(1)%> </option>"
    							+"<option>Mercredi 		: <%=mainInfos.get(1).get(2)%> </option>"
    							+"<option>Jeudi 		: <%=mainInfos.get(1).get(3)%> </option>"
    							+"<option>Vendredi 		: <%=mainInfos.get(1).get(4)%> </option>"
    							+"<option>Samedi 		: <%=mainInfos.get(1).get(5)%> </option>"
    							+"<option>Dimanche 		: <%=mainInfos.get(1).get(6)%> </option>"
    							+"</select></form>"
    							+"<h2>Services : </h2>"
    							+" <p><%=plusInfos.get(0)%></p>"
    							+"<h2>Carburants</h2>";
    							<%
    							//Affichage des carburants présents dans la station en parcourant la liste créer précédemment pour les noms
    							for (int j=0; j< 6;j++){
    								if (plusInfos.get(j+1)!=null){
    									%>
    									contentString+="<p><%=listeCarburants.get(j)%> : <%=plusInfos.get(j+1)%> euros</p>";
    									<%
    								}
    							}
    							%>
    					<!-- on ajoute le contenu texte à l'infowindow numéro i pour que chaque marker est un nom différent -->
    					var infowindow<%=i%> = new google.maps.InfoWindow({
    					    content: contentString
    					  });
    					<!-- on créé le marker i avec l'infowindow i associée -->
	    				var marker<%=i%> = new google.maps.Marker({position :{lat:<%=latitudeS%>, lng:<%=longitudeS%>}, map: map, title : <%=latitudeS%> + ' ' + <%=longitudeS%>});
	    				marker<%=i%>.addListener('click', function(){
	    					infowindow<%=i%>.open(map, marker<%=i%>);	
	    				});
    				<%	}			
    			}		
        	%>
				
    	    	<!-- on affiche par une petite fenêtre où se situe l'utilisateur -->
    			infoWindow.setPosition(pos);
    			infoWindow.setContent('Vous etes ici !');
    			infoWindow.open(map);
    			
    			<!-- -->
    			map.setCenter(pos);
    		},function(){
    			handleLocationError(true, infoWindow, map.getCenter());
    		});
    		}else{
    			handleLocationError(true, infoWindow, map.getCenter());
    			}
    		}
    function handleLocationError(browserHasGeolocation, infoWindow, pos) {
        infoWindow.setPosition(pos);
        infoWindow.setContent(browserHasGeolocation ?
                              'Error: The Geolocation service failed.' :
                              'Error: Your browser doesn\'t support geolocation.');
        infoWindow.open(map);
      }
    

    
    </script>
		
    	    
    		<script async defer
    			src="https://maps.googleapis.com/maps/api/js?key=AIzaSyCP9qSQtRz-og4UWeUJX0_rqASIZyYAr4Y&callback=initMap"> 
    	    </script>
    	</body>
    	</html>