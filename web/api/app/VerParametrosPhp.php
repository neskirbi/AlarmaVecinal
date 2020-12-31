<?php
require_once"funciones/funciones.php";
include "app/Conexion.php";



class VerParametrosPhp{

	public function __construct(){
		echo 'memory_limit = ' . ini_get('memory_limit') . "<br>";
		echo 'upload_max_filesize = ' . ini_get('upload_max_filesize') . "<br>";
		echo 'post_max_size = ' . ini_get('post_max_size') . "<br>";
		echo 'max_execution_time = ' . ini_get('max_execution_time') . "<br>";
		echo 'display_errors = ' . ini_get('display_errors') . "<br>";
		echo 'LimitRequestBody = ' . get_cfg_var('LimitRequestBody') . "<br>";
		

		
	}
}






?>

