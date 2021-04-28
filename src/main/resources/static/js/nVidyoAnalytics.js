/*$(document).ajaxStart(function(){
  // Show image container
  $(".loader").show();
});
$(document).ajaxComplete(function(){
  // Hide image container
  $(".loader").hide();
});*/


$( document ).ready(function() {
   if (sessionStorage.getItem("logon") == null){
        window.location.href="/nVidyoLogin.html";
        console.log("Welcome " + sessionStorage.getItem("logon"));
	} 
	fillCampaign();
	getClientName();
	
});
function doLogout(){
	sessionStorage.removeItem("logon",$("#loginName").val());
	window.location.href ="/nVidyoLogin.html"; 
	
}
$(function () {
	$('#startdatepicker').datepicker({
		dateFormat: "yyyy-mm-dd",
        minDate: 0,
		onSelect: function () {
            var sdt = $('#startdatepicker');
            var startDate = $(this).datepicker('getDate');
            console.log(startDate);
            //alert(startDate);
		}
	});
	$('#enddatepicker').datepicker({
		dateFormat: "yyyy-mm-dd",
        minDate: 0,
		onSelect: function () {
            var edt = $('#enddatepicker');
            var endDate = $(this).datepicker('getDate');
            console.log(endDate);
		}
	});
});


$('#viewChart').on( 'click', function () {
	if($("#startdatepicker").datepicker("getDate") != "" && $("#enddatepicker").datepicker("getDate")!="" && $("#campaignName").val() != ""){
		//$("#loader").removeClass('hide');
		getViewChartData_date();
		getViewChartData_city();
		getViewChartData_country();
		getViewChartData_dateHour();
		getViewChartData_browser();
		getViewChartData_os();
		
		
		showEventTracking();
		document.getElementById('div_date').style.display='block';
		document.getElementById('div_city').style.display='block';
		document.getElementById('div_country').style.display='block';
		document.getElementById('div_dateHour').style.display='block';
		document.getElementById('div_browser').style.display='block';
		document.getElementById('div_os').style.display='block';
		showVideoCount();
	}else{
		alert("Oops!! Please select Dates and Campaign");
	}	

} );

function getTime(timeType){
	var today = new Date();
	var time = today.getHours() + ":" + today.getMinutes() + ":" + today.getSeconds();
	return (timeType + " "+time);
}

function getViewChartData_date(){
	var sDate = $("#startdatepicker").datepicker("getDate");
	var eDate = $("#enddatepicker").datepicker("getDate");
	var campName=$("#campaignName").val();
	//var categoryName=$("#categoryName").val();
	startDate=$.datepicker.formatDate("yy-mm-dd", sDate);
	endDate=$.datepicker.formatDate("yy-mm-dd", eDate);
	client_name= sessionStorage.getItem("clientName");
	console.log("loading started");
	$("#loader").removeClass('hide');
	
	console.log(getTime("start"));
	$.ajax({
		url: '/npv/client/chart/'+ client_name + '/'+ campName+'/eventDate/'+ startDate+'/'+endDate,
		
		type: 'GET',
        contentType: "application/json",
        async: true,
        error: function() {
            $('#err').html('<p>An error has occurred</p>');
        },
        dataType: 'json', //you may use jsonp for cross origin request
        crossDomain: true,
      	success: function(res) {
      		console.log(res);
      		drawViewChart_date(res);
      		   
		}
	});
	 
	console.log(getTime("end"));
}

function drawViewChart_date(res){
	
	var items = [];
	$(res).each(function(i, val) {
		
		items.push([val.category, Number(val.total)]);
	});
	items.unshift(['category', 'total']);
	//console.log(items);
	google.charts.load('current', {packages: ['corechart', 'line']});
	google.charts.setOnLoadCallback(drawChart);
	
	function drawChart() {
		var data = google.visualization.arrayToDataTable(items);
	    var view = new google.visualization.DataView(data);
	      
	        var options = {
	      	backgroundColor: 'transparent',
	        //title: 'Financial Status Prediction 2019',
	        width: 400,
            height: 350,
            colors: ['blue'],
            legend: { position: 'top' },
	        chartArea: {width: '60%'},
	        bars: 'vertical',
	        hAxis: {
	            title: 'Date',
	            titleTextStyle: {
	                color: "blue",
	                fontName: "sans-serif",
	                fontSize: 15,
	                bold: true,
	                italic: true
	            }
	        },
	        /*hAxis: {
	          title: 'Date',
	          minValue: 0
	        },*/
	        vAxis: {
	            title: 'Views',
	            titleTextStyle: {
	                color: "blue",
	                fontName: "sans-serif",
	                fontSize: 15,
	                bold: true,
	                italic: true
	            }
	        }
	      };

	      var chart = new google.visualization.LineChart(document.getElementById('chartContainer_date'));
	      chart.draw(view, options);
	      console.log("loading finished");
	      $("#loader").addClass('hide');
	      $("#div_date").removeClass('hide');
	    }
}

function getViewChartData_city(){
	var sDate = $("#startdatepicker").datepicker("getDate");
	var eDate = $("#enddatepicker").datepicker("getDate");
	var campName=$("#campaignName").val();
	//var categoryName=$("#categoryName").val();
	startDate=$.datepicker.formatDate("yy-mm-dd", sDate);
	endDate=$.datepicker.formatDate("yy-mm-dd", eDate);
	client_name= sessionStorage.getItem("clientName");
	console.log(getTime("start"));
	
	$.ajax({
		url: '/npv/client/chart/'+ client_name+'/'+ campName+'/eventCity/'+ startDate+'/'+endDate,
       	type: 'GET',
        contentType: "application/json",
        async: true,
        error: function() {
            $('#err').html('<p>An error has occurred</p>');
        },
        dataType: 'json', //you may use jsonp for cross origin request
        crossDomain: true,
      	success: function(res) {
      		console.log(res);
      		drawViewChart_city(res);
      		   
		}
	});
	console.log(getTime("end"));
}

