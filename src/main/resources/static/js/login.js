$("#loginForm").submit(function (event) {
    event.preventDefault();

    const formEl = $(this)
    const input = {
        "username": $("#usernameInput").val(),
        "password": $("#passwordInput").val()
    }
    const submitButton = $('input[type=submit]', formEl);

    $.ajax({
        type: 'POST',
        url: formEl.prop('action'),
        headers: {
            "Content-Type": "application/json"
        },
        accept: {
            javascript: 'application/javascript'
        },
        data: JSON.stringify(input),
        beforeSend: function () {
            submitButton.prop('disabled', 'disabled');
        },
        success: function (response) {
            const token = response['token'];
            sessionStorage.setItem('auth', token)
            window.location.replace("home?token=" + token)
        },
        error: () => alert("Fuck off!")
    });
});

