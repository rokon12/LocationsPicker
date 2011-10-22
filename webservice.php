<?php
		
	header("Access-Control-Allow-Origin: *");
	/* connect to the db */
	$link = mysql_connect('localhost','kermithefrog','password') or die('Cannot connect to the DB');
	mysql_select_db('testing_app',$link) or die('Cannot select the DB');
	mysql_set_charset("UTF8", $link);
	
	

	/* get the passed variables */
	$type = $_REQUEST['type'];
	
	
	switch($type) {
		
		case "set" : setPoints(); 
		break;
		
		case "get" : getPoints();
		break;
		
		case "del" : deletePoints();
		break;
	}
		
	function setPoints() {

		$q = mysql_query("INSERT INTO test (lat, lng) VALUES 
							('".$_REQUEST['lat']."', '".$_REQUEST['lng']."')");			
	}	
	
	function getPoints() {

		$q = mysql_query("SELECT lat, lng FROM test");
	
		while($e = mysql_fetch_assoc($q)) 

        	$output[] = $e;

		output($output);
	}
	
	function output($input) {
		print(my_json_encode($input));
	}
	
	function my_json_encode($arr)
	{
        //convmap since 0x80 char codes so it takes all multibyte codes (above ASCII 127). So such characters are being "hidden" from normal json_encoding
        array_walk_recursive($arr, function (&$item, $key) { 
			if (is_string($item)) 
				$item = mb_encode_numericentity($item, array (0x80, 0xffff, 0, 0xffff), 'UTF-8'); 
		});
		return mb_decode_numericentity(json_encode($arr), array (0x80, 0xffff, 0, 0xffff), 'UTF-8');
	}
	
	function deletePoints() {

		$q = mysql_query("DELETE FROM test");			
	}
	
	/* disconnect from the db */
	@mysql_close($link);

?>