function drawViewChart_city(res){
	var items = [];
	$(res).each(function(i, val) {
		
		items.push([val.category, Number(val.total)]);
	});
	items.unshift(['category', 'total']);
	//console.log(items);
	google.charts.load('current', {packages: ['corechart', 'line']});
	google.charts.setOnLoadCallback(drawChart);
	
	function drawChart() {
		var data = google.visualization.arrayToDataTable(items);
	    var view = new google.visualization.DataView(data);
	      
	        var options = {
	      	backgroundColor: 'transparent',
	        //title: 'Financial Status Prediction 2019',
	        width: 400,
            height: 350,
            colors: ['gold','red','green','blue'],
            legend: { position: 'top' },
	        chartArea: {width: '60%'},
	        hAxis: {
	            title: 'City',
	            titleTextStyle: {
	                color: "orange",
	                fontName: "sans-serif",
	                fontSize: 15,
	                bold: true,
	                italic: true
	            }
	        },
	        vAxis: {
	            title: 'Views',
	            titleTextStyle: {
	                color: "orange",
	                fontName: "sans-serif",
	                fontSize: 15,
	                bold: true,
	                italic: true
	            }
	        }
	      };

	      var chart = new google.visualization.LineChart(document.getElementById('chartContainer_city'));
	      chart.draw(view, options);
	      $("#div_city").removeClass('hide');
	    }
}
function getViewChartData_country(){
	var sDate = $("#startdatepicker").datepicker("getDate");
	var eDate = $("#enddatepicker").datepicker("getDate");
	var campName=$("#campaignName").val();
	//var categoryName=$("#categoryName").val();
	startDate=$.datepicker.formatDate("yy-mm-dd", sDate);
	endDate=$.datepicker.formatDate("yy-mm-dd", eDate);
	client_name= sessionStorage.getItem("clientName");
	console.log(getTime("start"));
	
	$.ajax({
		url: '/npv/client/chart/'+ client_name+'/'+ campName+'/eventCountry/'+ startDate+'/'+endDate,
       	type: 'GET',
        contentType: "application/json",
        async: true,
        error: function() {
            $('#err').html('<p>An error has occurred</p>');
        },
        dataType: 'json', //you may use jsonp for cross origin request
        crossDomain: true,
      	success: function(res) {
      		console.log(res);
      		drawViewChart_country(res);
      		   
		}
	});
	console.log(getTime("end"));
}

function drawViewChart_country(res){
	var items = [];
	$(res).each(function(i, val) {
		
		items.push([val.category, Number(val.total)]);
	});
	items.unshift(['category', 'total']);
	//console.log(items);
	google.charts.load('current', {packages: ['corechart', 'line']});
	google.charts.setOnLoadCallback(drawChart);
	
	function drawChart() {
		var data = google.visualization.arrayToDataTable(items);
	    var view = new google.visualization.DataView(data);
	      
	        var options = {
	      	backgroundColor: 'transparent',
	        //title: 'Financial Status Prediction 2019',
	        width: 400,
            height: 350,
            colors: ['red'],
            legend: { position: 'top' },
	        chartArea: {width: '60%'},
	        hAxis: {
	            title: 'Country',
	            titleTextStyle: {
	                color: "red",
	                fontName: "sans-serif",
	                fontSize: 15,
	                bold: true,
	                italic: true
	            }
	        },
	        vAxis: {
	            title: 'Views',
	            titleTextStyle: {
	                color: "red",
	                fontName: "sans-serif",
	                fontSize: 15,
	                bold: true,
	                italic: true
	            }
	        }
	      };

	      var chart = new google.visualization.LineChart(document.getElementById('chartContainer_country'));
	      chart.draw(view, options);
	      $("#div_country").removeClass('hide');
	    }
}
function getViewChartData_dateHour(){
	var sDate = $("#startdatepicker").datepicker("getDate");
	var eDate = $("#enddatepicker").datepicker("getDate");
	var campName=$("#campaignName").val();
	//var categoryName=$("#categoryName").val();
	startDate=$.datepicker.formatDate("yy-mm-dd", sDate);
	endDate=$.datepicker.formatDate("yy-mm-dd", eDate);
	client_name= sessionStorage.getItem("clientName");
	console.log(getTime("start"));
	
	$.ajax({
		url: '/npv/client/chart/'+ client_name+'/'+ campName+'/hour(eventDateTime)/'+ startDate+'/'+endDate,
       	type: 'GET',
        contentType: "application/json",
        async: true,
        error: function() {
            $('#err').html('<p>An error has occurred</p>');
        },
        dataType: 'json', //you may use jsonp for cross origin request
        crossDomain: true,
      	success: function(res) {
      		console.log(res);
      		drawViewChart_dateHour(res);
      		   
		}
	});
	console.log(getTime("end"));
}

