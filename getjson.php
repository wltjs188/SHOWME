<?php 

    error_reporting(E_ALL); 
    ini_set('display_errors',1); 

    include('dbcon.php');
        

    $stmt = $con->prepare('select * from person WHERE uuid=\'hi\'');
    $stmt->execute();

    if ($stmt->rowCount() > 0)
    {
        $data = array(); 

        while($row=$stmt->fetch(PDO::FETCH_ASSOC))
        {
            extract($row);
    
            array_push($data, 
                array('id'=>$uuid,
                'name'=>$name,
                'gender'=>$gender
                'height'=>$height
                'top'=>$top
                'bottom'=>$bottom
                'foot'=>$foot
            ));
        }

        header('Content-Type: application/json; charset=utf8');
        $json = json_encode(array("person"=>$data), JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
        echo $json;
    }

?>