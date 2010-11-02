function loadGraph() {
	if(!$("#graph").loaded) {
		jQuery.getJSON( self.location, displayGraph);
		$("#graph").loaded = true;
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