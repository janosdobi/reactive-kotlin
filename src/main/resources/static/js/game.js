$(window).on("load", function () {
    const longUrl = window.location.href
    let shortURL = longUrl.slice(0, longUrl.indexOf("?token="));
    window.history.replaceState({}, null, shortURL);
    setupPage();
    listenToEvents();
});

function setupPage() {
    const player = sessionStorage.getItem("player");
    if (player != null) {
        $('#welcome').text('Welcome ' + player + '!');
    }
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
