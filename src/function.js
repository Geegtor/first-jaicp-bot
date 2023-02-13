function deleteFromCart(name) {
    var $session = $jsapi.context().session;
    for (var i = 0; i < $session.cart.length; i++) {
        var current_position = $session.cart[i];
        if(current_position.name === name){
            $session.cart.splice(i, 1);
        }
    }
}

function getTotalSum() {
    var totalSum = 0;
    var $session = $jsapi.context().session;

    for (var i = 0; i < $session.cart.length; i++) {
        var current_position = $session.cart[i];
        for(var id = 1; id < Object.keys(pizza).length + 1; id++) {
            if (current_position.name === db[id].value.title) {
                var color = _.find(db[id].value.colors, function(color) {
                    return color.id === current_position.id;
                });
                totalSum += color.price * current_position.quantity;
            }
        }
    }
    return totalSum;
}

function editText(messageId, text) {
    var $response = $jsapi.context().response;
    var reply = {
        type: "raw",
        body: {
            text: text,
            message_id: messageId,
            reply_markup: {
                "resize_keyboard": false,
            }
        },
        method: "editMessageText"
    };

    $response.replies = $response.replies || [];
    $response.replies.push(reply);
}
