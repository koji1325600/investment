/** グラフ・テーブルの非同期定期更新 */
setInterval(function () {
  var investName1 = document.getElementById("investName1").value;
  var investName2 = document.getElementById("investName2").value;
  var graphType = document.getElementById("graphType").value;
  var data = {};

  if (graphType == "line") {
    $.ajax({
      url: 'homeLineGraphAjax',
      type: 'POST',
      data: JSON.stringify(data),
      dataType: "json",
      contentType: 'application/json',
    })
    .done(function(investLogDtoList) {
      window.myLine.destroy();
      var ctx = document.getElementById("graph-area").getContext("2d");
      window.myLine = new Chart(ctx, createLineGraphDate(investLogDtoList, investName1, investName2));
    })
    .fail(function() {
      alert("error!");  // 通信に失敗した場合の処理
    })
  } else {
    $.ajax({
      url: 'homePieGraphAjax',
      type: 'POST',
      data: JSON.stringify(data),
      dataType: "json",
      contentType: 'application/json',
    })
    .done(function(investmentList) {
      window.myLine.destroy();
      var ctx = document.getElementById("graph-area").getContext("2d");
      window.myLine = new Chart(ctx, createPieGraphDate(investmentList));
    })
    .fail(function() {
      alert("error!");  // 通信に失敗した場合の処理
    })
  }

  $.ajax({
    url: 'homeTableAjax',
    type: 'POST',
    data: JSON.stringify(data),
    dataType: "json",
    contentType: 'application/json',
  })
  .done(function(investmentList) {
    $('#investTable').find("tr:gt(0)").remove();
    let i = 0;
    for (i = 0; i < investmentList.length; i++) {
      let trTag = $("<tr class=\"tr\"/>");
      trTag.append($("<td onclick=\"action('" + investmentList[i].id + "')\"></td>").text(decodeURI(investmentList[i].name)));
      trTag.append($("<td onclick=\"action('" + investmentList[i].id + "')\"></td>").text(decodeURI(investmentList[i].price)));
      $('#investTable').append(trTag);
    }
  })
  .fail(function() {
    alert("error!");  // 通信に失敗した場合の処理
  })
}, 5000);

/** 取引詳細画面への遷移アクション */
function action(params) {
  const form = document.createElement('form');
  form.method = 'get';
  form.action = 'detail';

  const hiddenField = document.createElement('input');
  hiddenField.type = 'hidden';
  hiddenField.name = 'id';
  hiddenField.value = params;

  form.appendChild(hiddenField);
  document.body.appendChild(form);
  form.submit();
}

/** 折れ線グラフを作成 */
function createLineGraphDate(investLogDtoList, investName1, investName2) {
  var averageLogList = investLogDtoList.filter(investLog => {
    return investLog.investName == "アベレージ";
  });
  var logList1 = investLogDtoList.filter(investLog => {
    return investLog.investName == investName1;
  });
  var logList2 = investLogDtoList.filter(investLog => {
    return investLog.investName == investName2;
  });

  var averagePriceList = averageLogList.map(function( value ) {
    return value.price;
  });
  var priceList1 = logList1.map(function( value ) {
    return value.price;
  });
  var priceList2 = logList2.map(function( value ) {
    return value.price;
  });

  var averageNameList = averageLogList.map(function() {
    return "";
  });

  // ▼グラフの中身
  var lineChartData = {
    type: 'line',
    data: {
      labels : averageNameList,
      datasets : [
        {
            label: "アベレージ",
            backgroundColor : "rgba(60,190,20,0.3)", // 線から下端までを塗りつぶす色
            borderColor : "rgba(92,220,92,1)", // 折れ線の色
            pointColor : "rgba(92,220,92,1)",  // ドットの塗りつぶし色
            pointStrokeColor : "white",        // ドットの枠線色
            pointHoverBackgroundColor : "rgba(92,220,92,1)",     // マウスが載った際のドットの塗りつぶし色
            pointHoverBorderColor : "green",    // マウスが載った際のドットの枠線色
            data : averagePriceList       // 各点の値
        },
        {
            label: investName1,
            backgroundColor : "rgba(60,160,220,0.3)",
            borderColor : "rgba(60,160,220,1)",
            pointColor : "rgba(60,160,220,1)",
            pointStrokeColor : "white",
            pointHoverBackgroundColor : "rgba(60,160,220,1)",
            pointHighlightStroke : "blue",
            data : priceList1
        },
        {
            label: investName2,
            backgroundColor : "rgba(255,29,58,0.3)",
            borderColor : "rgba(220,60,60,1)",
            pointColor : "rgba(220,60,60,1)",
            pointStrokeColor : "white",
            pointHoverBackgroundColor : "rgba(220,60,60,1)",
            pointHighlightStroke : "red",
            data : priceList2
        }
      ]
    },
    options: {
    }
 }
 return lineChartData;
}

/** 円グラフを作成 */
function createPieGraphDate(investmentList) {
  var investList = investmentList.filter(invest => {
    return invest.name != "アベレージ";
  });

  var investNameList = investList.map(function( value ) {
    return value.name;
  });

  var investPriceList = investList.map(function( value ) {
    return value.price;
  });
  // ▼グラフの中身
  var pieChartData = {
    type: 'pie',
    data: {
      labels: investNameList,
      datasets: [{
        data: investPriceList,
        backgroundColor: ['#FF5192', '#FF5F17', '#7B3CFF','#2C7CFF','#32EEFF','#C9FF2F','#30F9B2','#2DFF57','#808080'],
        weight: 100,
      }],
    }
  }
  return pieChartData;
}