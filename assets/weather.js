function applyWeatherInfo(data, loc) {
    try {
        data = JSON.parse(data);

        /* 현재 날씨 */
        var src = "<tr align=center><td colspan=2 class=title>현재 날씨</td></tr>";
        src += "<tr align=center>";
        src += "<td width=50% class=current><img src='./images/" + data.current.icon + "' width=60%><br>" + data.current.status + "</td>";
        src += "<td width=50% class=current><img src='./images/loc.png' width=60%><br>" + loc + "</td>";
        src += "</tr>";
        src += "<tr align=center>";
        src += "<td width=50% class=current><img src='./images/temp.png' width=60%><br>온도 " + data.current.temp + "</td>";
        src += "<td width=50% class=current><img src='./images/temp_wind.png' width=60%><br>체감 " + data.current.temp_wind + "</td>";
        src += "</tr>";
        src += "<tr align=center>";
        src += "<td width=50% class=current><img src='./images/hum.png' width=60%><br>습도 " + data.current.hum + "</td>";
        src += "<td width=50% class=current><img src='./images/wind.png' width=60%><br>" + data.current.wind_dir + ", " + data.current.wind_speed + "</td>";
        src += "</tr>";
        document.getElementById('current').innerHTML = src.replace(/℃/g, "°C"); //℃에 대응되는 폰트가 없음

        /* 주간 날씨 */
        data = data.weekly;
        var src = "<tr align=center><td colspan=3 class=title>주간 날씨</td></tr>";
        for (var n = 0; n < data.length; n++) {
            src += "<tr align=center>";
            src += "<td width=30%>" + data[n].date + "</td>";
            src += "<td>" + data[n].temp_min + " ~ " + data[n].temp_max + "</td>";
            src += "<td width=20%><img src='./images/" + data[n].icon + "' width=60%></td>";
            src += "</tr>";
            document.getElementById('weekly').innerHTML = src.replace(/℃/g, "°C");
        }
    } catch (e) {
        alert(e);
    }
}