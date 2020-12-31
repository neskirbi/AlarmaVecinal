<?php
require_once"funciones/funciones.php";
include "app/Conexion.php";

class SetBloqueo{

	
	private $mysqli=null;
	
	public function __construct(){
		$mysql=new Conexion();
		$this->mysqli=$mysql->Conectar();
		$this->Set();
	}

	private function Set(){


		$id_bloqueado=uuid();
		$id_grupo=Inyeccion(param('id_grupo'),$this->mysqli);
		$id_usuario=Inyeccion(param('id_usuario'),$this->mysqli);
		$fecha=fechahora();

		$sql="UPDATE usuarios set id_grupo='' where id_usuario='$id_usuario' and id_grupo='$id_grupo' ";
		
		if($this->mysqli->query($sql)){
			$sql="INSERT into bloqueados (id_bloqueado,id_grupo,id_usuario,fecha) values('$id_bloqueado','$id_grupo','$id_usuario','$fecha')  ";
			
			if($this->mysqli->query($sql)){
				echo '{"response":"1"}';
			}else{
				echo '{"response":"0"}';
			}	
		}else{
			echo'{"response":"0"}';
		}
		
		
		
		
	}

	
}


?>