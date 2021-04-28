function loginDone(){
	//alert($("#loginName").val()+$("#pwd").val());
	if(($("#loginName").val() == "ppsadmin") && ($("#pwd").val() =="pps")){
		window.location.href ="/ppsVidyoAnalytics.html"; 
		sessionStorage.setItem("logon",$("#loginName").val());
	}else{
		alert("Invalid credentials Plesae try again!!");
	}
}
function handle(e){
	  var key=e.keyCode || e.which;
	  if (key==13){
	     loginDone();
	  }
	}