setTimeout(function () {
    location.reload();
}, 5000);

function createGraphDate(investLogDaoList) {
  var averageLogList = investLogDaoList.filter(investLog => {
    return investLog.investName == "アベレージ";
  });
  var logList1 = investLogDaoList.filter(investLog => {
    return investLog.investName == "ドリアン";
  });
  var logList2 = investLogDaoList.filter(investLog => {
    return investLog.investName == "メロン";
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

  console.log(averagePriceList);
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
            label: "ドリアン",
            backgroundColor : "rgba(60,160,220,0.3)",
            borderColor : "rgba(60,160,220,1)",
            pointColor : "rgba(60,160,220,1)",
            pointStrokeColor : "white",
            pointHoverBackgroundColor : "rgba(60,160,220,1)",
            pointHighlightStroke : "blue",
            data : priceList1
        },
        {
            label: "メロン",
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
