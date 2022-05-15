//交叉索引
db.product.insertMany(
[
{"name":"a","price":60,"stock":10},
{"name":"b","price":100,"stock":2},
{"name":"c","price":20,"stock":8},
{"name":"d","price":40,"stock":7},
{"name":"e","price":80,"stock":1}
]
)
db.product.createIndex({
   price:1},{
   background:true})
db.product.createIndex({
   stock:1},{
   background:true})

//可以使用交叉索引
db.product.find({"price":20,"stock":8}).explain();
//不能使用交叉索引
db.product.find({"price":20}).sort({"stock":1}).explain();

//覆盖索引
db.product.drop();
db.product.insertMany
(
[
{"name":"a","price":60,"stock":10},
{"name":"b","price":100,"stock":2},
{"name":"c","price":20,"stock":8},
{"name":"d","price":40,"stock":7},
{"name":"e","price":80,"stock":1}
]
);
db.product.createIndex
({
   price:1,stock:1},{
   background:true});
//走覆盖索引
db.product.find({price:20},{stock:1,_id:0}).explain("executionStats");
//不走覆盖索引
db.product.find({price:20},{stock:1}).explain("executionStats");

//过期索引
db.product.drop();
db.product.createIndex
(
   {createDate: 1},{expireAfterSeconds: 10},{background:true}
);
db.product.insertMany
(
[
{"name":"a","createDate":new Date("2022-05-11")},
{"name":"b","createDate":new Date("2022-05-11")},
{"name":"c","createDate":new Date("2022-05-11")},
{"name":"d","createDate":new Date("2022-05-11")},
{"name":"e","createDate":new Date("2022-05-11")}
]
);

db.product.createIndex
({"expireDate": 1},{expireAfterSeconds: 0})
db.product.insertOne(
{"name":"f","expireDate":new Date("2022-05-14T00:00:00.000Z")}
);
db.product.find
();