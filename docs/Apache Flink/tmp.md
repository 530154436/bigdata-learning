#### 1.6.1 Time（时间）
Flink 明确支持以下三种时间语义:
事件时间(event time)： 事件产生的时间，记录的是设备生产(或者存储)事件的时间<br>
摄取时间(ingestion time)： Flink 读取事件时记录的时间<br>
处理时间(processing time)： Flink pipeline 中具体算子处理事件的时间<br>
#### 1.6.2 Window（窗口）
#### 1.6.3 State（状态）
#### 1.6.4 Checkpoint（检查点）