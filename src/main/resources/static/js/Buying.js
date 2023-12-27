setInterval(function () {
  $.ajax({
    url: 'buyingAjax',
    type: 'POST',
    data: "",
    dataType: "json",
    contentType: 'application/json',
  })
  .done(function(assetsDtoList) {
    window.myLine.destroy();
    var ctx = document.getElementById("graph-area").getContext("2d");
    window.myLine = new Chart(ctx, createLineGraphDate(assetsDtoList));
  })
  .fail(function() {
    alert("error!");  // 通信に失敗した場合の処理
  })

  $.ajax({
    url: 'buyingUserAjax',
    type: 'POST',
    data: "",
    dataType: "json",
    contentType: 'application/json',
  })
  .done(function(userDto) {
    document.getElementById("money").innerText = userDto.money;
  })
  .fail(function() {
    alert("error!");  // 通信に失敗した場合の処理
  })

  $.ajax({
    url: 'buyingTableAjax',
    type: 'POST',
    data: "",
    dataType: "json",
    contentType: 'application/json',
  })
  .done(function(buyList) {
    $('#sellInvest > option').remove();
    let i = 0;
    for (i = 0; i < buyList.length; i++) {
      $('#sellInvest').append($('<option>').html(buyList[i].investName).val(buyList[i].investId));
    }

    $('#buyTable').find("tr:gt(0)").remove();
    for (i = 0; i < buyList.length; i++) {
      let trTag = $("<tr/>");
      trTag.append($("<td></td>").text(decodeURI(buyList[i].investName)));
      trTag.append($("<td></td>").text(decodeURI(buyList[i].quantity)));
      trTag.append($("<td></td>").text(decodeURI(buyList[i].buyPrice)));
      $('#buyTable').append(trTag);
    }
  })
  .fail(function() {
    alert("error!");  // 通信に失敗した場合の処理
  })
}, 5000);

function createLineGraphDate(assetsDtoList) {
  var assetsPriceList = assetsDtoList.map(function( value ) {
    return value.price;
  });

  var assetsNameList = assetsDtoList.map(function() {
    return "";
  });

  // ▼グラフの中身
  var lineChartData = {
    type: 'line',
    data: {
      labels : assetsNameList,
      datasets : [
        {
            label: "資産",
            backgroundColor : "rgba(60,190,20,0.3)", // 線から下端までを塗りつぶす色
            borderColor : "rgba(92,220,92,1)", // 折れ線の色
            pointColor : "rgba(92,220,92,1)",  // ドットの塗りつぶし色
            pointStrokeColor : "white",        // ドットの枠線色
            pointHoverBackgroundColor : "rgba(92,220,92,1)",     // マウスが載った際のドットの塗りつぶし色
            pointHoverBorderColor : "green",    // マウスが載った際のドットの枠線色
            data : assetsPriceList       // 各点の値
        }
      ]
    },
    options: {
    }
 }
 return lineChartData;
}

function selected(options, selectValue) {
  for (let option of options) {
    if (option.value === selectValue) option.selected = true;
  }
}

function action(params) {
  const form = document.createElement('form');
  form.method = 'POST';
  form.action = 'auto';

  const hiddenField = document.createElement('input');
  hiddenField.type = 'hidden';
  hiddenField.name = 'auto';
  hiddenField.value = params;

  form.appendChild(hiddenField);
  document.body.appendChild(form);
  form.submit();
}

document.addEventListener('DOMContentLoaded', function(){
	var elem = document.getElementById('auto');
	elem.addEventListener('change', function(){
		var value = elem.value;
		action(value);
	}, false);
}, false);