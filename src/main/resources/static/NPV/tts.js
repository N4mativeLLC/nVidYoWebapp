//$(document).ready(function () {
	/*$("#btnplay").on('click', function (e) {
	    //debugger;
	    var speechText = $("#speechTxt").val();
	    var u1 = new SpeechSynthesisUtterance(speechText);
	    u1.lang = 'en-US';
	    u1.pitch = 1;
	    u1.rate = 1;
	    //u1.voice = voices[10];
	    u1.voiceURI = 'native';
	    u1.volume = 1;
	    speechSynthesis.speak(u1);
	});*/
	
   /* var obj = document.createElement("audio"); 
    obj.setAttribute("src", "Users/surbhi/Documents/workspace/liamwebapp/output_ssml.mp3"); 
    $.get(); 

    $("#btnplay").click(function() { 
        obj.play(); 
    }); 
   
});*/


$("#btnplay").click(function() { 
	playAudio(); 
}); 


function playAudio(){
	ssml=	$("#speechTxt").val();
	gender= $("#gender").val();
	language= $("#lang").val();
	$.ajax({
		url: '/npv/tts/'+ gender +'/'+ language,
		type: 'PUT',
	 	data: ssml,
	   	dataType: 'json', //you may use jsonp for cross origin request
	   	contentType: 'audio/mpeg',//application/json',
	   	//responseType: 'blob',
	   	error: function(data) {
	   		//console.log(data);
			//console.log(data.responseText);
			resultAudio="data:audio/mpeg;base64,"+ data.responseText;  
			var binary= convertDataURIToBinary(resultAudio);
			console.log(binary);
			var blob=new Blob([binary], {type : 'audio/mpeg'});
			console.log(blob);
			var blobUrl = URL.createObjectURL(blob);
			/*var a = document.createElement("a");
	        a.href = blobUrl;
	        a.download = "myaudioFile.mp3";
	        document.body.appendChild(a);
	        a.click();*/
	        sessionStorage.setItem("blobUrl", blobUrl);
			//$('body').append('<br> Blob URL : <a href="'+blobUrl+'" target="_blank">'+blobUrl+'</a><br>');
			$("#source").attr("src", blobUrl);
			$("#myAudio")[0].pause();
			$("#myAudio")[0].load();//suspends and restores all audio element
			$("#myAudio")[0].oncanplaythrough =  $("#myAudio")[0].play();
			
		},
		crossDomain: true,
	   	success: function(data) {
	   		console.log(data.responseText);
		}
	});
}

function convertDataURIToBinary(dataURI) {
	  var BASE64_MARKER = ';base64,';
	  var base64Index = dataURI.indexOf(BASE64_MARKER) + BASE64_MARKER.length;
	  var base64 = dataURI.substring(base64Index);
	  var raw = window.atob(base64);
	  var rawLength = raw.length;
	  var array = new Uint8Array(new ArrayBuffer(rawLength));
	  
	  for(i = 0; i < rawLength; i++) {
	    array[i] = raw.charCodeAt(i);
	  }
	  return array;
	}

$("#btnDownload").click(function() { 
	downloadAudio(); 
});

function downloadAudio(){
	blobUrl= sessionStorage.getItem("blobUrl");
	var a = document.createElement("a");
    a.href = blobUrl;
    a.download = "myaudioFile.mp3";
    document.body.appendChild(a);
    a.click();
}

$( '#btnBreak' ).on('click', function(){
    var cursorPos = $('#speechTxt').prop('selectionStart');
    var v = $('#speechTxt').val();
    var textBefore = v.substring(0,  cursorPos );
    var textAfter  = v.substring( cursorPos, v.length );
    $('#speechTxt').val( textBefore+ $(this).val() +textAfter );
});

$( '#btnEmphasis' ).on('click', function(){
	selectedText= window.getSelection().toString();
	textToAdd= "<emphasis>"+ selectedText+"</emphasis>";
	replaceIt($('#speechTxt')[0], textToAdd); 
    
});

function replaceIt(txtarea, newtxt) {
	  $(txtarea).val(
	        $(txtarea).val().substring(0, txtarea.selectionStart)+
	        newtxt+
	        $(txtarea).val().substring(txtarea.selectionEnd)
	   );  
	}