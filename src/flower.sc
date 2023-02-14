require: data/db.csv
    name = db
    var = db

theme: /
    
    state: ChooseFlower
        a: Какие цветы хотели бы заказать?
        script:
            for (var i = 1; i < Object.keys(db).length + 1; i++) {
                var region = db[i].value.region;
                if (_.contains(region, $client.city)) {
                    var button_name = db[i].value.title;
                    $reactions.buttons({text: button_name, transition: 'GetFlower'})
                }
            }
        buttons:
            "Назад" -> ../ChooseCity
            
        state: GetFlower
            script:
                $session.flower = $request.query;
            go!: /ChooseColor
            
        state: ClickPlease
            q: *
            a: Пожалуйста, используйте кнопки для выбора
            go!: ..
            
    state: ChooseColor
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
            "Назад" -> ../ChooseFlower
            #"1" -> ../GetColor
            
        state: ClickPlease
            q: *
            a: Пожалуйста, используйте кнопки для выбора
            go!: ..
            
    state: GetColor
        event: telegramCallbackQuery
        script:
            $session.flower_id = parseInt($request.query)
            
            for (var id = 1; id < Object.keys(db).length + 1; id++) {
                if ($session.flower == db[id].value.title) {
                    var color = _.find(db[id].value.colors, function(color){
                        return color.id === $session.flower_id;
                    });
                    $session.flower_color = color.name;
                    $session.flower_price = color.price;
                }
            }
        go!: /ChooseQuantity
            
    state: ChooseQuantity
        InputNumber:
            prompt = Укажите значение цифрой, количество цветов вы хотели бы заказать (от 1 до 101)
            failureMessage = "Пожалуйста, введите число в диапазоне 1–101."
            minValue = 1
            maxValue = 101
            varName = quantity
            then = /ChooseQuantity/GetQuantity
            
        state: GetQuantity
            script:
                $session.quantity = parseInt($session.quantity);
                $session.cart.push({
                    id: $session.flower_id,
                    name: $session.flower,
                    color: $session.flower_color,
                    price: $session.flower_price,
                    quantity: $session.quantity,
                    total: $session.quantity * $session.flower_price,
                });
            a: Добавим ещё позицию или оформляем?
            buttons:
                "Меню" -> ../../ChooseFlower
            buttons:
                "Оформить заказ" -> /Cart
