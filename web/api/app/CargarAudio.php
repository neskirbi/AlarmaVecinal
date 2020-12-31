<?php
require_once"funciones/funciones.php";
include "app/Conexion.php";

class CargarAudio{

	
	private $mysqli=null;
	
	public function __construct(){
		$mysql=new Conexion();
		$this->mysqli=$mysql->Conectar();
		//$this->mysqli->set_charset('utf8mb4');
		$this->Guardar();
	}

	private function Guardar(){


		$id_audio=str_replace(" ","+",Inyeccion(param('id_audio'),$this->mysqli));
		$id_grupo=Inyeccion(param('id_grupo'),$this->mysqli);
		$audio=str_replace(" ","+",(Inyeccion(param('audio'),$this->mysqli)));
		$fecha=fechahora();

		if($id_audio!="" && $audio!=""){
			 $path="../chat/audios/".$id_grupo."/";

			if(!is_dir($path)){
				mkdir($path, 0777, true);
				
			}

			$narchivos = glob($path . "*");
			
			$numero=count($narchivos);

			
			//orden por el servidor
			//$file = fopen($path.$numero.".jpg", "wb");
			//orden por el telefono
			$file = fopen($path.$id_audio.".mp3", "wb");
		    

		    if(fwrite($file, base64_decode($audio))){
		    	echo'{"respuesta":"1","audio":"'.$audio.'"}';
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