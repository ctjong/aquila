module.exports = {
    dependencies: [],
    Instance: function(){
        var _this = this;

        //----------------------------------------------
        // CONSTRUCTOR
        //----------------------------------------------

        function _construct(){}

        //----------------------------------------------
        // PUBLIC
        //----------------------------------------------

        this.lowercaseKeys = function(obj){
            var newObj = {};
            for(var key in obj){
                if(!obj.hasOwnProperty(key)) continue;
                newObj[key.toLowerCase()] = obj[key];
            }
            return newObj;
        };

        _construct();
    }
};