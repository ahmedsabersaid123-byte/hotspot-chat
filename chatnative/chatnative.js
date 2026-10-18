var exec = require('cordova/exec');
module.exports = {
  call: function (action, args, ok, err) {
    exec(ok || function () {}, err || function () {}, 'ChatNative', action, args || []);
  }
};
