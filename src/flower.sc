require: data/db.csv
    name = db
    var = db

theme: /
    
    state: chooseFlower
        a: Какие цветы хотели бы заказать?
        script:
            for (var i = 1; i < Object.keys(db).length + 1; i++) {
                var region = db[i].value.region;
                if (_.contains(region, $client.city)) {
                    var button_name = db[i].value.title;
                    $reactions.buttons({text: button_name, transition: 'getFlower'})
                }
            }
        buttons:
            "Назад" -> ../chooseCity
            
        state: getFlower
            script:
                $session.flower = $request.query;
            go!: /chooseColor
            
        state: clickPlease
            q: *
            a: Пожалуйста, используйте кнопки для выбора
            go!: ..
            
    state: chooseColor
        a: Выберете наиболее подходящий вариант:
        script:
            for (var i = 1; i < Object.keys(db).length + 1; i++) {
                if ($session.flower == db[i].value.title) {
                    var colors = db[i].value.colors;
                    for (var j = 0; j < colors.length; j++) {
                        var button_name = colors[j].name + " " + colors[j].price + " руб. / шт.";
                        $reactions.inlineButtons({
                            text: button_name, 
                            callback_data: colors[j].id
                            })
                    }
                }
            }
        buttons:
            "Назад" -> ../chooseFlower
            
        state: clickPlease
            q: *
            a: Пожалуйста, используйте кнопки для выбора
            go!: ..
            
    state: getColor
        event: telegramCallbackQuery
        script:
            $session.flower_id = parseInt($request.query)
        go!: /chooseQuantity
            
    state: chooseQuantity
        InputNumber:
            prompt = Укажите значение цифрой, сколько цветов вы хотели бы заказать (от 1 до 101)
            failureMessage = ["Не могли бы вы попробовать еще раз?", "Пожалуйста, введите число в диапазоне 2–101."]
            minValue = 1
            maxValue = 101
            varName = quantity
            then = /chooseQuantity/getQuantity
            
        state: getQuantity
            script:
                $session.quantity = parseInt($session.quantity);
                $session.cart.push({
                    name: $session.flower, 
                    id: $session.flower_id, 
                    quantity: $session.quantity,
                });
            a: Добавим ещё позицию или оформляем?
            buttons:
                "Меню" -> /chooseFlower
            buttons:
                "Оформить заказ" -> /Cart
                
            state: clickPlease
            q: *
            a: Пожалуйста, используйте кнопки
            go!: ..
