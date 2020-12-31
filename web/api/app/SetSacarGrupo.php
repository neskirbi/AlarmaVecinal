<?php
require_once"funciones/funciones.php";
include "app/Conexion.php";

class SetSacarGrupo{

	
	private $mysqli=null;
	
	public function __construct(){
		$mysql=new Conexion();
		$this->mysqli=$mysql->Conectar();
		$this->Set();
	}

	private function Set(){

	
		$id_usuario=Inyeccion(param('id_usuario'),$this->mysqli);
		$id_grupo=Inyeccion(param('id_grupo'),$this->mysqli);

		$sql="UPDATE usuarios set id_grupo='' where id_usuario='$id_usuario' and id_grupo='$id_grupo' ";
		
		if($this->mysqli->query($sql)){
			echo'{"response":"1"}';
		}else{
			echo'{"response":"0"}';
		}

		
		
	}

	
}


?>