
(function ($) {
    

    
    /*==================================================================
    [ Validate ]*/
    
    var input = $('.validate-input .input100');
    $('.validate-form').on('submit',function(){
	
	console.log("at validate-form");
        var check = true;

        for(var i=0; i<input.length; i++) {
            if(validate(input[i]) == false){
                showValidate(input[i]);
                check=false;
            }
        }
        
        console.log("check value ====> "+check);
        
        loginpoc();
        
        return check;
    });


    $('.validate-form .input100').each(function(){
	console.log("at validate-form checking");
        $(this).focus(function(){
           hideValidate(this);
        });
    });
    
    function loginpoc(){
	
	console.log("login entered");
	
		var combined = "?email=" + $("#email").val() + "&" + "password=" + $("#pass").val()
	console.log("at login"+combined);
		var request;
		request = $.ajax({
			url: 'http://localhost:8179/user/login' + combined,
			type: 'GET',
			dataType: 'json',
			async: false,
			success: function(data) {
				console.log("login successfull" + JSON.stringify(data));
				var d = JSON.stringify(data);
				var f = JSON.parse(d);
				var status = new String(f.InsertStatus);
				//console.log($.type status)
				if(status == "Success") {
					setCookie("sessionUUid", f.sessionTrack, 20);
					setCookie("userId", f.user_id, 20);
				}
				
				console.log("InsertStatus : " + f.InsertStatus);
				console.log("sessionTrack : " + f.sessionTrack);
				
			},
			error: function(data) {
				console.log("login failed" + data);
				alert("Login Failed - Please Try Again !");
				window.location.href = "http://localhost:8179/login.html";
			}

		});

		request.done(function(jqXHR, textStatus, response) {
			console.log("login done");
			var delayInMilliseconds = 1; //1 second
			if(getCookie("sessionUUid") != null && getCookie("userId") != null && getCookie("sessionUUid") != "" && getCookie("userId") != ""){
		
			setTimeout(function() {
				console.log("After Delay");
				window.location.href = "http://localhost:8179/success.html";
			}, delayInMilliseconds);
			} else {
				
				alert("Login Failed - Please Try Again !");
				window.location.href = "http://localhost:8179/login.html";
			}
			
		});
	
       	  console.log("at validate-form after finishing");
	
	}

    function validate (input) {
	console.log("at validate");
        if($(input).attr('type') == 'email' || $(input).attr('name') == 'email') {
            if($(input).val().trim().match(/^([a-zA-Z0-9_\-\.]+)@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.)|(([a-zA-Z0-9\-]+\.)+))([a-zA-Z]{1,5}|[0-9]{1,3})(\]?)$/) == null) {
                return false;
            }
        }
        else {
            if($(input).val().trim() == ''){
                return false;
            }
        }
    }

    function showValidate(input) {
	console.log("at validate-show");
        var thisAlert = $(input).parent();

        $(thisAlert).addClass('alert-validate');
    }

    function hideValidate(input) {
	console.log("at validate-hide");
        var thisAlert = $(input).parent();

        $(thisAlert).removeClass('alert-validate');
    }
    
    function setCookie(cname, cvalue, minutes) {
	const d = new Date();
	d.setTime(d.getTime() + (minutes * 60 * 1000));
	let expires = "expires=" + d.toUTCString();
	document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
}

function getCookie(cname) {
	let name = cname + "=";
	let decodedCookie = decodeURIComponent(document.cookie);
	let ca = decodedCookie.split(';');
	for (let i = 0; i < ca.length; i++) {
		let c = ca[i];
		while (c.charAt(0) == ' ') {
			c = c.substring(1);
		}
		if (c.indexOf(name) == 0) {
			return c.substring(name.length, c.length);
		}
	}
	return "";
}

  

})(jQuery);


  