function drawViewChart_dateHour(res){
	var items = [];
	$(res).each(function(i, val) {
		
		items.push([val.category, Number(val.total)]);
	});
	items.unshift(['category', 'total']);
	//console.log(items);
	google.charts.load('current', {packages: ['corechart', 'line']});
	google.charts.setOnLoadCallback(drawChart);
	
	function drawChart() {
		var data = google.visualization.arrayToDataTable(items);
	    var view = new google.visualization.DataView(data);
	    
	    var options = {
		            backgroundColor: 'transparent',
		            title: 'Date Hour',
		            pieHole: 0.4,
		            is3D:true,
		            width: 400,
		            height: 350,
		            titleTextStyle:{
		            	color: "purple",
		                fontName: "sans-serif",
		                fontSize: 15,
		                bold: true,
		                italic: true
		            }
		        };

	      var chart = new google.visualization.PieChart(document.getElementById('chartContainer_dateHour'));
	      chart.draw(view, options);
	      $("#div_dateHour").removeClass('hide');
	    }
}
function getViewChartData_browser(){
	var sDate = $("#startdatepicker").datepicker("getDate");
	var eDate = $("#enddatepicker").datepicker("getDate");
	var campName=$("#campaignName").val();
	//var categoryName=$("#categoryName").val();
	startDate=$.datepicker.formatDate("yy-mm-dd", sDate);
	endDate=$.datepicker.formatDate("yy-mm-dd", eDate);
	client_name= sessionStorage.getItem("clientName");
	console.log(getTime("start"));
	
	$.ajax({
		url: '/npv/client/chart/'+ client_name+'/'+ campName+'/eventBrowser/'+ startDate+'/'+endDate,
       	type: 'GET',
        contentType: "application/json",
        async: true,
        error: function() {
            $('#err').html('<p>An error has occurred</p>');
        },
        dataType: 'json', //you may use jsonp for cross origin request
        crossDomain: true,
      	success: function(res) {
      		console.log(res);
      		drawViewChart_browser(res);
      		   
		}
	});
	console.log(getTime("end"));
}

function drawViewChart_browser(res){
	var items = [];
	$(res).each(function(i, val) {
		
		items.push([val.category, Number(val.total)]);
	});
	items.unshift(['category', 'total']);
	//console.log(items);
	google.charts.load('current', {packages: ['corechart', 'line']});
	google.charts.setOnLoadCallback(drawChart);
	
	function drawChart() {
		var data = google.visualization.arrayToDataTable(items);
	    var view = new google.visualization.DataView(data);
	      
	        var options = {
	      	backgroundColor: 'transparent',
	        //title: 'Financial Status Prediction 2019',
	        width: 400,
            height: 350,
            colors: ['blue'],
            legend: { position: 'top' },
	        chartArea: {width: '60%'},
	        hAxis: {
	            title: 'Browser',
	            titleTextStyle: {
	                color: "blue",
	                fontName: "sans-serif",
	                fontSize: 15,
	                bold: true,
	                italic: true
	            }
	        },
	        vAxis: {
	            title: 'Views',
	            titleTextStyle: {
	                color: "blue",
	                fontName: "sans-serif",
	                fontSize: 15,
	                bold: true,
	                italic: true
	            }
	        }
	      };

	      var chart = new google.visualization.ColumnChart(document.getElementById('chartContainer_browser'));
	      chart.draw(view, options);
	      $("#div_browser").removeClass('hide');
	    }
}
function getViewChartData_os(){
	var sDate = $("#startdatepicker").datepicker("getDate");
	var eDate = $("#enddatepicker").datepicker("getDate");
	var campName=$("#campaignName").val();
	//var categoryName=$("#categoryName").val();
	startDate=$.datepicker.formatDate("yy-mm-dd", sDate);
	endDate=$.datepicker.formatDate("yy-mm-dd", eDate);
	client_name= sessionStorage.getItem("clientName");
	console.log(getTime("start"));
	
	$.ajax({
		url: '/npv/client/chart/'+ client_name+'/'+ campName+'/eventOperatingSystem/'+ startDate+'/'+endDate,
       	type: 'GET',
        contentType: "application/json",
        async: true,
        error: function() {
            $('#err').html('<p>An error has occurred</p>');
        },
        dataType: 'json', //you may use jsonp for cross origin request
        crossDomain: true,
      	success: function(res) {
      		console.log(res);
      		drawViewChart_os(res);
      		   
		}
	});
	console.log(getTime("end"));
}

function drawViewChart_os(res){
	var items = [];
	$(res).each(function(i, val) {
		
		items.push([val.category, Number(val.total)]);
	});
	items.unshift(['category', 'total']);
	//console.log(items);
	google.charts.load('current', {packages: ['corechart', 'line']});
	google.charts.setOnLoadCallback(drawChart);
	
	function drawChart() {
		var data = google.visualization.arrayToDataTable(items);
	    var view = new google.visualization.DataView(data);
	      
	        var options = {
	      	backgroundColor: 'transparent',
	        //title: 'Financial Status Prediction 2019',
	        width: 400,
            height: 350,
            colors: ['green'],
            legend: { position: 'top' },
	        chartArea: {width: '60%'},
	        hAxis: {
	            title: 'Operating System',
	            titleTextStyle: {
	                color: "green",
	                fontName: "sans-serif",
	                fontSize: 15,
	                bold: true,
	                italic: true
	            }
	        },
	        vAxis: {
	            title: 'Views',
	            titleTextStyle: {
	                color: "green",
	                fontName: "sans-serif",
	                fontSize: 15,
	                bold: true,
	                italic: true
	            }
	        }
	      };

	      var chart = new google.visualization.ColumnChart(document.getElementById('chartContainer_os'));
	      chart.draw(view, options);
	      $("#div_os").removeClass('hide');
	    }
}

