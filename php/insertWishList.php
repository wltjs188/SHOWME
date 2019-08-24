<?php 

    error_reporting(E_ALL); 
    ini_set('display_errors',1); 

    include('dbcon.php');


    $android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");


    if( (($_SERVER['REQUEST_METHOD'] == 'POST') && isset($_POST['submit'])) || $android )
    {

        // 안드로이드 코드의 postParameters 변수에 적어준 이름을 가지고 값을 전달 받습니다.

        $uid=$_POST['uid'];
        $productId=$_POST['productId'];
    
      



        if(!isset($errMSG)) // 이름과 나라 모두 입력이 되었다면 
        {
            try{
                // SQL문을 실행하여 데이터를 MySQL 서버의 person 테이블에 저장합니다. 
                $stmt = $con->prepare('INSERT INTO WishList(uid,productId) VALUES(:uid, :productId)');
                $stmt->bindParam(':uid', $uid);
                $stmt->bindParam(':productId', $productId);
             
               if($stmt->execute())
                {
                    $successMSG = "관심상품 등록되었습니다.";
                }
                else
                {
                    $errMSG = "관심상품 등록 에러";
                }
                
              


            } catch(PDOException $e) {
                die("Database error: " . $e->getMessage()); 
            }
        }

    }

?>

