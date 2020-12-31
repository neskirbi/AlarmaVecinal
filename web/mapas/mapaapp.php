<?php 
include"../funciones/funciones.php";
include"../head.php";
$lat=param("lat");
$lon=param("lon");
$tag=param("tag");
?><!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />

	<style type="text/css">
		
		body {
		    padding: 0;
		    margin: 0;
		}
		html, body, #mapid {
		    height: 100%;
		    width: 100vw;
		}
	</style>


	
	<title>Local</title>
</head>
<body>
	<div id="mapid"></div>
</body>

<script type="text/javascript">
		var lat=<?php echo $lat;?>,lon=<?php echo $lon;?>,tag='<center><?php echo $tag;?></center>';
		var map = L.map('mapid').setView([lat, lon], 18);

		L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
		    attribution: 'Map data &copy; <a href="https://www.openstreetmap.org/">OpenStreetMap</a> contributors, <a href="https://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="https://www.mapbox.com/">Mapbox</a>',
		    maxZoom: 18,
		    id: 'mapbox/streets-v11',
		    tileSize: 512,
		    zoomOffset: -1,
		    accessToken: 'your.mapbox.access.token'
		}).addTo(map);
	 	L.marker([lat, lon]).addTo(map)
                .bindPopup(tag)
                .openPopup();

	map.locate({setView: true, maxZoom: 16});


	
	</script>
</html>