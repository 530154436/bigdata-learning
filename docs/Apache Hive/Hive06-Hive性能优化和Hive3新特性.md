## 


### IN 和 EXISTS

Hive SQL中的`EXISTS子`句用于检查一个子查询是否至少返回一行结果。<br>

##### 使用EXISTS而不是IN
当子查询返回大量数据时，使用EXISTS通常比IN更高效。因为IN需要将所有匹配的行从子查询结果集中返回给主查询，而EXISTS只需要找到一个匹配的行即可。 例如，将以下查询：
```
SELECT * FROM table1 WHERE column1 IN (SELECT column2 FROM table2);
```
改为：
```
SELECT * FROM table1 WHERE EXISTS (SELECT 1 FROM table2 WHERE table1.column1 = table2.column2);
```
##### 使用NOT EXISTS而不是NOT IN
如果需要查询那些在子查询中没有匹配项的行，可以使用NOT EXISTS。这通常比使用NOT IN更高效，因为NOT IN需要返回所有不在子查询结果集中的行，而`NOT EXIST`S只需要找到一个不匹配的行即可。 例如，将以下查询：
```
SELECT * FROM table1 WHERE column1 NOT IN (SELECT column2 FROM table2);
```
改为：
```
SELECT * FROM table1 WHERE NOT EXISTS (SELECT 1 FROM table2 WHERE table1.column1 = table2.column2);
```
