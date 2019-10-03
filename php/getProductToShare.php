<?php  
error_reporting(E_ALL); 
ini_set('display_errors',1); 

include('dbcon.php');



//POST 값을 읽어온다.
$uid=isset($_POST['uid']) ? $_POST['uid'] : '';
$wishProductName=isset($_POST['wishProductName']) ? $_POST['wishProductName'] : '';

$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");


if ($uid != "" ){ 

    $sql="select * from WishProduct where uid='$uid' and wishProductName='$wishProductName' ";
    $stmt = $con->prepare($sql);
    $stmt->execute();
 
    if ($stmt->rowCount() == 0){

        echo "'관심상품 중 ";
        echo $wishProductName;
        echo "' 은 찾을 수 없습니다. 다시 입력을 해주세요.";
    }
    else{

        $data = array(); 

        while($row=$stmt->fetch(PDO::FETCH_ASSOC)){

            extract($row);

            array_push($data, 
                array('uid'=>$row["uid"],
                'productId'=>$row["productId"],
                'optionNum'=>$row["optionNum"]
            ));
        }


        if (!$android) {
            echo "<pre>"; 
            print_r($data); 
            echo '</pre>';
        }else
        {
            header('Content-Type: application/json; charset=utf8');
            $json = json_encode(array("getWishListItem"=>$data), JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
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