<?php
require_once"funciones/funciones.php";
include "app/Conexion.php";

class CargarArchivo{

	
	private $mysqli=null;
	
	public function __construct(){
		$mysql=new Conexion();
		$this->mysqli=$mysql->Conectar();
		//$this->mysqli->set_charset('utf8mb4');
		$this->Guardar();
	}

	private function Guardar(){


		$id_archivo=Inyeccion(param('id_archivo'),$this->mysqli);
		$id_grupo=Inyeccion(param('id_grupo'),$this->mysqli);
		$indice=Inyeccion(param('indice'),$this->mysqli);
		$tipo=Inyeccion(param('tipo'),$this->mysqli);
		$archivo=str_replace(" ","+",(Inyeccion(param('archivo'),$this->mysqli)));
		$fecha=fechahora();

		$archivot="../temporales/".$id_archivo.".txt";

		switch ($tipo) {
			case 'audio':
				$patharchivo="../chat/audios/".$id_grupo."/";
				$archivofile="../chat/audios/".$id_grupo."/".$id_archivo.".jpg";
				break;

			case 'imagen':
				$patharchivo="../chat/imagenes/".$id_grupo."/";
				$archivofile="../chat/imagenes/".$id_grupo."/".$id_archivo.".jpg";
				break;
			
			case 'video':
				$patharchivo="../chat/videos/".$id_grupo."/";
				$archivofile="../chat/videos/".$id_grupo."/".$id_archivo.".jpg";
				break;
		}

		
		

	
		if($indice=='1'){
			if(!file_exists($archivot)){
				$this->CrearArchivo($archivot);
			}
			$this->GuardarImagen($this->Concatenar($archivo,$archivot),$patharchivo,$archivofile);
		}else{
			if(!file_exists($archivot)){
				$this->CrearArchivo($archivot);
			}
			$this->Concatenar($archivo,$archivot);
		}

		
		
	}

	function CrearArchivo($archivot){
		$file = fopen($archivot,'w');
		fwrite($file, '');
		fclose($file);
	}

	function Concatenar($archivo,$archivot){

		$file = fopen($archivot,'r');
		$texto="";

		fseek($file, 0);

		/*while(!feof($file)){
		    $texto = fgets($file);
		    //echo $linea;
		}*/
		while ($line = fgets($file)) {
			$texto.=$line;
		}
		/*if(filesize($archivot)>0){

			$texto=fread($file, filesize($archivot));
			echo'{"respuesta":"'.substr($texto, 0,10).'"}';
		}*/
		$texto=$texto.$archivo;
		$file = fopen($archivot,'w');
		if(fwrite($file, $texto)){
	    	echo'{"respuesta":"C1"}';
	    }else{
	    	echo'{"respuesta":"0"}';
	    }
		fclose($file);
		return $texto;
	}

	function GuardarImagen($archivo,$patharchivo,$archivofile){
		

		if(!is_dir($patharchivo)){
			mkdir($patharchivo, 0777, true);
			
		}

		//$archivo=str_replace(" ","+",$archivo);
		//orden por el servidor
		//$file = fopen($path.$numero.".jpg", "wb");
		//orden por el telefono
		//echo$archivofile."------";
		$file = fopen($archivofile, "w");
	    

	    if(fwrite($file, base64_decode($archivo))){
	    	echo'{"respuesta":"G1"}';
	    }else{
	    	echo'{"respuesta":"0"}';
	    }
	    fclose($file);
	}
	

	

	
}

/**

$myfile = fopen("newfile.txt", "w") or die("Unable to open file!");
$txt = "John Doe\n";
fwrite($myfile, $txt);
$txt = "Jane Doe\n";
fwrite($myfile, $txt);
fclose($myfile);

/


?>