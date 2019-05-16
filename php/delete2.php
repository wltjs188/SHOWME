<?php
	$servername = "localhost";
	$username = "abc";
	$password = "125400";
	$dbname = "사용자이름";

	// Create connection
	$conn = new mysqli($servername, $username, $password, $dbname);
	// Check connection
	if ($conn->connect_error) {
	    die("Connection failed: " . $conn->connect_error);
	} 
	$uuid = $_POST['uuid'];
	$productURL = $_POST['productURL'];
	// sql to delete a record
	$sql = "DELETE FROM wishList WHERE uuid=$uuid AND  productURL=$productURL";

	if ($conn->query($sql) === TRUE) {
	    echo "Record deleted successfully";
	} else {
	    echo "Error deleting record: " . $conn->error;
	}

	$conn->close();
?>