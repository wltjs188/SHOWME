<?php  
error_reporting(E_ALL); 
ini_set('display_errors',1); 

include('dbcon.php');



//POST 값을 읽어온다.
$uuid=isset($_POST['uuid']) ? $_POST['uuid'] : '';
$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");


if ($uuid != "" ){ 

    $sql="select * from person where uuid='$uuid'";
    $stmt = $con->prepare($sql);
    $stmt->execute();
 
    if ($stmt->rowCount() == 0){

        echo "'";
        echo $uuid;
        echo "'은 찾을 수 없습니다. 사용자 입력을 해주세요.";
    }
    else{

        $data = array(); 

        while($row=$stmt->fetch(PDO::FETCH_ASSOC)){

            extract($row);

            array_push($data, 
                array('uuid'=>$row["uuid"],
                'name'=>$row["name"],
                'gender'=>$row["gender"],
                'height'=>$row["height"],
                'top'=>$row["top"],
                'bottom'=>$row["bottom"],
                'foot'=>$row["foot"]
            ));
        }


        if (!$android) {
            echo "<pre>"; 
            print_r($data); 
            echo '</pre>';
        }else
        {
            header('Content-Type: application/json; charset=utf8');
            $json = json_encode(array("person"=>$data), JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
            echo $json;
        }
    }
}
else {
    echo "사용자를 입력해주세요.";
}

?>



<?php

$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

if (!$android){
?>

<html>
   <body>
   
      <form action="<?php $_PHP_SELF ?>" method="POST">
         uuid: <input type = "text" name = "uuid" />
         <input type = "submit" />
      </form>
   
   </body>
</html>
<?php
}

   
?>