<?php
 	header('Content-Type: text/html; charset=utf-8'); 
	error_reporting(E_ALL); 
    ini_set('display_errors',1); 

    include('dbcon.php');


    $android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

    $uid=$_POST['uid'];
	$infoName=$_POST['infoName'];
	$value=$_POST['value'];



	$con = mysqli_connect("localhost","kimcheon","kim2cheon1","SHOWOOMI");
	$sql="UPDATE User SET $infoName='$value' WHERE uid='$uid'";

	mysqli_query("set session character_set_connection=utf8;");
	mysqli_query("set session character_set_results=utf8;");

	mysqli_query("set session character_set_client=utf8;");

	mysqli_query($con,$sql);
	
?>