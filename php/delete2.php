<?php
	error_reporting(E_ALL); 
    ini_set('display_errors',1); 

    include('dbcon.php');


    $android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

    $uuid = $_POST['uuid'];
	$productURL = $_POST['productURL'];



	$con = mysqli_connect("localhost","abc","125400","사용자이름");
	$sql = "DELETE FROM wishList WHERE uuid = '$uuid' AND  'productURL = $productURL'";
	mysqli_query($con,$sql);

?>