<?php
 	header('Content-Type: text/html; charset=utf-8'); 
	error_reporting(E_ALL); 
    ini_set('display_errors',1); 

    include('dbcon.php');


    $android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

    $uid=$_POST['uid'];
	$wishProductName=$_POST['wishProductName'];
	$value=$_POST['value'];



	$con = mysqli_connect("localhost","kimcheon","kim2cheon1","SHOWOOMI");
	$sql="UPDATE WishProduct SET $wishProductName='$value' WHERE uid='$uid' and wishProductName='$wishProductName'";

	mysqli_query($con,"set session character_set_connection=utf8;");
	mysqli_query($con,"set session character_set_results=utf8;");

	mysqli_query($con,"set session character_set_client=utf8;");

	mysqli_query($con,$sql);
	
?>