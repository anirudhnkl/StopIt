<?php
session_start();
 
$dbhost = "45.79.128.82"; // this will ususally be 'localhost', but can sometimes differ
$dbname = "Users"; // the name of the database that you are going to use for this project
$dbuser = "abhinav"; // the username that you created, or were given, to access your database
$dbpass = "Stop_It"; // the password that you created, or were given, to access your database
 
mysql_connect($dbhost, $dbuser, $dbpass) or die("MySQL Error: " . mysql_error());
mysql_select_db($dbname) or die("MySQL Error: " . mysql_error());
?>





































/*<?php

session_start(); // Starting Session

// if empty $_POST['submit'] show login form else

if (empty($_POST['submit'])) {
?>
<section class="loginform cf">
  <form name="login" action="index.html" method="get" accept-charset="utf-8">
      <ul>

          <li style = "list-style: none"><label for="usermail">Email</label>
          <input type="email" id = "input" name="usermail" placeholder="yourname@email.com" required></li>

          <li style = "list-style: none"><label for="password">Password</label>
          <input type="password" id = "input" name="password" placeholder="password" required></li>

          <li style = "list-style: none">
          <input type="submit" value="Login"></li>

          
      </ul>
  </form>
</section>
<?



} else {
$error=''; // Variable To Store Error Message
if (isset($_POST['submit'])) {
if (empty($_POST['username']) || empty($_POST['password'])) {
$error = "Username or Password is invalid";
}
else
{
// Define $username and $password
$username=$_POST['username'];
$password=$_POST['password'];
// Establishing Connection with Server by passing server_name, user_id and password as a parameter
$connection = mysql_connect("45.79.128.82", "abhinav", "Stop_It");
// To protect MySQL injection for Security purpose
$username = stripslashes($username);
$password = stripslashes($password);
$username = mysql_real_escape_string($username);
$password = mysql_real_escape_string($password);
// Selecting Database
$db = mysql_select_db("Users", $connection);
// SQL query to fetch information of registerd users and finds user match.
$query = mysql_query("select * from login where password='$password' AND username='$username'", $connection);
$rows = mysql_num_rows($query);
if ($rows == 1) {
$_SESSION['abhinav']=$username; // Initializing Session
header("location: profile.php"); // Redirecting To Other Page
} else {
$error = "Username or Password is invalid";
}
mysql_close($connection); // Closing Connection
}
}
}
?>
*/