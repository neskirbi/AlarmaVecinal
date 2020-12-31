<?php
require_once"funciones/funciones.php";
include "app/Conexion.php";

class GetPuedoGrupo{

	
	private $mysqli=null;
	
	public function __construct(){
		$mysql=new Conexion();
		$this->mysqli=$mysql->Conectar();
		$this->Get();
	}

	private function Get(){

		$id_usuario=Inyeccion(param('id_usuario'),$this->mysqli);
		$id_grupo=Inyeccion(param('id_grupo'),$this->mysqli);
		
		$sql="SELECT id_grupo from bloqueados where id_usuario='$id_usuario' and id_grupo='$id_grupo' ";
		
		
		GetRowsJson($this->mysqli->query($sql));	
		
		
	}

	
}


?>