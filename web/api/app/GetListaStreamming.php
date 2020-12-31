<?php
require_once"funciones/funciones.php";
include "app/Conexion.php";

class GetListaStreamming{

	
	private $mysqli=null;
	
	public function __construct(){
		$mysql=new Conexion();
		$this->mysqli=$mysql->Conectar();
		$this->Get();
	}

	private function Get(){

		$id_grupo=Inyeccion(param('id_grupo'),$this->mysqli);
		
		$sql="SELECT tra.id_transmision,concat(usu.nombres,' ',usu.apellidos) as nombre,tra.fecha from transmisiones as tra
		left join usuarios as usu on usu.id_usuario=tra.id_usuario order by tra.fecha asc   ";
		GetRowsJson($this->mysqli->query($sql));	
		
		
		
	}

	
}


?>