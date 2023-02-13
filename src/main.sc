require: flower.sc
require: cart.sc

theme: /
    
    state: Start
        q!: $regex</start>
        script:
            $context.responce = {};
            $context.session = {};
            $context.client = {};
            $context.temp = {};
        a: Привет, я бот для заказа цветов.
        go!: /ChooseCity

    state: ChooseCity || modal = true
        a: Выберите ваш город:
        buttons:
            "Москва"  -> ./RememberCity
            "Санкт-Петербург" -> ./RememberCity
            
        state: RememberCity
            script:
                $client.city = $request.query
                $session.cart = [];
            go!: /ChooseFlower
                
        state: ClickPlease
            q: *
            a: Пожалуйста, используйте кнопки для выбора
            go!: ..
    
    state: Bye
        intent!: /пока
        a: {{$context.client.city}}

    state: NoMatch || noContext = true
        event!: noMatch
        a: Я не понял, простите, вы сказали {{$request.query}}
        go!: ../ChooseCity
        
