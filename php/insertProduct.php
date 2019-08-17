<?php 
    error_reporting(E_ALL); 
    ini_set('display_errors',1); 
    include('dbcon.php');
    
    $android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

    if( (($_SERVER['REQUEST_METHOD'] == 'POST') && isset($_POST['submit'])) || $android )
    {
        // 안드로이드 코드의 postParameters 변수에 적어준 이름을 가지고 값을 전달 받습니다.
        $p_id=$_POST['p_id'];
        $p_name=$_POST['p_name'];
        $p_image=$_POST['p_image'];
        $p_price=$_POST['p_price'];
        $p_size=$_POST['p_size'];
        $p_color=$_POST['p_color'];
        $p_fabric=$_POST['p_fabric'];
        $p_pattern=$_POST['p_pattern'];
        $p_detail=$_POST['p_detail'];
        
        if(!isset($errMSG)) // 이름과 나라 모두 입력이 되었다면 
        {
            try{
                // SQL문을 실행하여 데이터를 MySQL 서버의 person 테이블에 저장합니다. 
                $stmt = $con->prepare('INSERT INTO Product(p_id, p_name, p_image, p_price, p_size, p_color, p_fabric, p_pattern, p_detail) VALUES(:p_id, :p_name, :p_image, :p_price, :p_size, :p_color, :p_fabric, :p_pattern, :p_detail )');
                $stmt->bindParam(':p_id', $p_id);
                $stmt->bindParam(':p_name', $p_name);
                $stmt->bindParam(':p_image', $p_image);
                $stmt->bindParam(':p_price', $p_price);
                $stmt->bindParam(':p_size', $p_size);
                $stmt->bindParam(':p_color', $p_color);
                $stmt->bindParam(':p_fabric', $p_fabric);
                $stmt->bindParam(':p_pattern', $p_pattern);
                $stmt->bindParam(':p_detail', $p_detail);
                
                $successMSG = "새로운 상품을 추가했습니다.";
            } catch(PDOException $e) {
                die("Database error: " . $e->getMessage()); 
            }
        }
    }
?>