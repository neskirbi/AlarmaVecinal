<?php
require_once"funciones/funciones.php";
include "app/Conexion.php";

class GetVecinosGrupo{

	
	private $mysqli=null;
	
	public function __construct(){
		$mysql=new Conexion();
		$this->mysqli=$mysql->Conectar();
		$this->Get();
	}

	private function Get(){

		$id_grupo=Inyeccion(param('id_grupo'),$this->mysqli);
		
		$sql="SELECT id_usuario,concat(nombres,' ',apellidos) as nombre ,direccion from usuarios where id_grupo='$id_grupo' and id_grupo!=''  ";
		
		GetRowsJson($this->mysqli->query($sql));	
		
		
	}

	
}


?>