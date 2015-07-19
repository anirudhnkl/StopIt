if (!empty($_GET['getAll'])) {
  $sth = mysqli_query("SELECT * FROM data"); // What do you want to get?
  $rows = array();
  while($r = mysqli_fetch_assoc($sth)) { //Grab all of the data
      $rows[] = $r;
  }
  print json_encode($rows); //This makes the data valid JSON
} 
else {
 echo "{ Error: Nothing provided }";
}

