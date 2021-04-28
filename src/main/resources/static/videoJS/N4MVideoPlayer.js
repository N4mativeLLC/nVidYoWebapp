

/*var myPlayer = videojs('videoarea');
myPlayer.src('http://vjs.zencdn.net/v/oceans.mp4');
myPlayer.ready(function() {
myPlayer.play();
});*/

var wrapper = $('<div/>');
wrapper.addClass('box');

var video = $('<video></video>');
video.addClass("video-js vjs-default-skin vjs-big-play-centered ");
video.attr('id', 'box-' + "N4M");
video.appendTo(wrapper);

var annotation = $('<span></span>');
annotation.addClass('grid-overlay-bottom-right');
annotation.appendTo(wrapper);

wrapper.appendTo('#N4mPlayerDiv');

videojs("box-" + "N4M", {
techOrder: ['flash', 'html5'],
autoplay: true,
width: 640,
height: 480,
controls: true,
muted:true,
sources: [{
  src:'http://vjs.zencdn.net/v/oceans.mp4',
  type: "video/mp4",

}]
});

	