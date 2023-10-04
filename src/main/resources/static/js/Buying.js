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