function loginDone(){
	
	$.ajax({
		url: '/npv/login/'+document.getElementById("loginName").value + '/' + document.getElementById("pwd").value,
      	type: 'GET',
      	contentType: "charset=utf-8",
      	dataType: 'text', //you may use jsonp for cross origin request
      	crossDomain: true,
	  	error: function(res) {
			alert("Invalid Credentials");
       	},
		success: function(res) {
			var json = $.parseJSON(res);
			console.log(json);
			$(json).each(function(i, val) {
				console.log(val.landing_page);
				window.location.href = val.landing_page;
				sessionStorage.setItem("logon",$("#loginName").val());
         	});
			
			
		}
	});
}
function handle(e){
	  var key=e.keyCode || e.which;
	  if (key==13){
	     loginDone();
	  }
	}