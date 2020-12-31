<?php
require_once"funciones/funciones.php";
include "app/Conexion.php";

class SetChat{

	
	private $mysqli=null;
	
	public function __construct(){
		$mysql=new Conexion();
		$this->mysqli=$mysql->Conectar();
		//$this->mysqli->set_charset('utf8mb4');
		$this->Set();
	}

	private function Set(){


		$id_chat=Inyeccion(param('id_chat'),$this->mysqli);
		if($id_chat==""){
			$id_chat=uuid();
		}
		$id_grupo=Inyeccion(param('id_grupo'),$this->mysqli);
		if($id_grupo!=""){
			$id_usuario=Inyeccion(param('id_usuario'),$this->mysqli);
			$mensaje=LimpiaBase64(Inyeccion(param('mensaje'),$this->mysqli));
			$tipo=Inyeccion(param('tipo'),$this->mysqli);
			if($tipo==""){
				$tipo="1";
			}
			$fecha=fechahora();
			
			$sql="INSERT into chat (id_chat,id_grupo,id_usuario,mensaje,fecha,tipo) values('$id_chat','$id_grupo','$id_usuario','$mensaje','$fecha','$tipo')  ";
			
			if($this->mysqli->query($sql)){
				echo '{"respuesta":"1"}';
			}else{
				echo '{"respuesta":"0"}';
			}	
		}else{
			echo '{"respuesta":"1"}';
		}
		
		
		
	}

	
}


?>