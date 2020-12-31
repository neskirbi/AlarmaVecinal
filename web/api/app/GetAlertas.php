<?php
require_once"funciones/funciones.php";
include "app/Conexion.php";

class GetAlertas{

	
	private $mysqli=null;
	
	public function __construct(){
		$mysql=new Conexion();
		$this->mysqli=$mysql->Conectar();
		$this->Get();
	}

	private function Get(){

		$id_grupo=Inyeccion(param('id_grupo'),$this->mysqli);
		
		$sql="SELECT ale.id_alerta,usu.id_usuario,ale.imagen,ale.asunto,ale.mensaje,ale.fecha,concat(usu.nombres,' ',usu.apellidos) as nombre 
		from alertas as ale 
		LEFT JOIN usuarios as usu on usu.id_usuario=ale.id_usuario 
		where ale.id_grupo ='$id_grupo' order by ale.fecha desc limit 10  ";
		
		GetRowsJson($this->mysqli->query($sql));	
		
		
	}

	
}


?>