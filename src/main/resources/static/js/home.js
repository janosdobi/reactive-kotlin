$(window).on("load", function () {
    const longUrl = window.location.href
    const shortURL = longUrl.slice(0, longUrl.indexOf("?token="));
    window.history.replaceState({}, null, shortURL);
});

function newPlayer() {
    sessionStorage.setItem('player', $("#playerName").val());
    $("#home").html(`<div class="form-group text-center">
                               <div class="form-group text-center">
                                    <button type="submit" class="btn btn-light btn-login" onclick="newGame()">start new game</button>
                               </div>
                               <div class="form-group text-center">
                                   <button type="submit" class="btn btn-light btn-login" id="joinGameBtn" onclick="joinGame()">join existing game with code:</button>
                                   <input type="text" class="form-control" placeholder="code here" id="gameCode"/>
                               </div>
                           </div>`)
}

function newGame() {
    const token = sessionStorage.getItem('auth');
    const playerName = sessionStorage.getItem('player');
    const requestBody = {
        playerName: playerName
    }
    $.post({
        url: "api/v1/new-game",
        headers: {
            "Authorization": "Bearer " + token,
            "Content-Type": "application/json"
        },
        data: JSON.stringify(requestBody),
        success: function (response) {
            sessionStorage.setItem("gameCode", response.code)
            window.location.replace("game/" + response.code + "?token=" + token);
        }
    });
}

function joinGame() {
    const token = sessionStorage.getItem('auth');
    const playerName = sessionStorage.getItem('player');
    const gameCode = $("#gameCode").val()
    const requestBody = {
        gameCode: gameCode,
        playerName: playerName
    }
    $.post({
        url: "api/v1/join-game",
        headers: {
            "Authorization": "Bearer " + token,
            "Content-Type": "application/json"
        },
        data: JSON.stringify(requestBody),
        success: function (response) {
            if (response.success) {
                sessionStorage.setItem("gameCode", response.gameCode);
                window.location.replace("game/" + response.gameCode + "?token=" + token);
            } else {
                $('#errorMessage').html(`<p class="text-center text-light">${response.message}</p>`);
            }
        }
    }).fail(function(response) {
        console.log(response)
        //TODO finetune exception handling
        alert(`Something is wrong, most probably the game code you have provided. Try again`)
    });
}
