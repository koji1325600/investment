setTimeout(function () {
    const form = document.createElement('form');
    form.method = 'get';
    form.action = 'detail';
  
    const params = document.getElementById("investId").value;
    const hiddenField = document.createElement('input');
    hiddenField.type = 'hidden';
    hiddenField.name = 'id';
    hiddenField.value = params;
  
    form.appendChild(hiddenField);
    document.body.appendChild(form);
    form.submit();
  }, 5000);
function createLineGraphDate(investLogDaoList) {
    var investName = investLogDaoList[0].investName;
    var investPriceList = investLogDaoList.map(function( value ) {
      return value.price;
    });
  
    var investNameList = investLogDaoList.map(function() {
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