<?php
	error_reporting(E_ALL); 
    ini_set('display_errors',1); 

    include('dbcon.php');


    $android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

    $uid = $_POST['uid'];
	$productId = $_POST['productId'];
	$optionNum = $_POST['optionNum'];


	$con = mysqli_connect("localhost","kimcheon","kim2cheon1","SHOWOOMI");
	$sql = "DELETE FROM WishList WHERE uid = '$uid' AND  productId = '$productId' AND  optionNum = '$optionNum'" ;
	mysqli_query($con,$sql);

?>