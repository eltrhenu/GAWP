function enablePassport() {
				document.getElementById("extra-1").style = "visibility: visable";
	 			document.getElementById("no1").style = "display: block";
        }
	
function disablePassport() {
				document.getElementById("extra-1").style = "visibility: hidden";
				document.getElementById("yes1").style = "display: block";

        }

function enableWork() {
				document.getElementById("extra-2").style = "visibility: visable";
	 			document.getElementById("yes2").style = "display: block";
        }
	
function disableWork() {
				document.getElementById("extra-2").style = "visibility: hidden";
				document.getElementById("no2").style = "display: block";

        }


$(document).ready(function () {
  $('textarea[data-limit-rows=true]')
    .on('keypress', function (event) {
        var textarea = $(this),
            text = textarea.val(),
            numberOfLines = (text.match(/\n/g) || []).length + 1,
            maxRows = parseInt(textarea.attr('rows'));

        if (event.which === 13 && numberOfLines === maxRows ) {
          return false;
        }
    });
});

function toNote(){
  var elemtext = document.getElementById("noteText").value;
  document.getElementById("note").value = elemtext;
};


function checkWork(string1, string2){
  if(string1 == string2){//Çalışıyorsa
    enableWork();
  }
  else{
    disableWork();
  }
};

function checkPassport(string1, string2){
  if(string1 == string2){//IDsi yok ise
    enablePassport();
  }
  else {
    disablePassport();
  }
};