<?php
require_once"funciones/funciones.php";
include "app/Conexion.php";

class UpdatefbToken{

	
	private $mysqli=null;
	
	public function __construct(){
		$mysql=new Conexion();
		$this->mysqli=$mysql->Conectar();
		$this->Update();
	}

	private function Update(){

		$id_usuario=Inyeccion(param('id_usuario'),$this->mysqli);
		$fbtoken=Inyeccion(param('fbtoken'),$this->mysqli);		
		
		$sql="UPDATE usuarios set fbtoken='$fbtoken' where id_usuario='$id_usuario' ";
		$this->mysqli->query($sql);
		
		
	}

	
}


?>