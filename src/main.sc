theme: /

    state: Echo
        event!: noMatch
        a: Вы сказали: {{$parseTree.text}}

    state: Match
        event!: match
        a: {{$context.intent.answer}}