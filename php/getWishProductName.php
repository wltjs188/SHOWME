<?php  
error_reporting(E_ALL); 
ini_set('display_errors',1); 

include('dbcon.php');



//POST 값을 읽어온다.
$uid=isset($_POST['uid']) ? $_POST['uid'] : '';

$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");


if ($uid != "" ){ 

    $sql="select * from WishProduct where uid='$uid'";
    $stmt = $con->prepare($sql);
    $stmt->execute();
 
    if ($stmt->rowCount() == 0){

        echo "'";
        echo $uid;
        echo "'의 관심상품 없습니다.";
    }
    else{

        $data = array(); 

        while($row=$stmt->fetch(PDO::FETCH_ASSOC)){

            extract($row);

            array_push($data, 
                array(
                'wishProductName'=>$row["wishProductName"]
            ));
        }


        if (!$android) {
            echo "<pre>"; 
            print_r($data); 
            echo '</pre>';
        }else
        {
            header('Content-Type: application/json; charset=utf8');
            $json = json_encode(array("WishProductName"=>$data), JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
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
         uid: <input type = "text" name = "uid" />
         <input type = "submit" />
      </form>
   
   </body>
</html>
<?php
}

   
?>