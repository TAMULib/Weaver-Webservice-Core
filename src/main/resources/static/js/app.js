var stompClient = null;

function setConnected(connected) {
   if(connected) {     
        $(".connect-btn").fadeOut(300);
        $(".app-splash").fadeOut(300, function() {
            $(".app-content").show(300);
            $(".disconnect-btn").fadeIn(300)
            $(".screen-name").attr("disabled", true);
            $(".chatInput").show(300); 
        }); 
   } else {
        $(".chatInput").fadeOut(350);
        $(".disconnect-btn").fadeOut(350);
        $(".app-content").fadeOut(350, function() {
            $(".connect-btn").fadeIn(350);
            $(".screen-name").val("").attr("disabled", false);
            $(".app-splash").show(350); 
        }); 
   }
}

function connect(name) {
    var socket = new SockJS('/chat');
    stompClient = Stomp.over(socket);
    
    stompClient.connect({name: name}, 
        function success(res) { 
            setConnected(true);
            stompClient.subscribe('/WSRes/chat', function subscribed(res){
                
                var resObj = JSON.parse(res.body)

                if(resObj.action == "CHAT") showMessage(resObj, name);
                if(resObj.action == "UPDATE") updateUsers(resObj);    
            
            }, {name: name});
        },
        function error(res) {
            setTimeout(function() {
                    disconnect();
                }, 1500);
            $(".connect-btn").html(res.headers.message);
            $(".screen-name").val("");
        }
    );
}

function disconnect() {
    stompClient.disconnect();
    setConnected(false);
}

function updateUsers(update) {
	
	$(".room-occupants").html("");
	
	$(update.users).each(function(index, user) {
		$(".room-occupants").append("<li>"+user+"</li>");
	});
	
}


function showMessage(message, userName) {
    var messageOwner = userName == message.name ? "myself" : "someoneElse";
    var messageTemplate = '<div class="message-wrapper '+messageOwner+'">' +
                            '<span class="message-name">'+message.name+'</span>:' +
                            '<span class="message-content">'+ message.message+'</span>' +
                          '</div>';

    $(".chat-window").append(messageTemplate);
    $(".chat-window").scrollTop($(".chat-window")[0].scrollHeight);

}


$(".chatInput").on("keypress", function(e) { 

    var $this = $(this);

    if(e.keyCode == 13) {
        var message = $this.val();
        $this.val("");
        var name = $(".screen-name").val();

        stompClient.send("/ws/chat", 
            {
                name: name
            }, 
            JSON.stringify({ 
                'message': message, 
                'name': name 
            }
        ));
    }

});

    
$(".connect-btn").on("click", function() {
    var $this = $(this);
    var name = $(".screen-name").val();

    if(name != "") {
        connect(name);
    } else {
        $this.html("Input Screen Name")
    }

    return false;

});

$(".disconnect-btn").on("click", function() {
    
    disconnect()

    return false;

});
