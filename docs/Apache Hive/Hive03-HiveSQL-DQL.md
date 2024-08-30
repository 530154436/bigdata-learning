## 一、Select查询
+ 语法树：
```sql
[WITH CommonTableExpression (, CommonTableExpression)*] 
SELECT [ALL | DISTINCT] select_expr, select_expr, ...
  FROM table_reference
  [WHERE where_condition]
  [GROUP BY col_list]
  [ORDER BY col_list]
  [CLUSTER BY col_list
    | [DISTRIBUTE BY col_list] [SORT BY col_list]
  ]
 [LIMIT [offset,] rows]
```
（1）`table_reference`：查询的输入，可以是普通物理表、视图、join查询结果或子查询结果。
（2）`表名`和`列名`不区分大小写。

+ 案例：


### 1.1 select_expr
### 1.2 ALL 、DISTINCT
### 1.3 WHERE
### 1.4 分区查询、分区裁剪
### 1.5 HAVING
### 1.6 LIMIT
### 1.7 ORDER BY
### 1.8 CLUSTER BY
### 1.9 DISTRIBUTE BY +SORT BY
### 1.10 Union联合查询
### 1.11 Subqueries子查询
### 1.12 where子句中子查询
### 1.13 HiveSQL查询执行顺序

## 二、Join连接查询