/*function getEventChartData(){
	var sDate = $("#startdatepicker").datepicker("getDate");
	var eDate = $("#enddatepicker").datepicker("getDate");
	var campName=$("#campaignName").val();
	
	$.ajax({
		url: '/npv/client/events/'+campName+'/'+startDate+'/'+endDate,
       	type: 'GET',
        contentType: "application/json",
        //async: false,
        error: function() {
            $('#err').html('<p>An error has occurred</p>');
        },
        dataType: 'json', //you may use jsonp for cross origin request
        crossDomain: true,
      	success: function(res) {
      		console.log(res);
      		drawEventChart(res);
        	   
		}
	});
}
function drawEventChart(res){
	var items = [];
	$(res).each(function(i, val) {
		
		items.push([val.eventAction, Number(val.totalEvents)]);
	});
	items.unshift(['eventAction', 'totalEvents']);
	//console.log(items);
	google.charts.load('current', {packages: ['corechart', 'line']});
	google.charts.setOnLoadCallback(drawChart_event);
	
	function drawChart_event() {
		var data = google.visualization.arrayToDataTable(items);
	    var view = new google.visualization.DataView(data);
	      
	        var options = {
	      	backgroundColor: 'transparent',
	        //title: 'Financial Status Prediction 2019',
	        width: 500,
            height: 350,
            colors: ['red'],
            legend: { position: 'top' },
	        chartArea: {width: '60%'},
	        hAxis: {
	          title: 'Events',
	          minValue: 0
	        },
	        vAxis: {
	          title: 'Total'
	        }
	      };

	      var chart = new google.visualization.LineChart(document.getElementById('chartContainer_event'));
	      chart.draw(view, options);
	    }
}*/

function showEventTracking(){
	var sDate = $("#startdatepicker").datepicker("getDate");
	var eDate = $("#enddatepicker").datepicker("getDate");
	var campName=$("#campaignName").val();
	client_name= sessionStorage.getItem("clientName");
	$('#tblEventsTrack').show();
	$('#tblEventsTrack').dataTable( {
		destroy: true,
		async: false,
	    ajax:{
	    	url: '/npv/client/chart/events/action/'+client_name+'/'+campName+'/'+startDate+'/'+endDate,
	        dataSrc: 'data',
	      },
	    columns: [ 
		    { data: 'eventAction' },
		   	{ data: 'total' },
		   	
	   ]
	} );
}

$('#exportBtn_date').on( 'click', function () {
	if($("#startdatepicker").datepicker("getDate") != "" && $("#enddatepicker").datepicker("getDate")!="" && $("#campaignName").val() != ""){
		exportDateReport();
	}else{
		alert("Oops!! Please select Dates and Campaign");
	}	
	
} );
$('#exportBtn_city').on( 'click', function () {
	if($("#startdatepicker").datepicker("getDate") != "" && $("#enddatepicker").datepicker("getDate")!="" && $("#campaignName").val() != ""){
		exportCityReport();
	}else{
		alert("Oops!! Please select Dates and Campaign");
	}	
	
} );
$('#exportBtn_country').on( 'click', function () {
	if($("#startdatepicker").datepicker("getDate") != "" && $("#enddatepicker").datepicker("getDate")!="" && $("#campaignName").val() != ""){
		exportCountryReport();
	}else{
		alert("Oops!! Please select Dates and Campaign");
	}	
	
} );
$('#exportBtn_dateHour').on( 'click', function () {
	if($("#startdatepicker").datepicker("getDate") != "" && $("#enddatepicker").datepicker("getDate")!="" && $("#campaignName").val() != ""){
		exportDateHourReport();
	}else{
		alert("Oops!! Please select Dates and Campaign");
	}	
} );

$('#exportBtn_browser').on( 'click', function () {
	if($("#startdatepicker").datepicker("getDate") != "" && $("#enddatepicker").datepicker("getDate")!="" && $("#campaignName").val() != ""){
		exportBrowserReport();
	}else{
		alert("Oops!! Please select Dates and Campaign");
	}
} );

$('#exportBtn_os').on( 'click', function () {
	if($("#startdatepicker").datepicker("getDate") != "" && $("#enddatepicker").datepicker("getDate")!="" && $("#campaignName").val() != ""){
		exportOSReport();
	}else{
		alert("Oops!! Please select Dates and Campaign");
	}
} );

$('#exportBtn_uniqueVideos').on( 'click', function () {
	if($("#startdatepicker").datepicker("getDate") != "" && $("#enddatepicker").datepicker("getDate")!="" && $("#campaignName").val() != ""){
		exportAllUniqueVideosReport();
	}else{
		alert("Oops!! Please select Dates and Campaign");
	}
} );
$('#exportBtn_allVideos').on( 'click', function () {
	if($("#startdatepicker").datepicker("getDate") != "" && $("#enddatepicker").datepicker("getDate")!="" && $("#campaignName").val() != ""){
		exportAllVideosReport();
	}else{
		alert("Oops!! Please select Dates and Campaign");
	}
} );

$('#exportBtn_allEvents').on( 'click', function () {
	if($("#startdatepicker").datepicker("getDate") != "" && $("#enddatepicker").datepicker("getDate")!="" && $("#campaignName").val() != ""){
		exportAllEvents();
	}else{
		alert("Oops!! Please select Dates and Campaign");
	}
} );

$('#exportBtn_percentPlayed').on( 'click', function () {
	if($("#startdatepicker").datepicker("getDate") != "" && $("#enddatepicker").datepicker("getDate")!="" && $("#campaignName").val() != ""){
		exportvideoByPercent();
	}else{
		alert("Oops!! Please select Dates and Campaign");
	}
} );

$('#exportBtn_videosNotViewed').on( 'click', function () {
	if($("#startdatepicker").datepicker("getDate") != "" && $("#enddatepicker").datepicker("getDate")!="" && $("#campaignName").val() != ""){
		exportVideosNotViewed();
	}else{
		alert("Oops!! Please select Dates and Campaign");
	}
} );

/*$('#videoCountbtn').on( 'click', function () {
	if($("#startdatepicker").datepicker("getDate") != "" && $("#enddatepicker").datepicker("getDate")!="" && $("#campaignName").val() != ""){
		showVideoCount();
	}else{
		alert("Oops!! Please select Dates and Campaign");
	}
} );*/

