setInterval(function () {
  var investId = document.getElementById("investId").value;
  var data = {
    investId: investId,
  }

  $.ajax({
    url: 'detailGraphAjax',
    type: 'POST',
    data: JSON.stringify(data),
    dataType: "json",
    contentType: 'application/json',
  })
  .done(function(investLogDtoList) {
    window.myLine.destroy();
    var ctx = document.getElementById("graph-area").getContext("2d");
    window.myLine = new Chart(ctx, createLineGraphDate(investLogDtoList));
  })
  .fail(function() {
    alert("error!");  // 通信に失敗した場合の処理
  })

  $.ajax({
    url: 'detailTableAjax',
    type: 'POST',
    data: JSON.stringify(data),
    dataType: "json",
    contentType: 'application/json',
  })
  .done(function(investDto) {
    document.getElementById("maxPrice").innerText = investDto.maxPrice;
    document.getElementById("minPrice").innerText = investDto.minPrice;
    document.getElementById("price").innerText = investDto.price;
    document.getElementById("crash").innerText = investDto.crash;
    document.getElementById("condit").innerText = investDto.condit;
  })
  .fail(function() {
    alert("error!");  // 通信に失敗した場合の処理
  })
}, 5000);

function createLineGraphDate(investLogDtoList) {
    var investName = investLogDtoList[0].investName;
    var investPriceList = investLogDtoList.map(function( value ) {
      return value.price;
    });
  
    var investNameList = investLogDtoList.map(function() {
      return "";
    });
  
    // ▼グラフの中身
    var lineChartData = {
      type: 'line',
      data: {
        labels : investNameList,
        datasets : [
          {
              label: investName,
              backgroundColor : "rgba(60,190,20,0.3)", // 線から下端までを塗りつぶす色
              borderColor : "rgba(92,220,92,1)", // 折れ線の色
              pointColor : "rgba(92,220,92,1)",  // ドットの塗りつぶし色
              pointStrokeColor : "white",        // ドットの枠線色
              pointHoverBackgroundColor : "rgba(92,220,92,1)",     // マウスが載った際のドットの塗りつぶし色
              pointHoverBorderColor : "green",    // マウスが載った際のドットの枠線色
              data : investPriceList       // 各点の値
          }
        ]
      },
      options: {
      }
   }
   return lineChartData;
  }