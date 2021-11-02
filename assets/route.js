function applyRoute(json) {
    var data = JSON.parse(json);
    data0 = data.res.paths;
    var src = '';
    for (var n = 0; n < data0.length; n++) {
        var data = data0[n];
        var time = data.duration;
        var dist = data.distance;
        var fee = data.fare;
        data = data.legs[0].steps;
        src += '<div class=pad>';
        src += '<table width=100% border=1>';
        src += '<tr>';
        src += '<td class=title colspan=' + data.length + '>';
        src += '<b><big>' + time + '분, ' + (dist / 1000) + 'km</big></b>';
        src += '</td>';
        src += '</tr>';
        src += '<tr align=center>';
        var src2 = '<table class=info width=100% border=1>';
        for (var m = 0; m < data.length; m++) {
            src2 += '<tr>';
            var width = Math.floor((data[m].duration / time) * 100);
            var route = data[m].route;
            if (route == null) {
                src += '<td nowrap class=route_blank width=' + width + '% bgcolor=#E0E0E0>' + data[m].duration + '분</td>';
            } else {
                if (data[m].type == 'BUS') route.name += '번';
                src += '<td nowrap class=route width=' + width + '% bgcolor=' + route.type.color + '>' + route.name + ' (' + data[m].duration + '분)</td>';
                src2 += '<td nowrap width=20% align=center><font color=' + route.type.color + '><b>' + route.name + '</b></font></td>';
                src2 += '<td>' + data[m].stations[0].name + '</td>';
                if (route.arrival && route.arrival.items.length > 0) {
                    src2 += '</tr><tr><td></td>';
                    var sec = route.arrival.items[0].remainingTime;
                    var min = parseInt(sec / 60);
                    sec = sec % 60;
                    var tym = (min > 0 ? min + '분 ' : '') + sec + '초';
                    info = route.arrival.items[0].remainingStop + '정류장 전, ' + tym + ' 남음';
                    if (route.arrival.items[0].remainingSeat) info += ', ' + route.arrival.items[0].remainingSeat + '석 남음';
                    src2 += '<td>' + info + '</td>';
                    src2 += '</tr><tr>';
                }
            }
            src2 += '</tr>';
        }
        src2 += '</table>';
        src += '</tr>';
        src += '</table>';
        src += src2;
        src += '</div>';
        src += '<br>';
    }
    document.getElementById('data_table').innerHTML = src;
}