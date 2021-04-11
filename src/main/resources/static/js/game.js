$(window).on("load", function () {
    const longUrl = window.location.href
    const shortURL = longUrl.slice(0, longUrl.indexOf("?token="));
    console.log(shortURL)
    window.history.replaceState({}, null, shortURL);
});