$(window).on("load", function () {
    const longUrl = window.location.href
    let shortURL = longUrl.slice(0, longUrl.indexOf("?token="));
    window.history.replaceState({}, null, shortURL);
    setupPage();
    listenToEvents();
});

function setupPage() {
    //set player name in welcome tag
    const player = sessionStorage.getItem("player");
    if (player != null) {
        $('#welcome').text(`Welcome ${player}!`);
    }

    //present players who already joined
    getPlayersAlreadyJoined(sessionStorage.getItem("gameCode"));
}

function getPlayersAlreadyJoined(gameCode) {
    const token = sessionStorage.getItem('auth');
    const actualPlayer = sessionStorage.getItem('player')
    const url = window.location.href;
    const cleanUrl = url.slice(0, url.indexOf('/game/'));
    $.get({
        url: cleanUrl + "/api/v1/players/" + gameCode,
        headers: {
            "Authorization": "Bearer " + token,
            "Content-Type": "application/json"
        },
        success: function (response) {
            if (response != null && response.length > 0) {
                response
                    .filter(player => actualPlayer !== player.name)
                    .forEach(player => $("#playersBeforeStart").append(
                        `<li class="p text-center text-light" id="${player.name.replace(" ", "_")}">${player.name}</li>`
                    ));
            }
        }
    });
}

function listenToEvents() {
    const token = sessionStorage.getItem("auth");
    const gameCode = sessionStorage.getItem("gameCode")
    const eventSource = new EventSource("/game/v1/events?gameCode=" + gameCode + "&token=" + token);

    eventSource.onmessage = event => {
        const data = JSON.parse(event.data);
        handleEvent(data);
    };
}

function handleEvent(data) {
    const eventType = data.eventType;
    console.log("Event received: " + eventType)

    switch (eventType) {
        case 'PlayerJoinedEvent':
            handlePlayerJoined(data);
            break;
        case 'PlayerLeftEvent':
            handlePlayerLeft(data);
            break;
        case 'GameStartedEvent':
            handleGameStarted(data);
            break;
        case 'RoundStartedEvent':
            handleRoundStarted(data);
            break;
        case 'RoundFinishedEvent':
            handleRoundFinished(data);
            break;
    }

    function handlePlayerJoined(data) {
        const sanitizedName = data.player.name.replace(" ", "_")
        $("#playersBeforeStart").append(`<li class="p text-center text-light" id="${sanitizedName}">${data.player.name}</li>`);
    }

    function handlePlayerLeft(data) {
        const sanitizedName = data.player.name.replace(" ", "_")
        $(`#${sanitizedName}`).remove();
    }

    function generatePlayerScoreTable(data) {
        return data.game.players.map(player => `<tr>
                                                    <td>${player.name}<td>
                                                    <td>${player.score}</td>
                                                </tr>`);
    }

    function handleGameStarted(data) {
        $("#game").html(`<table id="playerScoreTable" class="text-light">
                    <tr>
                        <th>Player</th>
                        <th>Score</th>
                    </tr>
                    ${generatePlayerScoreTable(data)}
                </table>`)
    }

    function handleRoundStarted(data) {
        $("#round").html(`<p class="h2 text-center text-light">Round ${data.roundNumber}</p>
                                <p class="d-inline p text-center text-light float-right">Time left: <span id="timer">${data.roundLength}</span></p>`
        )
        const timerElement = $("#timer");
        let timeLeft = data.roundLength
        let timer = setInterval(function () {
            if (timeLeft <= 0) {
                clearInterval(timer);
            }
            timeLeft -= 1;
            timerElement.text(timeLeft);
        }, 1000);
    }

    function handleRoundFinished(data) {
        if (data.roundNumber < data.totalRounds) {
            $('#round').html(`<p class="h2 text-center text-light">Round ${data.roundNumber} finished!</p>
                                <button id="nextRound" type="submit" class="btn btn-light btn-login" onclick="nextRound()">next round</button>`);
        } else {
            $('#round').html(`<p class="h2 text-center text-light">Game over!</p>`);
        }
    }
}

function startGame() {
    const url = window.location.href;
    const cleanUrl = url.slice(0, url.indexOf('/game/'));
    const token = sessionStorage.getItem('auth');
    const numberOfRounds = $("#noRounds").val();
    const lengthOfRounds = $("#lengthRounds").val();
    const playerName = sessionStorage.getItem('player');
    //TODO remove this horrible hack
    $.get({
        url: `https://opentdb.com/api.php?amount=${numberOfRounds}&type=multiple&encode=base64`,
        success: function (response) {
            const requestBody = {
                gameCode: sessionStorage.getItem('gameCode'),
                numberOfRounds: numberOfRounds,
                lengthOfRounds: lengthOfRounds,
                playerName: playerName,
                questions: response.results
            }
            if (numberOfRounds >= 3 && lengthOfRounds >= 1) {
                $.post({
                    url: cleanUrl + "/api/v1/start/",
                    headers: {
                        "Authorization": "Bearer " + token,
                        "Content-Type": "application/json"
                    },
                    data: JSON.stringify(requestBody)
                });
            } else {
                alert("Please select number & length of rounds!")
            }
        }
    });
}

function quitGame() {
    const url = window.location.href;
    const cleanUrl = url.slice(0, url.indexOf('/game/'));
    const token = sessionStorage.getItem('auth');
    const requestBody = {
        playerName: sessionStorage.getItem('player'),
        gameCode: sessionStorage.getItem('gameCode')
    }
    $.post({
        url: cleanUrl + "/api/v1/quit/",
        headers: {
            "Authorization": "Bearer " + token,
            "Content-Type": "application/json"
        },
        data: JSON.stringify(requestBody),
        success: function () {
            window.close();
        }
    });
}

function nextRound() {
    const url = window.location.href;
    const cleanUrl = url.slice(0, url.indexOf('/game/'));
    const token = sessionStorage.getItem('auth');
    const requestBody = {
        gameCode: sessionStorage.getItem('gameCode')
    }
    $.post({
        url: cleanUrl + "/api/v1/next-round/",
        headers: {
            "Authorization": "Bearer " + token,
            "Content-Type": "application/json"
        },
        data: JSON.stringify(requestBody)
    });
}
