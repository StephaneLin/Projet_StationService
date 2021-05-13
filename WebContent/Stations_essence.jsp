<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.ArrayList"%>
<!DOCTYPE html>
<html lang="fr">
<head>
<meta charset="ISO-8859-1">
<title>Station essences</title>
<meta name="viewport" content="initial-scale=1.0">
<meta charset="utf-8">
<link href="style.css" rel="stylesheet">
</head>
<body>
	<header class="center">
		<h1>
			<img
				src="https://image.freepik.com/icones-gratuites/logo-station-carburant_318-9138.jpg"
				alt="fuel station"> Stations essences
		</h1>
	</header>
	<form id="posForm" action="Markers" method="POST">
		<input type="hidden" name="lat" id="lat"><br> <input
			type="hidden" name="lng" id="lng"><br> <br> <input
			type="button" onclick="getPos()"
			value="Cliquer pour voir les stations essences autour de vous">
	</form>
	<div id="map"></div>
	<script>
		function initMap() {
			var position = {
				lat : 46.52863469527167,
				lng : 2.43896484375
			};
			var map = new google.maps.Map(document.getElementById('map'), {
				center : position,
				zoom : 5
			});
			var infoWindow = new google.maps.InfoWindow;

			if (navigator.geolocation) {
				//Recupere la position actuelle de l'utilisateur
				navigator.geolocation.getCurrentPosition(function(position) {
					//Initialise la variable avec les coordonnées l'utilisateur
					var pos = {
						lat : position.coords.latitude,
						lng : position.coords.longitude
					};
					//Change le niveau de zoom
					map.setZoom(13);
					//Centre sur la position de l'utilisateur
					map.setCenter(pos);
					infoWindow.setPosition(pos);
					infoWindow.setContent('Vous etes ici !');
					infoWindow.open(map);

				}, function() {
					handleLocationError(true, infoWindow, map.getCenter());
				});
			} else { //Si le navigateur ne gere pas la geolocalisation
				handleLocationError(true, infoWindow, map.getCenter());
			}
		}
		function handleLocationError(browserHasGeolocation, infoWindow, pos) {
			infoWindow.setPosition(pos);
			infoWindow
					.setContent(browserHasGeolocation ? 'Error: The Geolocation service failed.'
							: 'Error: Your browser doesn\'t support geolocation.');
			infoWindow.open(map);
		}
		function getPos() {
			navigator.geolocation
					.getCurrentPosition(function(position) {
						//Initialise la variable avec les coordonnées l'utilisateur
						document.getElementById("lat").value = position.coords.latitude;
						document.getElementById("lng").value = position.coords.longitude;
						document.getElementById("posForm").submit();
					});
		}
	</script>
	<script async defer
		src="https://maps.googleapis.com/maps/api/js?key=AIzaSyCP9qSQtRz-og4UWeUJX0_rqASIZyYAr4Y&callback=initMap">
		
	</script>
</body>
</html>