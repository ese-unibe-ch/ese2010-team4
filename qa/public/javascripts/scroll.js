jQuery.easing.quart = function (x, t, b, c, d) {
	return -c * ((t=t/d-1)*t*t*t - 1) + b;
};

jQuery(document).ready(function(){

	jQuery('a[href*=#main],a[href*=#respond]').click(function() {
	if (location.pathname.replace(/^\//,'') == this.pathname.replace(/^\//,'') && location.hostname == this.hostname) {
		var $target = jQuery(this.hash);
		$target = $target.length && $target || jQuery('[name=' + this.hash.slice(1) +']');
 		if ($target.length) {
			var targetOffset = $target.offset().top;
			jQuery('html,body').animate({ scrollTop: targetOffset }, 1200, 'quart');
			return false;
		}
	}
	});

});
