require: flower.sc

theme: /
    
    state: start
        q!: $regex</start>
        script:
            $context.responce = {};
            $context.session = {};
            $context.client = {};
            $context.temp = {};
        a: Привет, я бот для заказа цветов. Уже начинаем оформлять!
        go!: /chooseCity

    state: chooseCity || modal = true
        a: Выберите ваш город:
        buttons:
            "Санкт-Петербург" -> ./rememberCity
            "Москва"  -> ./rememberCity
            
        state: rememberCity
            script:
                $client.city = $request.query
                $session.cart = [];
            go!: /chooseFlower
                
        state: clickPlease
            q: *
            a: Пожалуйста, используйте кнопки для выбора
            go!: ..
    
    state: bye
        intent!: /пока
        a: {{$context.client.city}}

    state: noMatch || noContext = true
        event!: noMatch
        a: Я не понял, простите, вы сказали {{$request.query}}
        go!: ../chooseCity
        
