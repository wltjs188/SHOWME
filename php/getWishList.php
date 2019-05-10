<?php 
    error_reporting(E_ALL); 
    ini_set('display_errors',1); 
    include('dbcon.php');
    $android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");
    if( (($_SERVER['REQUEST_METHOD'] == 'POST') && isset($_POST['submit'])) || $android )
    {
        // 안드로이드 코드의 postParameters 변수에 적어준 이름을 가지고 값을 전 
        $uuid=$_POST['uuid'];
        $productURL=$_POST['productURL'];
        $info=$_POST['info'];
        $image=$_POST['image'];
        
        if(!isset($errMSG)) // 이름과 나라 모두 입력이 되었다면 
        {
            
                 try{
                // SQL문을 실행하여 데이터를 MySQL 서버의 person 테이블에 저장합니다. 
                $stmt = $con->prepare('INSERT INTO wishList(uuid,productURL,info, image) VALUES(:uuid, :productURL, :info, :image)');
                $stmt->bindParam(':uuid', $uuid);
                $stmt->bindParam(':productURL', $productURL);
                $stmt->bindParam(':info', $info);
                $stmt->bindParam(':image', $image);
                
                if($stmt->execute())
                {
                    $successMSG = "관심상품 등록되었습니다.";
                }
                else
                {
                    $errMSG = "사용자 추가 에러";
                }
            } catch(PDOException $e) {
                die("Database error: " . $e->getMessage()); 
            }
            } catch(PDOException $e) {
                die("Database error: " . $e->getMessage()); 
            }
        }
    }
?>