function showVideoCount(){
	var sDate = $("#startdatepicker").datepicker("getDate");
	var eDate = $("#enddatepicker").datepicker("getDate");
	var campName=$("#campaignName").val();
	//var categoryName=$("#categoryName").val();
	startDate=$.datepicker.formatDate("yy-mm-dd", sDate);
	endDate=$.datepicker.formatDate("yy-mm-dd", eDate);
	client_name= sessionStorage.getItem("clientName");
	
	$.ajax({
		url: '/npv/client/videosViewCount/'+ client_name+'/'+campName +'/'+ startDate+'/'+endDate,
       	type: 'GET',
        contentType: "application/json",
        async: false,
        error: function() {
            $('#err').html('<p>An error has occurred</p>');
        },
        dataType: 'json', //you may use jsonp for cross origin request
        crossDomain: true,
      	success: function(res) {
      		console.log(res);
      		HTMLstring = '<label style="color:green">Viewed : '+res["viewed"]+ '</label><br/>';
      		HTMLstring += '<label style="color:red"">Not Viewed : ' + res["notviewed"] + '</label><br/>';
      	    
      	    $("#videoCountbtn").html(HTMLstring);
      	    //$('#modalVideoCount').modal('show');
      		   
		}
	});
	
}

function exportDateReport(){
	//alert('in');
	var sDate = $("#startdatepicker").datepicker("getDate");
	var eDate = $("#enddatepicker").datepicker("getDate");
	var campName=$("#campaignName").val();
	//var categoryName=$("#categoryName").val();
	startDate=$.datepicker.formatDate("yy-mm-dd", sDate);
	endDate=$.datepicker.formatDate("yy-mm-dd", eDate);
	client_name= sessionStorage.getItem("clientName");
	
	$.ajax({
		url: '/npv/client/chart/'+ client_name+'/'+ campName+'/eventDate/'+ startDate+'/'+endDate,
       	type: 'GET',
        contentType: "application/json",
        async: false,
        error: function() {
            $('#err').html('<p>An error has occurred</p>');
        },
        dataType: 'json', //you may use jsonp for cross origin request
        crossDomain: true,
      	success: function(res) {
      		//console.log(res);
      		response = res;
      		var responseArray = typeof response != 'object' ? JSON.parse(response) : response;
      		var rowsStr = 'Total, Date\r\n';
      		for (var i = 0; i < responseArray.length; i++) {
		    	var row = '';
		    	for (var index in responseArray[i]) {
		            row += '"' + responseArray[i][index] + '",';
		        }

  	        row.slice(0, row.length - 1);
  	        
  	        //add a line break after each row
  	        rowsStr += row + '\r\n';
      		    
      		}
      		//console.log("row"+rowsStr);
      		var uri = 'data:text/csv;charset=utf-8,' + escape(rowsStr);
      		var link = document.createElement("a");    
      	    link.href = uri;
      	    
      	    link.style = "visibility:hidden";
      	    link.download = "AnalyticsByDate.csv";
      	    
      	    document.body.appendChild(link);
      	    link.click();
      	    document.body.removeChild(link);
      		   
		}
	});	
}

