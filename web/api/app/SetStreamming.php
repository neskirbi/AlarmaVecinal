<?php
require_once"funciones/funciones.php";
include "app/Conexion.php";

class SetStreamming{

	
	private $mysqli=null;
	
	public function __construct(){
		$mysql=new Conexion();
		$this->mysqli=$mysql->Conectar();
		//$this->mysqli->set_charset('utf8mb4');
		$this->Set();
	}

	private function Set(){


		$id_transmision=uuid();
		$id_usuario=Inyeccion(param('id_usuario'),$this->mysqli);
		$id_grupo=Inyeccion(param('id_grupo'),$this->mysqli);
		$fecha=fechahora();
			
		$sql="INSERT into transmisiones (id_transmision,id_usuario,id_grupo,fecha) values('$id_transmision','$id_usuario','$id_grupo','$fecha')  ";
		
		if($this->mysqli->query($sql)){			
			echo '{"id_transmision":"'.$id_transmision.'"}';
		}else{
			echo '{"respuesta":"0"}';
		}	
		
	}

	
}


?>