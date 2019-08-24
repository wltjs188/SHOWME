<?php 
    error_reporting(E_ALL); 
    ini_set('display_errors',1); 
    include('dbcon.php');
    $android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");
    if( (($_SERVER['REQUEST_METHOD'] == 'POST') && isset($_POST['submit'])) || $android )
    {
        $uid=$_POST['uuid'];
        $productURL=$_POST['productURL'];
        $info=$_POST['info'];
        $image=$_POST['image'];
        
        if(!isset($errMSG))  
        {
            
                 try{
               
                $stmt = $con->prepare('INSERT INTO wishList(uuid,productURL,info, image) VALUES(:uuid, :productURL, :info, :image)');
                $stmt->bindParam(':uuid', $uuid);
                $stmt->bindParam(':productURL', $productURL);
                $stmt->bindParam(':info', $info);
                $stmt->bindParam(':image', $image);
                
                if($stmt->execute())
                {
                    $successMSG = "successMSG";
                }
                else
                {
                    $errMSG = "errMSG";
                }
            } catch(PDOException $e) {
                die("Database error: " . $e->getMessage()); 
            }
            
        }
    }
?>
