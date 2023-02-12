require: function.js

theme: /

    state: Cart
        intent!: /корзина
        a: Ваша корзина:
        script:
            $temp.totalSum = 0;
            var n = 0;
            for (var i = 0; i < $session.cart.length; i++) {
                var current_position = $session.cart[i];
                for (var id = 1; id < Object.keys(db).length + 1; id++) {
                    if (current_position.name == db[id].value.title){
                        var color = _.find(db[id].value.colors, function(color){
                            return color.id === current_position.id;
                        });

                        n++;

                        if (!color) {
                            $reactions.answer("Что-то пошло не так, вариант не найден для id " + current_position.id);
                        } else {
                            $reactions.answer(n + ". " + current_position.name + ", " + color.name + "\nЦена: " + color.price + "\nКоличество: " + current_position.quantity);

                            $reactions.inlineButtons({text: "Удалить", callback_data: current_position.name});

                            $temp.totalSum += color.price * current_position.quantity;
                        }
                    }
                }
            }

        a: Общая сумма заказа: {{ $temp.totalSum }} рублей.
        a: Если все верно, отправьте свой номер телефона, и наш менеджер с вами свяжется.
        buttons:
            {text: "Отправить номер телефона", request_contact: true}
            "Меню" -> /chooseFlower

        state: Edit
            event: telegramCallbackQuery
            script:
                var name = $request.rawRequest.callback_query.data;
                deleteFromCart(name);
                var message_id = $request.rawRequest.callback_query.message.message_id;

                editText(message_id, 'Удален');
                editText($session.messageId, 'Общая сумма заказа: ' + getTotalSum() + ' руб.');
            if: $session.cart.length == 0
                a: Вы очистили корзину
                go!: /chooseFlower

            state: ClickButtons
                q: *
                a: Нажмите, пожалуйста, кнопку.
                go!: ..

    state: GetPhoneNumber
        event: telegramSendContact
        script:
            $client.phone_number = $request.rawRequest.message.contact.phone_number;
            for (var i = 0; i < $session.cart.length; i++) {
                var current_position = $session.cart[i];
                var color = _.find(db[id].value.colors, function(color){
                    return color.id === current_position.id;
                });
                $temp.totalSum += color.price * current_position.quantity;
                        
                $integration.googleSheets.writeDataToLine(
                    "4404df16-bfc7-4bc6-9f84-65d02d000217",
                    "1571cbcb-0875-4d5d-8462-e0246a60d3b4",
                    "Sheet1",
                    [
                        $jsapi.currentTime(), 
                        $client.phone_number, 
                        current_position.name,
                        current_position.quantity,
                        $temp.totalSum,    
                    ]
                );
            }
        a: Спасибо! Наш менеджер свяжется с вами по номеру телефона {{ $client.phone_number }}.
