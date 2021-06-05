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
}

function startGame() {
    const url = window.location.href;
    const cleanUrl = url.slice(0, url.indexOf('/game/'));
    const token = sessionStorage.getItem('auth');
    const numberOfRounds = $("#noRounds").val()
    const lengthOfRounds = $("#lengthRounds").val()
    const playerName = sessionStorage.getItem('player')
    const requestBody = {
        gameCode: sessionStorage.getItem('gameCode'),
        numberOfRounds: numberOfRounds,
        lengthOfRounds: lengthOfRounds,
        playerName: playerName
    }
    if (numberOfRounds >= 3 && lengthOfRounds >= 60) {
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

function finishGame() {
    const url = window.location.href;
    const cleanUrl = url.slice(0, url.indexOf('/game/'));
    const token = sessionStorage.getItem('auth');
    const requestBody = {
        gameCode: sessionStorage.getItem('gameCode')
    }
    $.post({
        url: cleanUrl + "/api/v1/finish/",
        headers: {
            "Authorization": "Bearer " + token,
            "Content-Type": "application/json"
        },
        data: JSON.stringify(requestBody)
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
