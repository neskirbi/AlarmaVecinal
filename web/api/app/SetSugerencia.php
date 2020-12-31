<?php
require_once"funciones/funciones.php";
include "app/Conexion.php";

class SetSugerencia{

	
	private $mysqli=null;
	
	public function __construct(){
		$mysql=new Conexion();
		$this->mysqli=$mysql->Conectar();
		//$this->mysqli->set_charset('utf8mb4');
		$this->Set();
	}

	private function Set(){


		$id_sugerencia=uuid();
		$id_usuario=Inyeccion(param('id_usuario'),$this->mysqli);
		$sugerencia=LimpiaBase64(Inyeccion(param('sugerencia'),$this->mysqli));
		$fecha=fechahora();
			
		$sql="INSERT into sugerencias (id_sugerencia,id_usuario,sugerencia,fecha) values('$id_sugerencia','$id_usuario','$sugerencia','$fecha')  ";
		
		if($this->mysqli->query($sql)){

			MailTo('neskirbi@gmail.com','¡Nueva Sugerencia!','Lleg&oacute; una sugerencia a Alarma Vecinal.<br><br>'.base64_decode($sugerencia).'<br><br> ID: '.$id_usuario);
			echo '{"respuesta":"1"}';
		}else{
			echo '{"respuesta":"0"}';
		}	
		
		
		
	}

	
}


?>