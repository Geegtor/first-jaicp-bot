require: function.js

theme: /

    state: Cart
        intent!: /корзина
        a: Ваша корзина:
        script:
            $temp.totalSum = 0;
            for (var i = 0; i < $session.cart.length; i++) {
                var current_position = $session.cart[i];
                $reactions.answer(i + 1 + ". " + current_position.name + ", " + current_position.color + "\nЦена: " + current_position.price + "\nКоличество: " + current_position.quantity);
                //$reactions.inlineButtons({text: "Удалить", callback_data: current_position.name});
                $temp.totalSum += current_position.total;
            }

        a: Общая сумма заказа: {{ $temp.totalSum }} рублей.
        a: Если все верно, отправьте свой номер телефона, и наш менеджер с вами свяжется.
        buttons:
            {text: "Отправить номер телефона", request_contact: true}
            "Меню" -> /ChooseFlower
            #"1" -> /GetPhoneNumber
        #todo
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
                go!: /ChooseFlower

            state: ClickButtons
                q: *
                a: Нажмите, пожалуйста, кнопку.
                go!: ..

    state: GetPhoneNumber
        event: telegramSendContact
        script:
            $temp.totalSum = 0;
            //$client.phone_number = '79111111111'
            $client.phone_number = $request.rawRequest.message.contact.phone_number;
            
            var text = "Новый заказ для " + $client.phone_number + ":\n";
            for (var i = 0; i < $session.cart.length; i++) {
                var current_position = $session.cart[i];

                $integration.googleSheets.writeDataToLine(
                    "1571cbcb-0875-4d5d-8462-e0246a60d3b4",
                    "1x8NnGzNDQUroV6hp1BpoiZ9OoK142Ke2wyomqM60De0",
                    "Sheet1",
                    [
                        $client.city,
                        $client.phone_number, 
                        current_position.name,
                        current_position.color,
                        toPrettyString(current_position.quantity),
                        toPrettyString(current_position.total),
                    ]
                );
                
                text += i+1 + ". " + current_position.name + " " + current_position.color  + " " + toPrettyString(current_position.quantity)  + "шт., цена букета "   + toPrettyString(current_position.total) +  " р.;\n";
                $temp.totalSum += current_position.total;
            }
            text += 'Общая сумма заказа ' + $temp.totalSum;
            
            sendOrderToManager(text).then(function (res) {
                if (res && res.ok) {
                    $reactions.answer("Спасибо! Наш менеджер свяжется с вами по номеру телефона " + $client.phone_number)    
                } else {
                    $reactions.answer("Ошибка в отправке сообщения менеджеру");
                }
            }).catch(function (err) {
                $reactions.answer("Ошибка в отправке сообщения менеджеру");
            });