<?php
require_once"funciones/funciones.php";
include "app/Conexion.php";

class GuardarStreamming{

	
	private $mysqli=null;
	
	public function __construct(){
		$mysql=new Conexion();
		$this->mysqli=$mysql->Conectar();
		//$this->mysqli->set_charset('utf8mb4');
		$this->Guardar();
	}

	private function Guardar(){


		$id_transmision=Inyeccion(param('id_transmision'),$this->mysqli);
		$foto=str_replace(" ","+",(Inyeccion(param('foto'),$this->mysqli)));
		$fecha=fechahora();
		$orden=Inyeccion(param('orden'),$this->mysqli);

		if($id_transmision!="" && $foto!=""){
			 $path="../streamming/".$id_transmision."/";

			if(!is_dir($path)){
				mkdir($path, 0777, true);
				
			}

			$narchivos = glob($path . "*");
			
			$numero=count($narchivos);

			
			//orden por el servidor
			//$file = fopen($path.$numero.".jpg", "wb");
			//orden por el telefono
			$file = fopen($path.$orden.".jpg", "wb");
		    

		    if(fwrite($file, base64_decode($foto))){
		    	echo'{"respuesta":"1","foto":"'.$foto.'"}';
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