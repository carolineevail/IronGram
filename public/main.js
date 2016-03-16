function getPhotos(photosData) {
    $("#photos").empty();
    for (var i in photosData) {
        var elem = $("<img>");
        elem.attr("src", photosData[i].filename);
        $("#photos").append(elem);
    }
}

function getUser(userData) {
    if (userData.length == 0) {
        $("#login").show();
    }
    else {
        $("#upload").show();
        $.get("/photos", getPhotos);
    }
}

function loadPage() {
    $.get("/user", getUser);

}

setInterval(loadPage, 1000);
