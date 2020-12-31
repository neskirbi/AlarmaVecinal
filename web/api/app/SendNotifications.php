
<?php
require_once"funciones/funciones.php";
include "app/Conexion.php";

class SendNotifications{

	
	private $mysqli=null;
	
	public function __construct(){
		$mysql=new Conexion();
		$this->mysqli=$mysql->Conectar();
		//$this->mysqli->set_charset('utf8mb4');
		$this->Send();
	}

	private function Send(){

		$id_grupo=Inyeccion(param('id_grupo'),$this->mysqli);
		$mensaje=Inyeccion(param('mensaje'),$this->mysqli);

		$sql="SELECT fbtoken from usuarios where id_grupo='$id_grupo' and fbtoken!=''   ";

		$rows=$this->mysqli->query($sql);

    $tokens = array();
		while($row=$rows->fetch_array(MYSQLI_ASSOC)){
		  $tokens[]=$row['fbtoken'];

		}
        $message = array('message' => $mensaje);
        $notification = array('title' => "Alarma Vecinal",'body' => $mensaje,'imagen'=>'http://app-lab.com.mx/AlarmaVecinal/imagenes/notificaciones/sobre.png');
        //$message = array("message" => " FCM PUSH NOTIFICATION TEST MESSAGE");
        $pushStatus = $this->sendPushNotification($tokens, $message,$notification);
		
		//sendPushNotification();
		
	}

	function sendPushNotification($registration_ids, $message,$notification) {

      

    $url = "https://fcm.googleapis.com/fcm/send";

    //prepare data
    $fields = array (
        'registration_ids' => $registration_ids,
        'data' => $message,
        'notification'=> $notification
    );
    echo $fields = json_encode ( $fields ); 

    //header data
    $headers = array ('Authorization: key=AAAABVrPcE0:APA91bHeNJqDfN9Zyd6Sg0f8LW89kbGrBwfb2DqifJiIthUmM_hyxgaE3utpUyvVtp7FeaKrU-qMBHgpPBm6jXTwAEabLKdbXvV5LaRQpaG0KSr4GukGFrmfNkdBP2FgS2_r-iXHiewr', 'Content-Type: application/json');

    //initiate curl request
    $ch = curl_init ();
    curl_setopt ( $ch, CURLOPT_URL, $url );
    curl_setopt ( $ch, CURLOPT_POST, true );
    curl_setopt ( $ch, CURLOPT_HTTPHEADER, $headers );
    curl_setopt ( $ch, CURLOPT_RETURNTRANSFER, true );
    curl_setopt ( $ch, CURLOPT_POSTFIELDS, $fields );

    // execute curl request
    $result = curl_exec ( $ch );

    //close curl request
    curl_close ( $ch );

    //return output
    echo $result;

	}

	
}


?>