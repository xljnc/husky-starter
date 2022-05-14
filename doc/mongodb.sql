//交叉索引
db.product.insertMany{
[
{"name":"a","price":60,"stock":10},
{"name":"b","price":100,"stock":2},
{"name":"c","price":20,"stock":8},
{"name":"d","price":40,"stock":7},
{"name":"e","price":80,"stock":1}
]
}
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