function exportCityReport(){
	var sDate = $("#startdatepicker").datepicker("getDate");
	var eDate = $("#enddatepicker").datepicker("getDate");
	var campName=$("#campaignName").val();
	//var categoryName=$("#categoryName").val();
	startDate=$.datepicker.formatDate("yy-mm-dd", sDate);
	endDate=$.datepicker.formatDate("yy-mm-dd", eDate);
	client_name= sessionStorage.getItem("clientName");
	
	$.ajax({
		url: '/npv/client/chart/'+ client_name+'/'+ campName+'/eventCity/'+ startDate+'/'+endDate,
       	type: 'GET',
        contentType: "application/json",
        async: false,
        error: function() {
            $('#err').html('<p>An error has occurred</p>');
        },
        dataType: 'json', //you may use jsonp for cross origin request
        crossDomain: true,
      	success: function(res) {
      		response = res;
      		var responseArray = typeof response != 'object' ? JSON.parse(response) : response;
      		var rowsStr = 'Total, City\r\n';
      		for (var i = 0; i < responseArray.length; i++) {
		    	var row = '';
		    	for (var index in responseArray[i]) {
		            row += '"' + responseArray[i][index] + '",';
		        }
		    row.slice(0, row.length - 1);
  	        
  	        //add a line break after each row
  	        rowsStr += row + '\r\n';
      		}
      		console.log("row"+rowsStr);
      		var uri = 'data:text/csv;charset=utf-8,' + escape(rowsStr);
      		var link = document.createElement("a");    
      	    link.href = uri;
      	    
      	    link.style = "visibility:hidden";
      	    link.download = "AnalyticsByCity.csv";
      	    
      	    document.body.appendChild(link);
      	    link.click();
      	    document.body.removeChild(link);
      		   
		}
	});
}
function exportCountryReport(){
	var sDate = $("#startdatepicker").datepicker("getDate");
	var eDate = $("#enddatepicker").datepicker("getDate");
	var campName=$("#campaignName").val();
	//var categoryName=$("#categoryName").val();
	startDate=$.datepicker.formatDate("yy-mm-dd", sDate);
	endDate=$.datepicker.formatDate("yy-mm-dd", eDate);
	client_name= sessionStorage.getItem("clientName");
	
	$.ajax({
		url: '/npv/client/chart/'+ client_name+'/'+ campName+'/eventCountry/'+ startDate+'/'+endDate,
       	type: 'GET',
        contentType: "application/json",
        async: false,
        error: function() {
            $('#err').html('<p>An error has occurred</p>');
        },
        dataType: 'json', //you may use jsonp for cross origin request
        crossDomain: true,
      	success: function(res) {
      		response = res;
      		var responseArray = typeof response != 'object' ? JSON.parse(response) : response;
      		var rowsStr = 'Total, Country\r\n';
      		for (var i = 0; i < responseArray.length; i++) {
		    	var row = '';
		    	for (var index in responseArray[i]) {
		            row += '"' + responseArray[i][index] + '",';
		        }
		    row.slice(0, row.length - 1);
  	        
  	        //add a line break after each row
  	        rowsStr += row + '\r\n';
      		}
      		console.log("row"+rowsStr);
      		var uri = 'data:text/csv;charset=utf-8,' + escape(rowsStr);
      		var link = document.createElement("a");    
      	    link.href = uri;
      	    
      	    link.style = "visibility:hidden";
      	    link.download = "AnalyticsByCountry.csv";
      	    
      	    document.body.appendChild(link);
      	    link.click();
      	    document.body.removeChild(link);
      		   
		}
	});
}
function exportDateHourReport(){
	var sDate = $("#startdatepicker").datepicker("getDate");
	var eDate = $("#enddatepicker").datepicker("getDate");
	var campName=$("#campaignName").val();
	//var categoryName=$("#categoryName").val();
	startDate=$.datepicker.formatDate("yy-mm-dd", sDate);
	endDate=$.datepicker.formatDate("yy-mm-dd", eDate);
	client_name= sessionStorage.getItem("clientName");
	
	$.ajax({
		url: '/npv/client/chart/'+ client_name+'/'+ campName+'/hour(eventDateTime)/'+ startDate+'/'+endDate,
       	type: 'GET',
        contentType: "application/json",
        async: false,
        error: function() {
            $('#err').html('<p>An error has occurred</p>');
        },
        dataType: 'json', //you may use jsonp for cross origin request
        crossDomain: true,
      	success: function(res) {
      		response = res;
      		var responseArray = typeof response != 'object' ? JSON.parse(response) : response;
      		var rowsStr = 'Total, DateHour\r\n';
      		for (var i = 0; i < responseArray.length; i++) {
		    	var row = '';
		    	for (var index in responseArray[i]) {
		            row += '"' + responseArray[i][index] + '",';
		        }
		    row.slice(0, row.length - 1);
  	        
  	        //add a line break after each row
  	        rowsStr += row + '\r\n';
      		}
      		//console.log("row"+rowsStr);
      		var uri = 'data:text/csv;charset=utf-8,' + escape(rowsStr);
      		var link = document.createElement("a");    
      	    link.href = uri;
      	    
      	    link.style = "visibility:hidden";
      	    link.download = "AnalyticsByDateHour.csv";
      	    
      	    document.body.appendChild(link);
      	    link.click();
      	    document.body.removeChild(link);
      		   
		}
	});
}
function exportBrowserReport(){
	var sDate = $("#startdatepicker").datepicker("getDate");
	var eDate = $("#enddatepicker").datepicker("getDate");
	var campName=$("#campaignName").val();
	//var categoryName=$("#categoryName").val();
	startDate=$.datepicker.formatDate("yy-mm-dd", sDate);
	endDate=$.datepicker.formatDate("yy-mm-dd", eDate);
	client_name= sessionStorage.getItem("clientName");
	
	$.ajax({
		url: '/npv/client/chart/'+ client_name+'/'+ campName+'/eventBrowser/'+ startDate+'/'+endDate,
       	type: 'GET',
        contentType: "application/json",
        async: false,
        error: function() {
            $('#err').html('<p>An error has occurred</p>');
        },
        dataType: 'json', //you may use jsonp for cross origin request
        crossDomain: true,
      	success: function(res) {
      		response = res;
      		var responseArray = typeof response != 'object' ? JSON.parse(response) : response;
      		var rowsStr = 'Total, Browser\r\n';
      		for (var i = 0; i < responseArray.length; i++) {
		    	var row = '';
		    	for (var index in responseArray[i]) {
		            row += '"' + responseArray[i][index] + '",';
		        }
		    row.slice(0, row.length - 1);
  	        
  	        //add a line break after each row
  	        rowsStr += row + '\r\n';
      		}
      		//console.log("row"+rowsStr);
      		var uri = 'data:text/csv;charset=utf-8,' + escape(rowsStr);
      		var link = document.createElement("a");    
      	    link.href = uri;
      	    
      	    link.style = "visibility:hidden";
      	    link.download = "AnalyticsByBrowser.csv";
      	    
      	    document.body.appendChild(link);
      	    link.click();
      	    document.body.removeChild(link);
      		   
		}
	});
}
function exportOSReport(){
	var sDate = $("#startdatepicker").datepicker("getDate");
	var eDate = $("#enddatepicker").datepicker("getDate");
	var campName=$("#campaignName").val();
	//var categoryName=$("#categoryName").val();
	startDate=$.datepicker.formatDate("yy-mm-dd", sDate);
	endDate=$.datepicker.formatDate("yy-mm-dd", eDate);
	client_name= sessionStorage.getItem("clientName");
	
	$.ajax({
		url: '/npv/client/chart/'+ client_name+'/'+ campName+'/eventOperatingSystem/'+ startDate+'/'+endDate,
       	type: 'GET',
        contentType: "application/json",
        async: false,
        error: function() {
            $('#err').html('<p>An error has occurred</p>');
        },
        dataType: 'json', //you may use jsonp for cross origin request
        crossDomain: true,
      	success: function(res) {
      		response = res;
      		var responseArray = typeof response != 'object' ? JSON.parse(response) : response;
      		var rowsStr = 'Total, Operating System\r\n';
      		for (var i = 0; i < responseArray.length; i++) {
		    	var row = '';
		    	for (var index in responseArray[i]) {
		            row += '"' + responseArray[i][index] + '",';
		        }
		    row.slice(0, row.length - 1);
  	        
  	        //add a line break after each row
  	        rowsStr += row + '\r\n';
      		}
      		//console.log("row"+rowsStr);
      		var uri = 'data:text/csv;charset=utf-8,' + escape(rowsStr);
      		var link = document.createElement("a");    
      	    link.href = uri;
      	    
      	    link.style = "visibility:hidden";
      	    link.download = "AnalyticsByOS.csv";
      	    
      	    document.body.appendChild(link);
      	    link.click();
      	    document.body.removeChild(link);
      		   
		}
	});
}
function exportAllUniqueVideosReport(){
	var sDate = $("#startdatepicker").datepicker("getDate");
	var eDate = $("#enddatepicker").datepicker("getDate");
	var campName=$("#campaignName").val();
	//var categoryName=$("#categoryName").val();
	startDate=$.datepicker.formatDate("yy-mm-dd", sDate);
	endDate=$.datepicker.formatDate("yy-mm-dd", eDate);
	client_name= sessionStorage.getItem("clientName");
	
	$.ajax({
		url: '/npv/client/uniqueVideos/'+ client_name+'/'+ campName+'/'+ startDate+'/'+endDate,
       	type: 'GET',
        contentType: "application/json",
        async: false,
        error: function() {
            $('#err').html('<p>An error has occurred</p>');
        },
        dataType: 'json', //you may use jsonp for cross origin request
        crossDomain: true,
      	success: function(res) {
      		console.log(res);
      		response = res;
      		var responseArray = typeof response != 'object' ? JSON.parse(response) : response;
      		var rowsStr = 'Customer Name,Video File,Customer Id\r\n';
      		for (var i = 0; i < responseArray.length; i++) {
		    	var row = '';
		    	for (var index in responseArray[i]) {
		            row += '"' + responseArray[i][index] + '",';
		        }
		    row.slice(0, row.length - 1);
  	        
  	        //add a line break after each row
  	        rowsStr += row + '\r\n';
      		}
      		//console.log("row"+rowsStr);
      		var uri = 'data:text/csv;charset=utf-8,' + escape(rowsStr);
      		var link = document.createElement("a");    
      	    link.href = uri;
      	    
      	    link.style = "visibility:hidden";
      	    link.download = "VideosViewed.csv";
      	    
      	    document.body.appendChild(link);
      	    link.click();
      	    document.body.removeChild(link);
      		   
		}
	});
}
function exportAllVideosReport(){
	var sDate = $("#startdatepicker").datepicker("getDate");
	var eDate = $("#enddatepicker").datepicker("getDate");
	var campName=$("#campaignName").val();
	//var categoryName=$("#categoryName").val();
	startDate=$.datepicker.formatDate("yy-mm-dd", sDate);
	endDate=$.datepicker.formatDate("yy-mm-dd", eDate);
	client_name= sessionStorage.getItem("clientName");
	
	$.ajax({
		url: '/npv/client/eventSummary/'+ client_name+'/'+ campName+'/'+ startDate+'/'+endDate,
       	type: 'GET',
        contentType: "application/json",
        async: false,
        error: function() {
            $('#err').html('<p>An error has occurred</p>');
        },
        dataType: 'json', //you may use jsonp for cross origin request
        crossDomain: true,
      	success: function(res) {
      		console.log(res);
      		response = res;
      		var responseArray = typeof response != 'object' ? JSON.parse(response) : response;
      		var rowsStr = 'Opearating System,Date Time,City,Country,Customer Name,Customer ID,Browser,Video,Date\r\n';
      		for (var i = 0; i < responseArray.length; i++) {
		    	var row = '';
		    	for (var index in responseArray[i]) {
		            row += '"' + responseArray[i][index] + '",';
		        }
		    row.slice(0, row.length - 1);
  	        
  	        //add a line break after each row
  	        rowsStr += row + '\r\n';
      		}
      		//console.log("row"+rowsStr);
      		var uri = 'data:text/csv;charset=utf-8,' + escape(rowsStr);
      		var link = document.createElement("a");    
      	    link.href = uri;
      	    
      	    link.style = "visibility:hidden";
      	    link.download = "AllVideos.csv";
      	    
      	    document.body.appendChild(link);
      	    link.click();
      	    document.body.removeChild(link);
      		   
		}
	});
}

