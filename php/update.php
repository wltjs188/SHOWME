<?php
	error_reporting(E_ALL); 
    ini_set('display_errors',1); 

    include('dbcon.php');


    $android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

    $uuid=$_POST['uuid'];
	$infoName=$_POST['infoName'];
	$value=$_POST['value'];



	$con = mysqli_connect("localhost","abc","125400","사용자이름");
	$sql="UPDATE person SET $infoName='$value' WHERE uuid='$uuid'";
	mysqli_query($con,$sql);

?>