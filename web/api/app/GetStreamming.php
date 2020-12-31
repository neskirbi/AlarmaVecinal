<?php
require_once"funciones/funciones.php";
include "app/Conexion.php";

class GetStreamming{

	
	private $mysqli=null;
	
	public function __construct(){
		$mysql=new Conexion();
		$this->mysqli=$mysql->Conectar();
		//$this->mysqli->set_charset('utf8mb4');
		$this->Get();
	}

	private function Get(){

		
		$id_transmision=Inyeccion(param('id_transmision'),$this->mysqli);
		
		$path="../streamming/".$id_transmision."/";

			
		$narchivos = glob($path . "*");
		
		$numero=count($narchivos)-1;

		$fecha=fechahora();
		$fecha2=date("Y-m-d H:i:s",filectime($path));

		$d1=date("Y-m-d H:i:s",filectime($path."/0.jpg"));
		$d2=date("Y-m-d H:i:s",filectime($path."/".$numero.".jpg"));

		$d = date_diff(new DateTime($d1), new DateTime($d2));
		$duracion = $d->format("%H:%I:%S");

		$interval = date_diff(new DateTime($fecha), new DateTime($fecha2));
		$seconds = $interval->days * 24 * 60 * 60;
		$seconds += $interval->h * 60 * 60;
		$seconds += $interval->i * 60;
		$seconds += $interval->s;
		$vivo=0;
		if($seconds<5){
			$vivo=1;
		}

		echo '{"respuesta":"1","shot":"'.$numero.'","vivo":"'.$vivo.'","duracion":"'.$duracion.'"}';	
		
		
	}

	
}


?>