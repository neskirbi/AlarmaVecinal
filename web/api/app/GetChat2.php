<?php
require_once"funciones/funciones.php";
include "app/Conexion.php";

class GetChat2{

	
	private $mysqli=null;
	
	public function __construct(){
		$mysql=new Conexion();
		$this->mysqli=$mysql->Conectar();
		//$this->mysqli->set_charset('utf8mb4');
		$this->Get();
	}

	private function Get(){

		$id_grupo=Inyeccion(param('id_grupo'),$this->mysqli);
		$fecha=Inyeccion(param('fecha'),$this->mysqli);
		
		
		$sql="SELECT cha.id_chat,cha.mensaje,usu.id_usuario,CONCAT(usu.nombres,' ', usu.apellidos) as nombre, DATE_FORMAT(cha.fecha, '%H:%i') as hora,cha.fecha as fecha,cha.tipo,TIMEDIFF(cha.fecha,'$fecha')as diiff from chat as cha 
		join usuarios as usu on usu.id_usuario=cha.id_usuario
		where cha.id_grupo ='$id_grupo' and cha.id_grupo !='' and TIMEDIFF(cha.fecha,'$fecha')>'00:00:00' order by fecha desc";
		
		GetRowsJson($this->mysqli->query($sql));	
		
		
	}

	
}


?>