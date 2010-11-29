$(function() {
	$('div#graph').ready(function () {
	
		loadGraph();
	
	});

});

function loadGraph() {
	if(!$("#graph")[0].loaded) {
		jQuery.getJSON( graphData({id: userid}), displayGraph);
		$("#graph")[0].loaded = true;
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



