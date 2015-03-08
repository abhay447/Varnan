<?php
	$servername="localhost";
	$username="root";
	$password="sunita";
	$database="varnan";
	$conn = new mysqli($servername, $username, $password,$database);
	if ($conn->connect_error)
	{
		echo "heheh";
	    die("Connection failed: " . $conn->connect_error);
	}
	$sql = "SELECT * FROM REPORTS";
	$result = $conn->query($sql);
?>	
<html>
	<head>
		<style type="text/css" media="screen">
		#divID th {
		font-family: Verdana, sans-serif;
		font-size: 12px;
		background: url('bg.jpeg');
		}
		</style>
	</head>
	<body>
	<div id="divID">
	<table border="1">
	<tr><th>ID</th>
	<th>DESCRIPTION</th>
	<th>SUBMITTEDBY</th>
	<th>STATUS</th>
	<th>REGISTEREDAT</th>
	<th>LOCATION</th>
	<th>ALLOTEDTO</th></tr>
<?php
	if ($result->num_rows > 0) {
    	while($row = $result->fetch_assoc()) 
		{
    	    echo "<tr><td>" .$row["ID"]."</td>";
			echo "<td>".$row["DESCRIPTION"]."</td>";
			echo "<td>".$row["USERID"]."</td>";
			echo "<td>".$row["STATUS"]."</td>";
			echo "<td>".$row["DATETIMEID"]."</td>";
			echo "<td>".$row["GPS"]."</td>";
			echo "<td>".$row["FORWARDEDTO"]."</td></tr>";
			
    	}
	} else 
	{
    	echo "0 results";
	}
	echo "</table>";
	$conn->close();
?>
	</div>
	</body>
</html>
