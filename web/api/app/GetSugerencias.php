<?php
require_once"funciones/funciones.php";
include "app/Conexion.php";

class GetSugerencias{

	
	private $mysqli=null;
	
	public function __construct(){
		$mysql=new Conexion();
		$this->mysqli=$mysql->Conectar();
		//$this->mysqli->set_charset('utf8mb4');
		$this->Get();
	}

	private function Get(){

		
		
		$sql="SELECT sug.sugerencia ,usu.nombres,sug.fecha 
		from sugerencias as sug 
		join usuarios as usu on usu.id_usuario=sug.id_usuario 
		order by sug.fecha  ";

		$result=array();
		$rows=$this->mysqli->query($sql);
		while($row=$rows->fetch_array(MYSQLI_ASSOC)){
			echo"<br><br>".$row['nombres'].":  ".base64_decode($row['sugerencia']);
		
		//echo json_encode($result);
		
		//GetRowsJson($this->mysqli->query($sql));
		}	
		
		
	}

	
}


?>