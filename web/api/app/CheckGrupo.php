<?php
require_once"funciones/funciones.php";
include "app/Conexion.php";

class CheckGrupo{

	
	private $mysqli=null;
	
	public function __construct(){
		$mysql=new Conexion();
		$this->mysqli=$mysql->Conectar();
		$this->Check();
	}

	private function Check(){

		$id_usuario=Inyeccion(param('id_usuario'),$this->mysqli);
		
		$sql="SELECT id_grupo from usuarios where id_usuario ='$id_usuario'  ";
		
		GetRowsJson($this->mysqli->query($sql));

		
		
		
	}

	
}


?>