function exportAllEvents(){
	var sDate = $("#startdatepicker").datepicker("getDate");
	var eDate = $("#enddatepicker").datepicker("getDate");
	var campName=$("#campaignName").val();
	//var categoryName=$("#categoryName").val();
	startDate=$.datepicker.formatDate("yy-mm-dd", sDate);
	endDate=$.datepicker.formatDate("yy-mm-dd", eDate);
	client_name= sessionStorage.getItem("clientName");
	
	$.ajax({
		url: '/npv/client/events/'+ client_name+'/'+ campName+'/'+ startDate+'/'+endDate,
       	type: 'GET',
        contentType: "application/json",
        async: false,
        error: function() {
            $('#err').html('<p>An error has occurred</p>');
        },
        dataType: 'json', //you may use jsonp for cross origin request
        crossDomain: true,
      	success: function(res) {
      		console.log(res);
      		response = res;
      		var responseArray = typeof response != 'object' ? JSON.parse(response) : response;
      		var rowsStr = 'OperatingSystem,DateTime,Client,Country,Total Events,Campaign,Browser,Action,Video,City,Customer Name,Customer Id,Date,\r\n';
      		for (var i = 0; i < responseArray.length; i++) {
		    	var row = '';
		    	for (var index in responseArray[i]) {
		            row += '"' + responseArray[i][index] + '",';
		        }
		    row.slice(0, row.length - 1);
  	        
  	        //add a line break after each row
  	        rowsStr += row + '\r\n';
      		}
      		//console.log("row"+rowsStr);
      		var uri = 'data:text/csv;charset=utf-8,' + escape(rowsStr);
      		var link = document.createElement("a");    
      	    link.href = uri;
      	    
      	    link.style = "visibility:hidden";
      	    link.download = "AllEvents.csv";
      	    
      	    document.body.appendChild(link);
      	    link.click();
      	    document.body.removeChild(link);
      		   
		}
	});
}

