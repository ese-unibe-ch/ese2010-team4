$(function() {
	$('div#graph').ready(function () {
	
		loadGraph();
	
	});
	$('div.profileTab').hide();
	$('div#userInfo').show();
	$('a[href=#userInfo]').addClass("active");
	$('a.button[rel=tab]').click(function(){
		$('a.button[rel=tab]').removeClass("active");
		$(this).addClass("active");
		var a = this.hash.substr(1);
		
		$('div.profileTab').hide();		
		$('div.profileTab').each(function(){
			
			if(this.id==a){				
				$(this).show();
				
				if(this.id=="userActivities"){
					
					loadGraph();
				}					
			}
			
		});
		return false;
		
	});

});

function loadGraph() {
	if(!$("div#graph")[0].loaded) {
		
		jQuery.getJSON( graphData({id: userid}), displayGraph);
		$("div#graph")[0].loaded = true;
	}
}


function displayGraph(data, status) {
	var points = [];
		
	for(i in data) {
		
		points.push([data[i].time, data[i].value]);
	}

    $.plot($("#graphcanvas"), [
        {
            data: points,
            lines: { show: true, steps: true },
            points: { show: false }
        }
    ], {
     xaxis: {
    		 mode: "time"
     }
    } );
 }



