$(window).on("load", function () {
    const longUrl = window.location.href
    const shortURL = longUrl.slice(0, longUrl.indexOf("?token="));
    console.log(shortURL)
    window.history.replaceState({}, null, shortURL);
});


function newGame() {
    const token = sessionStorage.getItem('auth');
    $.get({
        url: "api/v1/new-game",
        headers: {"Authorization": "Bearer " + token},
        success: function (response) {
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
        window.location.replace("/game/" + response.gameId);
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

