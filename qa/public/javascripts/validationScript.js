var validEmail = false;
var validPW = false;
var validUser = false;

$(function(){
		
$('input#register').hide();

$('input#newusername').keyup(function(){

   var username = $('input#newusername').val();

      $.getJSON(userExists({username: username}), function(data) {

   if(username!=0) {
      if(!data) {
      // do change UI -> address ok
      	$('input#newusername').removeClass("redBorder normalBorder").addClass("correctBorder");
      	validUser = true;

      }
      else {
      // do change UI -> address is not valid
	$('input#newusername').removeClass("correctBorder normalBorder").addClass("redBorder");
     	validUser = false;

      }
  }
   else{
       // default case, input is empty
	$('input#newusername').removeClass("redBorder correctBorder").addClass("normalBorder");
       	validUser = false;
   }

  });  


});


$('input#email').keyup(function(){

   var email = $('input#email').val();
   if(email!=0) {
      if(isValidEmailAddress(email)) {
      // do change UI -> address ok
      	$('input#email').removeClass("redBorder normalBorder").addClass("correctBorder")
      	validEmail = true;

      }
      else {
      // do change UI -> address is not valid
	$('input#email').removeClass("correctBorder normalBorder").addClass("redBorder")
     	validEmail = false;

      }
  }
   else{
       // default case, input is empty
	$('input#email').removeClass("redBorder correctBorder").addClass("normalBorder")
       	validEmail = false;
   }

  });

$('input#newPassword2').keyup(function(){

   var pw = $('input#newPassword').val();
   var pw2 = $('input#newPassword2').val();
   if(pw2!=0) {
      if(isValidPW(pw, pw2)) {
      // do change UI -> address ok
      	$('input#newPassword').removeClass("redBorder normalBorder").addClass("correctBorder");
      	$('input#newPassword2').removeClass("redBorder normalBorder").addClass("correctBorder");
      	validPW = true;

      }
      else {
      // do change UI -> address is not valid
        $('input#newPassword').removeClass("correctBorder normalBorder").addClass("redBorder");
      	$('input#newPassword2').removeClass("correctBorder normalBorder").addClass("redBorder");
     	validPW = false;

      }
  }
   else{
       // default case, input is empty
        $('input#newPassword').removeClass("redBorder correctBorder").addClass("normalBorder");
      	$('input#newPassword2').removeClass("redBorder correctBorder").addClass("normalBorder");
       	validPW = false;
   }

  });


$('input').keyup(function(){
  

      var username = $('input#newusername').val();
      var validUser = null;

      $.getJSON(userExists({username: username}), function(data) {

      if(username==0) { validUser = false; }
      else {	validUser = !data; }
      
 
      if(validEmail && validPW && validUser ){
      // do change UI -> address ok
      	$('input#register').show();

      }
      else {
      // do change UI -> address is not valid
     	$('input#register').hide();

      }

  });

});

});


function isValidEmailAddress(emailAddress) {
	var pattern = new RegExp(/^(("[\w-\s]+")|([\w-]+(?:\.[\w-]+)*)|("[\w-\s]+")([\w-]+(?:\.[\w-]+)*))(@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$)|(@\[?((25[0-5]\.|2[0-4][0-9]\.|1[0-9]{2}\.|[0-9]{1,2}\.))((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[0-9]{1,2})\.){2}(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[0-9]{1,2})\]?$)/i);
	return pattern.test(emailAddress);
}

function isValidPW(password, password2){
	var pattern = new RegExp(/^[A-Za-z0-9!@#$%^&*()_]{6,20}$/);
	return pattern.test(password) && password == password2;
}
