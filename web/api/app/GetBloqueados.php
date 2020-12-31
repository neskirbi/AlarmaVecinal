<?php
require_once"funciones/funciones.php";
include "app/Conexion.php";

class GetBloqueados{

	
	private $mysqli=null;
	
	public function __construct(){
		$mysql=new Conexion();
		$this->mysqli=$mysql->Conectar();
		//$this->mysqli->set_charset('utf8mb4');
		$this->Get();
	}

	private function Get(){

		$id_grupo=Inyeccion(param('id_grupo'),$this->mysqli);

		$sql="SELECT blo.id_usuario,blo.id_grupo,concat(usu.nombres,' ',usu.apellidos) as nombre,usu.direccion from bloqueados as blo 
		join usuarios as usu on usu.id_usuario=blo.id_usuario
		where blo.id_grupo ='$id_grupo' order by usu.nombres desc ";
		
		GetRowsJson($this->mysqli->query($sql));	
		
		
	}

	
}


?>