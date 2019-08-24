<?php  
error_reporting(E_ALL); 
ini_set('display_errors',1); 

include('dbcon.php');



//POST 값을 읽어온다.
$productId=isset($_POST['productId']) ? $_POST['productId'] : '';
$optionNum=isset($_POST['optionNum']) ? $_POST['optionNum'] : '';
$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");


if ($productId != "" ){ 

    $sql="select * from Product where productId='$productId' and optionNum='$optionNum' ";
    $stmt = $con->prepare($sql);
    $stmt->execute();
 
    if ($stmt->rowCount() == 0){

      
        echo "상품을 찾을 수 없습니다. ";
    }
    else{

        $data = array(); 

        while($row=$stmt->fetch(PDO::FETCH_ASSOC)){

            extract($row);

            array_push($data, 
                array(
                'productId'=>$row["productId"],
                'optionNum'=>$row["optionNum"],
                'name'=>$row["name"],
                'category'=>$row["category"],
                'length'=>$row["length"],
                'image'=>$row["image"],
                'price'=>$row["price"],
                'size'=>$row["size"],
                'color'=>$row["color"],
                'fabric'=>$row["fabric"],
                'patter'=>$row["patter"],
                'detail'=>$row["detail"]
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