<nav>
<a href="#一什么是-copy_to">一、什么是 `copy_to`</a><br/>
<a href="#二场景区域信息聚合查询">二、场景：区域信息聚合查询</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;<a href="#21-创建索引并定义-mapping">2.1 创建索引并定义 mapping</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;<a href="#22-批量导入测试数据">2.2 批量导入测试数据</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;<a href="#23-简化查询">2.3 简化查询</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;<a href="#24-查看copy_to的内容">2.4 查看copy_to的内容</a><br/>
<a href="#参考">参考</a><br/>
</nav>

## 一、什么是 `copy_to`

Elasticsearch 的 **`copy_to`** 是一个映射（mapping）参数，用于将多个字段的原始值（**不是分析后的 term**）复制到一个目标字段中。该目标字段可以作为一个聚合字段进行查询，从而简化搜索逻辑并提升性能。

> **适用场景**：需要在多个相关字段上执行联合查询时（如姓名、地址、区域等），使用 `copy_to` 可以减少查询字段数量，提高搜索效率。

`copy_to` 复制的是字段的原始值（即 `_source` 中的值），而不是经过 analyzer 分词后的 term。目标字段会独立进行自己的分析流程。
+ `_source` 字段不会包含 `copy_to` 生成的字段内容。
+ 可通过数组形式复制到多个字段：`"copy_to": ["field1", "field2"]`
+ 不能通过中间字段间接复制（如 A → B → C 不生效）。必须从源字段直接复制到最终字段。
+ 不支持如`date_range`、`object` 等复杂类型。

> **注意**：`copy_to` 的目标字段必须在 mapping 中显式定义（或通过动态映射自动创建）。

## 二、场景：区域信息聚合查询

文档包含以下相关字段：
```json
{
  "city": "深圳市",
  "province": "广东省",
  "country": "中国"
}
```
频繁需要同时搜索这三个字段（如“中国 北京”），传统方式需使用 `bool + should`，较繁琐。
**解决方案：使用 `copy_to` 创建 `region` 字段**

### 2.1 创建索引并定义 mapping
```
PUT algo_test_copy_to
{
  "mappings": {
    "properties": {
      "city": {
        "type": "keyword",
        "copy_to": "region"
      },
      "province": {
        "type": "keyword",
        "copy_to": "region"
      },
      "country": {
        "type": "keyword",
        "copy_to": "region"
      },
      "region": {
        "type": "text",
        "store": true
      }
    }
  }
}
```

### 2.2 批量导入测试数据
```
POST _bulk
{ "index" : { "_index" : "algo_test_copy_to", "_id": 1} }
{"city":"北京市", "province":"北京市", "country":"中国"}
{ "index" : { "_index" : "algo_test_copy_to", "_id": 2} }
{"city":"上海市", "province":"上海市", "country":"中国"}
{ "index" : { "_index" : "algo_test_copy_to", "_id": 2} }
{"city":"深圳市", "province":"深圳市", "country":"中国"}
// ... 其他文档
```

### 2.3 简化查询
只需查询 `region` 字段：
```
GET algo_test_copy_to/_search
{
  "query": {
    "match": {
      "region": {
        "query": "中国 深圳市",
        "operator": "and"
      }
    }
  }
}
```
返回所有 `country=中国` 且 `city/province=深圳市` 的文档。
```json5
{
  "took" : 3,
  "timed_out" : false,
  "hits" : {
    "total" : 1,
    "max_score" : 2.3622525,
    "hits" : [
      {
        "_index" : "algo_test_copy_to",
        "_type" : "_doc",
        "_id" : "3",
        "_score" : 2.3622525,
        "_source" : {
          "city" : "深圳市",
          "province" : "广东省",
          "country" : "中国"
        }
      }
    ]
  }
}
```
### 2.4 查看copy_to的内容
由于 `copy_to` 字段不在 `_source` 中，若想查看其实际存储的值，需设置 `"store": true` 并使用 `stored_fields` 查询：
```
GET algo_test_copy_to/_doc/3?stored_fields=region
```
返回结果：
```json
{
  "_index" : "algo_test_copy_to",
  "_type" : "_doc",
  "_id" : "3",
  "_version" : 1,
  "_seq_no" : 2,
  "_primary_term" : 1,
  "found" : true,
  "fields" : {
    "region" : [
      "深圳市",
      "广东省",
      "中国"
    ]
  }
}
```
> 注意：顺序与 `copy_to` 定义顺序一致。

## 参考
[1] [elasticsearch copy-to](https://www.elastic.co/docs/reference/elasticsearch/mapping-reference/copy-to)<br>
[2] [使用Elasticsearch中的copy_to来提高搜索效率](https://www.cnblogs.com/hahaha111122222/p/12085057.html)<br>
