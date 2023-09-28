setTimeout(function () {
  const form = document.createElement('form');
  form.method = 'get';
  form.action = 'reHome';

  const params1 = document.getElementById("investName1").value;
  const hiddenField1 = document.createElement('input');
  hiddenField1.type = 'hidden';
  hiddenField1.name = 'investName1';
  hiddenField1.value = params1;

  const params2 = document.getElementById("investName2").value;
  const hiddenField2 = document.createElement('input');
  hiddenField2.type = 'hidden';
  hiddenField2.name = 'investName2';
  hiddenField2.value = params2;

  const params3 = document.getElementById("graphType").value;
  const hiddenField3 = document.createElement('input');
  hiddenField3.type = 'hidden';
  hiddenField3.name = 'graphType';
  hiddenField3.value = params3;

  form.appendChild(hiddenField1);
  form.appendChild(hiddenField2);
  form.appendChild(hiddenField3);
  document.body.appendChild(form);
  form.submit();
}, 5000);

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

function createLineGraphDate(investLogDaoList, investName1, investName2) {
  var averageLogList = investLogDaoList.filter(investLog => {
    return investLog.investName == "アベレージ";
  });
  var logList1 = investLogDaoList.filter(investLog => {
    return investLog.investName == investName1;
  });
  var logList2 = investLogDaoList.filter(investLog => {
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

function selected(options, selectValue) {
  for (let option of options) {
    if(option.value === selectValue) option.selected = true;
  }
}