/**
 * jQuery's Countdown Plugin
 *
 * display a countdown effect at given seconds, check out the following website for further information:
 * http://heartstringz.net/blog/posts/show/jquery-countdown-plugin
 *
 * @author Felix Ding
 * @version 0.1
 * @copyright Copyright(c) 2008. Felix Ding
 * @license http://www.opensource.org/licenses/bsd-license.php The BSD License
 * @date 2008-03-09
 * @lastmodified 2008-03-09 17:48    		 
 * @todo error & exceptions handling
*/
jQuery.fn.countdown = function(options) {
	/**
	 * app init
	*/	
	if(!options) options = '()';
	if(jQuery(this).length == 0) return false;
	var obj = this;	

	/**
	 * break out and execute callback (if any)
	 */
	if(options.seconds < 0 || options.seconds == 'undefined')
	{
		if(options.callback) eval(options.callback);
		return null;
	}

	/**
	 * recursive countdown
	 */
	window.setTimeout(
		function() {
			jQuery(obj).html(String(options.seconds));
			--options.seconds;
			jQuery(obj).countdown(options);
		}
		, 1000
	);	

	/**
     * return null
     */
    return this;
}