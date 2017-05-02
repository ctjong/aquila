//----------------------------------------------
// INITIALIZATION
//----------------------------------------------

// app objects
var app = new (require('express'))();
var modules = new (require('./modules/moduleCollection'))();
var contextFactory = require('./modules/context');

// modules
modules.add("body-parser", 'body-parser');
modules.add("sql", 'mssql');
modules.add("captcha", 'svg-captcha');
modules.add("crypto", 'crypto');
modules.add("azure", 'azure-storage');
modules.add("guid", 'guid');
modules.add("multiparty", 'multiparty');
modules.add("mime", 'mime-types');
modules.add("jwt", 'jsonwebtoken');
modules.addDef("create", './modules/handlers/create');
modules.addDef("read", './modules/handlers/read');
modules.addDef("update", './modules/handlers/update');
modules.addDef("delete", './modules/handlers/delete');
modules.addDef("createAsset", './modules/handlers/createAsset');
modules.addDef("deleteAsset", './modules/handlers/deleteAsset');
modules.addDef("helper", './modules/services/helper');
modules.addDef("auth", './modules/services/auth');
modules.addDef("query", './modules/services/query');
modules.addDef("condition", './modules/services/condition');
modules.addDef("exec", './modules/services/exec');

// display current route
app.use("", function (req, res, next) {
    console.log("===============================================================");
    console.log(req.method + " " + req.originalUrl);
    console.log("===============================================================");
    next();
});

// root endpoint
app.get('/', function (req, res) {
    res.status(400).send("400: invalid endpoint");
});


//----------------------------------------------
// DATA ENDPOINTS
//----------------------------------------------

app.use('/data/:entity/:accessType', modules.get("body-parser").json());
app.use('/data/:entity/:accessType', modules.get("body-parser").urlencoded({ extended: true }));
app.use('/data/:entity/:accessType', function (req, res, next) {
    req.context = new contextFactory.Context(req, res, req.params.entity, req.params.accessType);
    next();
});

/* 
    ASSET ENDPOINTS 
*/

// GET public & private
app.get('/data/asset/:accessType', function(req, res, next){
    res.status(400).send("Bad request");
});

// POST public
app.post('/data/asset/public', function(req, res){
    modules.get("auth").authorize(req.context, "create", null, function(){
        modules.get("createAsset").execute(req.context, req);
    });
});

// PUT public & private
app.put('/data/asset/:accessType', function(req, res, next){
    res.status(400).send("Bad request");
});

// DELETE public
app.delete('/data/asset/public/:id', function(req, res){
    modules.get("auth").authorize(req.context, "delete", req.params.id, function(){
        modules.get("deleteAsset").execute(req.context, decodeURIComponent(req.params.id));
    });
});

// DELETE private
app.delete('/data/asset/private/:id', function(req, res){
    res.status(400).send("Bad request");
});


/* 
    NON-ASSET ENDPOINTS 
*/

// GET public & private
app.get('/data/:entity/:accessType/findbyid/:id', function (req, res) {
    modules.get("auth").authorize(req.context, "read", null, function(){
        modules.get("read").execute(req.context, req.params, true);
    });
});
app.get('/data/:entity/:accessType/findbyconditions/:orderByField/:skip/:take/:conditions', function (req, res) {
    modules.get("auth").authorize(req.context, "read", null, function(){
        modules.get("read").execute(req.context, req.params, false);
    });
});
app.get('/data/:entity/:accessType/findall/:orderByField/:skip/:take', function (req, res) {
    modules.get("auth").authorize(req.context, "read", null, function(){
        modules.get("read").execute(req.context, req.params, false);
    });
});

// POST public
app.post('/data/:entity/public', function(req, res){
    modules.get("auth").authorize(req.context, "create", null, function(){
        modules.get("create").execute(req.context, req.body);
    });
});

// PUT public & private
app.put('/data/:entity/:accessType/:id', function(req, res){
    modules.get("auth").authorize(req.context, "update", req.params.id, function(){
        modules.get("update").execute(req.context, req.body, req.params.id);
    });
});

// DELETE public & private
app.delete('/data/:entity/:accessType/:id', function(req, res){
    modules.get("auth").authorize(req.context, "delete", req.params.id, function(){
        modules.get("delete").execute(req.context, req.params.id);
    });
});


//----------------------------------------------
// AUTH ENDPOINTS
//----------------------------------------------

app.use('/auth', modules.get("body-parser").json());
app.use('/auth', function(req, res, next){
    req.context = new contextFactory.Context(req, res, "user", "public");
    next();
});

app.post('/auth/token', function(req, res){
    modules.get("auth").generateLocalUserToken(req.context, req.body.username, req.body.password);
});

app.post('/auth/token/fbsl', function(req, res){
    modules.get("auth").processFbslToken(req.context, req.body.fbtoken);
});

app.post('/auth/token/fbll', function(req, res){
    modules.get("auth").processFbllToken(req.context, req.body.fbtoken);
});


//----------------------------------------------
// OTHER ENDPOINTS
//----------------------------------------------

// captcha
app.get('/captcha', function(req, res){
    var captcha = modules.get("captcha").create();
    req.session.captchaExpected = captcha.text;
    res.set('Content-Type', 'image/svg+xml');
    res.send(captcha.data);
});

// error handling
app.use(function (err, req, res, next) {
    try{
        modules.get("exec").handleError({res: res}, err);
    }catch(ex){
        console.error(err);
        res.status(500).send(err);
    }
});


//----------------------------------------------
// START
//----------------------------------------------

var port = process.env.PORT || 1337;
var server = app.listen(port, function () {
   var host = server.address().address;
   var port = server.address().port;
   console.log("Listening at http://%s:%s", host, port);
});