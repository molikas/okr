            function objectToArray(obj) {
                return Object.keys(obj).map(function (key) {
                  obj[key].id = key;
                  return obj[key];
                });
            }