<?php
require_once"funciones/funciones.php";
include "app/Conexion.php";

class CargarVideo{

	
	private $mysqli=null;
	
	public function __construct(){
		$mysql=new Conexion();
		$this->mysqli=$mysql->Conectar();
		//$this->mysqli->set_charset('utf8mb4');
		$this->Guardar();
	}

	private function Guardar(){


		$id_video=Inyeccion(param('id_video'),$this->mysqli);
		$video=str_replace(" ","+",(Inyeccion(param('video'),$this->mysqli)));
		$fecha=fechahora();

		if($id_video!="" && $video!=""){
			 $path="../chat/videoes/".$id_video."/";

			if(!is_dir($path)){
				mkdir($path, 0777, true);
				
			}

			$narchivos = glob($path . "*");
			
			$numero=count($narchivos);

			
			//orden por el servidor
			//$file = fopen($path.$numero.".jpg", "wb");
			//orden por el telefono
			$file = fopen($path.$orden.".jpg", "wb");
		    

		    if(fwrite($file, base64_decode($video))){
		    	echo'{"respuesta":"1","video":"'.$video.'"}';
		    }else{
		    	echo'{"respuesta":"0"}';
		    }
		    fclose($file);
		}else{
			echo'{"respuesta":"0"}';
		}

		
		
	}

	
}


?>