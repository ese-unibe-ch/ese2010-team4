/**
 * jCountr
 * Creates a countdown timer from a jQuery object. 
 *
 * $(<selector>).counter({params});
 */
 
jQuery.fn.countDown = function(params) {
	var self = this;

	self.display = $(this);
	self.sec = params.sec?params.sec:"0";
	self.message = params.message?params.message:"";
	self.finMessage = params.finMessage?params.finMessage:"Finished";
	self.interval = params.interval?params.interval*1000:"1000";

   	/** update the text for the countdown timer **/
    self._tick = function() {

    	if(self.sec > 0) {
	    	var counter = self._getCounterFromTimestamp(self.sec);
	    	
	    	/** show the current time **/
	        self.display.html(self.message + " " + counter);
	        
	        self.sec -= 1;

            return;
	    } else {
    		window.clearInterval(self._interval);
            /** display the finish message **/
            self.display.html(self.finMessage);
            
            return;
    	}
    };
    
    /** get a counter from an timestamp **/
	self._getCounterFromTimestamp = function(t) {
		if (t > 0) {
			hours = Math.floor(t / 3600)
			minutes = Math.floor( (t / 3600 - hours) * 60)
	    	seconds = Math.round( ( ( (t / 3600 - hours) * 60) - minutes) * 60)
	    } else {
	        hours = 0;
	        minutes = 0;
	        seconds = 0;
	    }
	    
	    if (seconds == 60)  {
	    	seconds = 0;
	    }
	    
	    if(seconds == 0)  {
	    	if(hours != 0) 	{
	    		minutes = minutes/1 + 1;
	    	}
	    }
	    
	    if (minutes < 10)  {
	    	if (minutes < 0) {
	    		minutes = 0;
	    	}
		    minutes = '0' + minutes;
	    }
	    if (seconds < 10) {
	    	if (seconds < 0) {
	    		seconds = 0;
	    	}
	    	seconds = '0' + seconds;
	    }
	    
	    if (hours < 10) {
	    	if (hours < 0) {
	    		hours = 0;
	    	}
	    	hours = '0' + hours;
	    }
		
		if(hours > 0) {
		    return hours + ":" + minutes + ":" + seconds;
		} else {
			return minutes + ":" + seconds;
		}
	};

    /** do it **/
    self._tick();
    self._interval = window.setInterval(self._tick,self.interval);

    return this;
};