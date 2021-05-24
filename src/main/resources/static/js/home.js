$(window).on("load", function () {
    const longUrl = window.location.href
    const shortURL = longUrl.slice(0, longUrl.indexOf("?token="));
    window.history.replaceState({}, null, shortURL);
});

function newPlayer() {
    sessionStorage.setItem('player', $("#playerName").val());
    $("#home").html("<div class=\"form-group\">\n" +
        "                    <div class=\"text-center\">\n" +
        "                        <button type=\"submit\" class=\"btn btn-light btn-login\" onclick=\"newGame()\">start new game\n" +
        "                        </button>\n" +
        "                    </div>\n" +
        "                </div>\n" +
        "                <div class=\"form-group\">\n" +
        "                    <div class=\"text-center\">\n" +
        "                            <div class=\"form-group\">\n" +
        "                                <button type=\"submit\" class=\"btn btn-light btn-login\" onclick=\"joinGame()\">join existing game with code:\n" +
        "                                </button>\n" +
        "                            </div>\n" +
        "                            <div class=\"form-group\">\n" +
        "                                <input type=\"text\" class=\"form-control\" placeholder=\"put the code here\" id=\"gameCode\"/>\n" +
        "                            </div>\n" +
        "                    </div>\n" +
        "                </div>")
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
            sessionStorage.setItem("gameCode", response.code);
            window.location.replace("game/" + response.code + "?token=" + token);
        }
    });
}


$("#joinGameForm").submit(function (event) {
    event.preventDefault();

    const formEl = $(this);
    const formData = getFormData(formEl)
    const submitButton = $('input[type=submit]', formEl);

    $.ajax({
        type: 'POST',
        url: formEl.prop('action'),
        headers: {
            "Authorization": sessionStorage.getItem('auth'),
            "Content-Type": "application/json"
        },
        accept: {
            javascript: 'application/javascript'
        },
        data: JSON.stringify(formData),
        beforeSend: function () {
            submitButton.prop('disabled', 'disabled');
        }
    }).done(function (response) {
        submitButton.prop('disabled', false);
        window.location.replace("/game/" + response.gameCode);
    });
});


function getFormData($form) {
    var unindexed_array = $form.serializeArray();
    var indexed_array = {};

    $.map(unindexed_array, function (n, i) {
        indexed_array[n['name']] = n['value'];
    });

    return indexed_array;
}

