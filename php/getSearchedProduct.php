<?php  
//상품 검색
error_reporting(E_ALL); 
ini_set('display_errors',1); 

include('dbcon.php');



//POST 값을 읽어온다.
$category=isset($_POST['category']) ? $_POST['category'] : '';
$color=isset($_POST['color']) ? $_POST['color'] : '';
$length=isset($_POST['length']) ? $_POST['length'] : '';
$size=isset($_POST['size']) ? $_POST['size'] : '';
$pattern=isset($_POST['pattern']) ? $_POST['pattern'] : '';
//$fabric=isset($_POST['fabric']) ? $_POST['fabric'] : '';
$detail=isset($_POST['detail']) ? $_POST['detail'] : '';

$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

$s1=($color==""?"":"and color='$color'");
$s2=($length==""?"":"and length='$length'");
$s3=($size==""?"":"and size='$size'");
$s4=($pattern==""?"":"and pattern='$pattern'");
//$s5=($fabric==""?"":"and fabric='$fabric'");
$s6=($detail==""?"":"and detail='$detail'");

if ($category != "" ){ 

    // $sql="select * from Product where category='$category'".$color==""?"":"and color='$color'".$length==""?"":"and length='$length'".$size==""?"":"and size='$size'".$pattern==""?"":"and pattern='$pattern'".$fabric==""?"":"and fabric='$fabric'".$detail==""?"":"and detail='$detail'";
    $sql="select * from Product where (category='$category' or category_detail='$category')".$s1.$s2.$s3.$s4.$s6;
    $stmt = $con->prepare($sql);
    $stmt->execute();
 
    if ($stmt->rowCount() == 0){
        echo $sql;
        echo "'";
        echo $category;
        echo "'은 찾을 수 없습니다. 상품을 입력을 해주세요.";
        
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
                'category_detail'=>$row["category_detail"],
                'length'=>$row["length"],
                'image'=>$row["image"],
                'price'=>$row["price"],
                'size'=>$row["size"],
                'color'=>$row["color"],
                'color_detail'=>$row["color_detail"],
               // 'fabric'=>$row["fabric"],
                'pattern'=>$row["pattern"],
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
            $json = json_encode(array("SearchedProduct"=>$data), JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
            echo $json;
        }
    }
}
else {
    echo "상품을 입력해주세요";
}

?>



<?php

$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

if (!$android){
?>

<html>
   <body>
   
      <form action="<?php $_PHP_SELF ?>" method="POST">
         category: <input type = "text" name = "category" />
         <input type = "submit" />
      </form>
   
   </body>
</html>
<?php
}

   
?>
