<?php 

    error_reporting(E_ALL); 
    ini_set('display_errors',1); 

    include('dbcon.php');


    $android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");


    if( (($_SERVER['REQUEST_METHOD'] == 'POST') && isset($_POST['submit'])) || $android )
    {

        // 안드로이드 코드의 postParameters 변수에 적어준 이름을 가지고 값을 전달 받습니다.

        $uuid=$_POST['uuid'];
        $name=$_POST['name'];
        $gender=$_POST['gender'];
        $height=$_POST['height'];
        $top=$_POST['top'];
        $bottom=$_POST['bottom'];
        $foot=$_POST['foot'];



        if(!isset($errMSG)) // 이름과 나라 모두 입력이 되었다면 
        {
            try{
                // SQL문을 실행하여 데이터를 MySQL 서버의 person 테이블에 저장합니다. 
                $stmt = $con->prepare('INSERT INTO person(uuid,name, gender, height, top, bottom, foot) VALUES(:uuid, :name, :gender, :height, :top, :bottom, :foot )');
                $stmt->bindParam(':uuid', $uuid);
                $stmt->bindParam(':name', $name);
                $stmt->bindParam(':gender', $gender);
                $stmt->bindParam(':height', $height);
                $stmt->bindParam(':top', $top);
                $stmt->bindParam(':bottom', $bottom);
                $stmt->bindParam(':foot', $foot);
                
                $successMSG = "새로운 사용자를 추가했습니다.";


            } catch(PDOException $e) {
                die("Database error: " . $e->getMessage()); 
            }
        }

    }

?>

