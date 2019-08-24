<?php
	error_reporting(E_ALL); 
    ini_set('display_errors',1); 

    include('dbcon.php');


    $android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

    $uid=$_POST['uid'];
	$infoName=$_POST['infoName'];
	$value=$_POST['value'];



	$con = mysqli_connect("localhost","kimcheon","kim2cheon1","SHOWOOMI");
	$sql="UPDATE person SET $infoName='$value' WHERE uid='$uid'";
	mysqli_query($con,$sql);

?>