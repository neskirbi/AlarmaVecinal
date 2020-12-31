<?php
require_once"funciones/funciones.php";
include "app/Conexion.php";

class CargarImagen{

	
	private $mysqli=null;
	
	public function __construct(){
		$mysql=new Conexion();
		$this->mysqli=$mysql->Conectar();
		//$this->mysqli->set_charset('utf8mb4');
		$this->Guardar();
	}

	private function Guardar(){


		$id_imagen=str_replace(" ","+",Inyeccion(param('id_imagen'),$this->mysqli));
		$id_grupo=Inyeccion(param('id_grupo'),$this->mysqli);
		$imagen=str_replace(" ","+",(Inyeccion(param('imagen'),$this->mysqli)));
		$fecha=fechahora();

		if($id_imagen!="" && $imagen!=""){
			 $path="../chat/imagenes/".$id_grupo."/";

			if(!is_dir($path)){
				mkdir($path, 0777, true);
				
			}

			$narchivos = glob($path . "*");
			
			$numero=count($narchivos);

			
			//orden por el servidor
			//$file = fopen($path.$numero.".jpg", "wb");
			//orden por el telefono
			$file = fopen($path.$id_imagen.".jpg", "wb");
		    

		    if(fwrite($file, base64_decode($imagen))){
		    	echo'{"respuesta":"1","imagen":"'.$imagen.'"}';
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