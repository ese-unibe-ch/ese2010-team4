<script type="text/javascript">
var toShow = true;
	function show()
	{
		if (!toShow){
			this.hide()
		}
		else {
			var myAttr = document.createAttribute("class");
			myAttr.nodeValue = "showElem";
			var myObj = document.getElementById('dropDownMenu');
			myObj.setAttributeNode(myAttr);
			toShow = false;
		}
	}
	
	function hide()
	{
		var myObj = document.getElementById('dropDownMenu');
		myObj.setAttribute('class','hideElem');
		toShow = true;
	}

</script>

<div class="pulldown">
	<a href="#" onclick="show()">${_label}&nbsp</a>
	<div id="dropDownMenu">
		<menu>
			#{doBody /}
		</menu>
	</div>
</div>
