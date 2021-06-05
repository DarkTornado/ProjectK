drawStations = (data) => {
    data = data.split("\n");
    var map = document.getElementById('stations');
    for (var n = 0; n < data.length; n++) {
        if (data[n].startsWith("/")) continue;
        var info = data[n].trim().split(",");
        map.innerHTML += '<circle cx="' + info[0] + '" cy="' + info[1] + '" r="16" fill="#EEEEEE" opacity="0" onclick="window.android.stationInfo(\'' + info[2] + '\', 3);" />';
    }
}

loadStations = (file) => {
    var rawFile = new XMLHttpRequest();
    rawFile.open("GET", "./csv/" + file + ".csv", false);
    rawFile.onreadystatechange = () => {
        if (rawFile.readyState === 4) {
            if (rawFile.status === 200 || rawFile.status == 0) {
                var allText = rawFile.responseText;
                drawStations(allText);
            }
        }
    }
    rawFile.send(null);
}