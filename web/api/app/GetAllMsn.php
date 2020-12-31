<?php
require_once"funciones/funciones.php";
include "app/Conexion.php";

class GetAllMsn{

	
	private $mysqli=null;
	
	public function __construct(){
		$mysql=new Conexion();
		$this->mysqli=$mysql->Conectar();
		//$this->mysqli->set_charset('utf8mb4');
		$this->Get();
	}

	private function Get(){

		
		
		$sql="SELECT cha.mensaje ,usu.nombres,cha.fecha,cha.tipo 
		from chat as cha 
		join usuarios as usu on usu.id_usuario=cha.id_usuario 
		order by cha.fecha asc ";
		$result=array();
		$rows=$this->mysqli->query($sql);
		echo"<br>".$rows->num_rows;
		while($row=$rows->fetch_array(MYSQLI_ASSOC)){
			if($row['tipo']=="1")
			echo"<br>".$row['fecha'].'  '.$row['nombres'].":  ".base64_decode($row['mensaje']);
		else 
			echo"<br>".$row['nombres'].":  Audiooooooooooo";
		}
		//echo json_encode($result);
		
		//GetRowsJson($this->mysqli->query($sql));	
		
		
	}

	
}


?>