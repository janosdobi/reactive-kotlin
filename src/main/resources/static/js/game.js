$(window).on("load", function () {
    const longUrl = window.location.href
    let shortURL = longUrl.slice(0, longUrl.indexOf("?token="));
    window.history.replaceState({}, null, shortURL);
    listenToEvents();
});


function listenToEvents() {
    const token = sessionStorage.getItem("auth");
    const eventSource = new EventSource("/game/v1/events?token=" + token);
    eventSource.onopen = () => console.log("Connection opened!");

    eventSource.onmessage = event => {
        let data = JSON.parse(event.data);
        console.log(data);
    };
}