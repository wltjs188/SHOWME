<?php  
error_reporting(E_ALL); 
ini_set('display_errors',1); 

include('dbcon.php');



//POST 값을 읽어온다.
$uid=isset($_POST['uid']) ? $_POST['uid'] : '';
$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");


if ($uid != "" ){ 

    $sql="select * from User where uid='$uid'";
    $stmt = $con->prepare($sql);
    $stmt->execute();
 
    if ($stmt->rowCount() == 0){

        echo "'";
        echo $uid;
        echo "'은 찾을 수 없습니다. 사용자 입력을 해주세요.";
    }
    else{

        $data = array(); 

        while($row=$stmt->fetch(PDO::FETCH_ASSOC)){

            extract($row);

            array_push($data, 
                array('uid'=>$row["uid"],
                'name'=>$row["name"],
                'address'=>$row["address"],
                'phoneNum'=>$row["phoneNum"]
            ));
        }


        if (!$android) {
            echo "<pre>"; 
            print_r($data); 
            echo '</pre>';
        }else
        {
            header('Content-Type: application/json; charset=utf8');
            $json = json_encode(array("User"=>$data), JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
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