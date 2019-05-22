<?php 

	error_reporting(E_ALL); 
    ini_set('display_errors',1); 

    include('dbcon.php');


    $android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");
   // POST 방식일 경우에만 코드가 실행됩니다.
 //  if(isset($_POST['id'])){
	//POST로 보낸 id 값을 받아서 변수에 입력합니다.
	$uuid = $_POST['uuid'];
	$productURL = $_POST['productURL'];
	
	//DB 접속 스크립트를 불러옵니다.
	require_once('db_config.php');
	
	//특정 종업원의 정보를 삭제하는 쿼리문을 작성합니다.
	$sql = "DELETE FROM wishList WHERE uuid='$uuid' AND  productURL='$productURL'";
	
	//쿼리문을 실행합니다.
	if(mysqli_query($con,$sql)){
                // 삭제 성공 시 아래 내용을 출력합니다. 
		echo '종업원 정보가 성공적으로 삭제되었습니다.';
	}else{
                // 삭제 실패 시 아래 내용을 출력합니다. 
		echo '종업원 정보를 삭제할 수 없습니다.';
	}
	
	//접속을 종료합니다.
	mysqli_close($con);
   // }else{
   //      // POST 방식이 아니면 아래 내용을 출력합니다.
   //      echo 'Post Request 가 아닙니다.';
   // }
?>