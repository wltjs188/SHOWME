<?php 
    error_reporting(E_ALL); 
    ini_set('display_errors',1); 
    include('dbcon.php');
    $android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");
    if( (($_SERVER['REQUEST_METHOD'] == 'POST') && isset($_POST['submit'])) || $android )
    {
        // ¾Èµå·ÎÀÌµå ÄÚµåÀÇ postParameters º¯¼ö¿¡ Àû¾îÁØ ÀÌ¸§À» °¡Áö°í °ªÀ» Àü 
        $uuid=$_POST['uuid'];
        $productURL=$_POST['productURL'];
        $info=$_POST['info'];
        $image=$_POST['image'];
        
        if(!isset($errMSG)) // ÀÌ¸§°ú ³ª¶ó ¸ðµÎ ÀÔ·ÂÀÌ µÇ¾ú´Ù¸é 
        {
            
                 try{
                // SQL¹®À» ½ÇÇàÇÏ¿© µ¥ÀÌÅÍ¸¦ MySQL ¼­¹öÀÇ person Å×ÀÌºí¿¡ ÀúÀåÇÕ´Ï´Ù. 
                $stmt = $con->prepare('INSERT INTO wishList(uuid,productURL,info, image) VALUES(:uuid, :productURL, :info, :image)');
                $stmt->bindParam(':uuid', $uuid);
                $stmt->bindParam(':productURL', $productURL);
                $stmt->bindParam(':info', $info);
                $stmt->bindParam(':image', $image);
                
                if($stmt->execute())
                {
                    $successMSG = "성공";
                }
                else
                {
                    $errMSG = "실패";
                }
            } catch(PDOException $e) {
                die("Database error: " . $e->getMessage()); 
            }
            
        }
    }
?>