function exportvideoByPercent(){
	var sDate = $("#startdatepicker").datepicker("getDate");
	var eDate = $("#enddatepicker").datepicker("getDate");
	var campName=$("#campaignName").val();
	//var categoryName=$("#categoryName").val();
	startDate=$.datepicker.formatDate("yy-mm-dd", sDate);
	endDate=$.datepicker.formatDate("yy-mm-dd", eDate);
	client_name= sessionStorage.getItem("clientName");
	
	$.ajax({
		url: '/npv/client/videosViewedByPercent/'+ client_name+'/'+ campName+'/'+ startDate+'/'+endDate,
       	type: 'GET',
        contentType: "application/json",
        async: false,
        error: function() {
            $('#err').html('<p>An error has occurred</p>');
        },
        dataType: 'json', //you may use jsonp for cross origin request
        crossDomain: true,
      	success: function(res) {
      		console.log(res);
      		response = res;
      		var responseArray = typeof response != 'object' ? JSON.parse(response) : response;
      		var rowsStr = 'Customer Name,Video,Customer Id,Percent Played,\r\n';
      		for (var i = 0; i < responseArray.length; i++) {
		    	var row = '';
		    	for (var index in responseArray[i]) {
		            row += '"' + responseArray[i][index] + '",';
		        }
		    row.slice(0, row.length - 1);
  	        
  	        //add a line break after each row
  	        rowsStr += row + '\r\n';
      		}
      		//console.log("row"+rowsStr);
      		var uri = 'data:text/csv;charset=utf-8,' + escape(rowsStr);
      		var link = document.createElement("a");    
      	    link.href = uri;
      	    
      	    link.style = "visibility:hidden";
      	    link.download = "VideoByPercentage.csv";
      	    
      	    document.body.appendChild(link);
      	    link.click();
      	    document.body.removeChild(link);
      		   
		}
	});
}

function exportVideosNotViewed(){
	var sDate = $("#startdatepicker").datepicker("getDate");
	var eDate = $("#enddatepicker").datepicker("getDate");
	var campName=$("#campaignName").val();
	//var categoryName=$("#categoryName").val();
	startDate=$.datepicker.formatDate("yy-mm-dd", sDate);
	endDate=$.datepicker.formatDate("yy-mm-dd", eDate);
	client_name= sessionStorage.getItem("clientName");
	
	$.ajax({
		url: '/npv/client/videosNotViewed/'+ client_name+'/'+ campName+'/'+ startDate+'/'+endDate,
       	type: 'GET',
        contentType: "application/json",
        async: false,
        error: function() {
            $('#err').html('<p>An error has occurred</p>');
        },
        dataType: 'json', //you may use jsonp for cross origin request
        crossDomain: true,
      	success: function(res) {
      		console.log(res);
      		response = res;
      		var responseArray = typeof response != 'object' ? JSON.parse(response) : response;
      		var rowsStr = 'Customer Name, Video File,Customer Id\r\n';
      		for (var i = 0; i < responseArray.length; i++) {
		    	var row = '';
		    	for (var index in responseArray[i]) {
		            row += '"' + responseArray[i][index] + '",';
		        }
		    row.slice(0, row.length - 1);
  	        
  	        //add a line break after each row
  	        rowsStr += row + '\r\n';
      		}
      		//console.log("row"+rowsStr);
      		var uri = 'data:text/csv;charset=utf-8,' + escape(rowsStr);
      		var link = document.createElement("a");    
      	    link.href = uri;
      	    
      	    link.style = "visibility:hidden";
      	    link.download = "VideosNotViewed.csv";
      	    
      	    document.body.appendChild(link);
      	    link.click();
      	    document.body.removeChild(link);
      		   
		}
	});
}
function fillCampaign(){
	user_name= sessionStorage.getItem("logon");
	select = document.getElementById('campaignName');
    removeOptions(select);
    $.ajax({
		url: '/npv/campaign/' + user_name,
        type: 'GET',
        contentType: "application/json",
        error: function(res) {
			
      	},
      	dataType: 'json', //you may use jsonp for cross origin request
      	crossDomain: true,
      	success: function(res) {
        	console.log(res);
        	var opt1 = document.createElement('option');
      		opt1.value = "";
      		opt1.innerHTML = "---Select Project---";
      		select.appendChild(opt1);
        	$(res).each(function(i, val) {
           		var opt = document.createElement('option');
           		opt.value = val.project_details;
           		opt.innerHTML = val.project_details;
           		select.appendChild(opt);
        
         	});
        }
  	});    
}

function removeOptions(selectbox){
	   var i;
	   for(i = selectbox.options.length - 1 ; i >= 0 ; i--){
	       selectbox.remove(i);
	   }
	}

function getClientName(){
	
	user_name= sessionStorage.getItem("logon");
	 $.ajax({
		url: '/npv/client/' + user_name,
        type: 'GET',
        contentType: "application/json",
        error: function(res) {
			
      	},
      	dataType: 'json', //you may use jsonp for cross origin request
      	crossDomain: true,
      	success: function(res) {
        	$(res).each(function(i, val) {
        		clientName=val.client_name;
        		console.log(clientName);
        		sessionStorage.setItem("clientName",clientName);
        
         	});
        }
  	});    
}
