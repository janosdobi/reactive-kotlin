$(window).on("load", function () {
    const longUrl = window.location.href
    let shortURL = longUrl.slice(0, longUrl.indexOf("?token="));
    window.history.replaceState({}, null, shortURL);
    setupPage(shortURL);
    listenToEvents();
});

function setupPage(shortUrl) {
    //set player name in welcome tag
    const player = sessionStorage.getItem("player");
    if (player != null) {
        $('#welcome').text('Welcome ' + player + '!');
    }

    //present players who already joined
    getPlayersAlreadyJoined(sessionStorage.getItem("gameCode"), shortUrl);
}

function getPlayersAlreadyJoined(gameCode, url) {
    const token = sessionStorage.getItem('auth');
    const actualPlayer = sessionStorage.getItem('player')
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
                    .forEach(player => $("#players").append('<li class="p text-center text-light">' + player.name + "</li>"));
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

    switch (eventType) {
        case 'PLAYER_JOINED': handlePlayerJoined(data);
    }

    function handlePlayerJoined(data) {
        $("#players").append('<li class="p text-center text-light">' + data.message + "</li>");
    }
}
