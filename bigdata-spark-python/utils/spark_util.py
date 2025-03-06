#!/usr/bin/env python
# -*- coding:utf-8 -*-
# @author: zhengchubin
# @time: 2025/3/3 14:27
# @function:
import traceback
from typing import List
from pyspark.sql import SparkSession


def get_partitions(spark_session: SparkSession, tlb_name: str,
                   reverse: bool = True, limit: int = 3) -> List[str]:
    """
    获取最新的前N个分区
    """
    partitions = []
    try:
        sql_1 = f"""SHOW PARTITIONS {tlb_name}"""
        partitions = list(spark_session.sql(sql_1).toPandas().get("partition").values)
        partitions = list(map(lambda x: str(x.split("=")[1]), partitions))
        partitions.sort(key=lambda x: x, reverse=reverse)
    except Exception as e:
        print(f"分区表不存在: {tlb_name}")
        print(traceback.format_exc(limit=5))
    return partitions[: limit]
