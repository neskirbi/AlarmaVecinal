<?php
require_once"funciones/funciones.php";
include "app/Conexion.php";

class webrtc{

	
	private $mysqli=null;
	
	public function __construct(){
		$mysql=new Conexion();
		$this->mysqli=$mysql->Conectar();
		//$this->mysqli->set_charset('utf8mb4');
		$this->Set();
	}

	private function Set(){
		print_r($_REQUEST);
		
	}

	
}


?>