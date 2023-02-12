require: data/db.csv
    name = db
    var = db

theme: /
    
    state: chooseFlower
        a: Какbt цветы хотели бы заказать?
        script:
            for (var i = 1; i < Object.keys(db).length + 1; i++) {
                var region = db[i].value.region;
                if (_.contains(regions, $client.city)) {
                    var button_name = db[i].value.title;
                    $reactions.buttons({text: button_name, transition: 'getFlower'})
                }
            }
            
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
                    var colors = db[i].values.color;
                    for (var j = 0; j < colors.length; j++) {
                        var button_name = colors[j].name + " " + colors[j].price + "руб. / шт.";
                        $reactions.inlineButtons({text: button_name, callback_data: colors[i].id})
                    }
                }
            }
        a: Для возврата в меню, нажмите "Назад"
        buttons:
            "Меню" -> /сhooseFlower
            
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
        a: Выберите количество цветов или укажите свой вариант цифрой
        "3" -> ./getQuantity
        "7" -> ./getQuantity
        "21" -> ./getQuantity
            
        state: getQuantity
            script:
                $session.quantity = parseInt($request.query);
                $session.cart.push({name: $session.flower, id: $session.flower_id, quantity: $session.quantity});
            a: Добавив ещё позицию или оформляем?
            buttons:
                "Меню" -> /chooseFlower
                
            state: clickPlease
            q: *
            a: Пожалуйста, используйте кнопки для выбора
            go!: ..
        
        state: clickPlease
            q: *
            a: Пожалуйста, используйте кнопки для выбора
            go!: ..