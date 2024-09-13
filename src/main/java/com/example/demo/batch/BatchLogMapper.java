package com.example.demo.batch;

import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BatchLogMapper {


    void insertBatchLog(BatchLog batchLog);

    BatchLog findBatchLogById(Long id);

    void updateBatchLog(BatchLog batchLog);

    List<BatchLog> findAllBatchLogs();
}
